package sh.haven.app.widget

import android.util.Log
import sh.haven.app.agent.HeadlessSshExec
import sh.haven.core.data.repository.ConnectionRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Collects hardware resource statistics (CPU, Memory, Disk, Load, Uptime)
 * from a remote SSH host using [HeadlessSshExec].
 *
 * Uses lightweight POSIX/Linux standard files (/proc/stat, /proc/meminfo,
 * /proc/loadavg, /proc/uptime, df) — requires zero extra daemon installed
 * on the remote server.
 */
@Singleton
class ServerMetricsCollector @Inject constructor(
    private val headlessSshExec: HeadlessSshExec,
    private val connectionRepository: ConnectionRepository,
) {
    companion object {
        private const val TAG = "ServerMetricsCollector"
        private const val TIMEOUT_MS = 12_000L

        // Dual-sample /proc/stat for accurate delta CPU percentage + memory + disk + uptime
        val PROBE_COMMAND = """
            cpu1=$(grep '^cpu ' /proc/stat 2>/dev/null);
            sleep 0.5;
            cpu2=$(grep '^cpu ' /proc/stat 2>/dev/null);
            echo "===CPU===";
            echo "$cpu1";
            echo "$cpu2";
            echo "===MEM===";
            grep -E '^(MemTotal|MemAvailable|MemFree|Buffers|Cached):' /proc/meminfo 2>/dev/null;
            echo "===DISK===";
            df -k / 2>/dev/null | tail -n 1;
            echo "===LOAD===";
            cat /proc/loadavg 2>/dev/null;
            echo "===UPTIME===";
            cat /proc/uptime 2>/dev/null
        """.trimIndent().replace("\n", " ")
    }

    suspend fun collect(profileId: String): ServerMetrics {
        val profile = connectionRepository.getById(profileId)
            ?: return ServerMetrics(
                profileId = profileId,
                profileName = "Unknown Profile",
                host = "",
                isOnline = false,
                errorMessage = "Host profile not found",
            )

        val profileName = profile.label.ifBlank { profile.host }
        val host = profile.host

        return try {
            val outcome = headlessSshExec.run(profileId, PROBE_COMMAND, TIMEOUT_MS)
            val exec = outcome.exec

            if (exec.timedOut) {
                return ServerMetrics(
                    profileId = profileId,
                    profileName = profileName,
                    host = host,
                    isOnline = false,
                    errorMessage = "Connection timed out",
                )
            }

            parseMetrics(profileId, profileName, host, exec.stdout)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to collect metrics: ${e.message}")
            ServerMetrics(
                profileId = profileId,
                profileName = profileName,
                host = host,
                isOnline = false,
                errorMessage = e.message ?: "Connection error",
            )
        }
    }

    internal fun parseMetrics(
        profileId: String,
        profileName: String,
        host: String,
        output: String,
    ): ServerMetrics {
        var cpuPercent = 0
        var ramUsedMb = 0L
        var ramTotalMb = 0L
        var ramPercent = 0
        var diskUsedGb = 0f
        var diskTotalGb = 0f
        var diskPercent = 0
        var load1m = 0f
        var uptimeStr = ""

        val sections = output.split("===")
        for (i in 1 until sections.size step 2) {
            val sectionName = sections[i].trim()
            val sectionContent = if (i + 1 < sections.size) sections[i + 1].trim() else ""

            when (sectionName) {
                "CPU" -> {
                    val lines = sectionContent.lines().filter { it.startsWith("cpu ") }
                    if (lines.size >= 2) {
                        cpuPercent = calculateCpuPercent(lines[0], lines[1])
                    }
                }
                "MEM" -> {
                    val memMap = mutableMapOf<String, Long>()
                    sectionContent.lines().forEach { line ->
                        val parts = line.split(":")
                        if (parts.size >= 2) {
                            val key = parts[0].trim()
                            val valueKb = parts[1].trim().split(" ").firstOrNull()?.toLongOrNull() ?: 0L
                            memMap[key] = valueKb
                        }
                    }
                    val totalKb = memMap["MemTotal"] ?: 0L
                    val availKb = memMap["MemAvailable"]
                        ?: ((memMap["MemFree"] ?: 0L) + (memMap["Buffers"] ?: 0L) + (memMap["Cached"] ?: 0L))
                    val usedKb = (totalKb - availKb).coerceAtLeast(0L)

                    if (totalKb > 0) {
                        ramPercent = ((usedKb * 100) / totalKb).toInt().coerceIn(0, 100)
                        ramUsedMb = usedKb / 1024L
                        ramTotalMb = totalKb / 1024L
                    }
                }
                "DISK" -> {
                    // Filesystem 1K-blocks Used Available Use% Mounted on
                    val tokens = sectionContent.split("\\s+".toRegex())
                    if (tokens.size >= 5) {
                        val totalKb = tokens[1].toLongOrNull() ?: 0L
                        val usedKb = tokens[2].toLongOrNull() ?: 0L
                        val percentStr = tokens[4].replace("%", "").trim()
                        diskPercent = percentStr.toIntOrNull()?.coerceIn(0, 100) ?: 0
                        diskTotalGb = totalKb.toFloat() / (1024f * 1024f)
                        diskUsedGb = usedKb.toFloat() / (1024f * 1024f)
                    }
                }
                "LOAD" -> {
                    val tokens = sectionContent.split("\\s+".toRegex())
                    if (tokens.isNotEmpty()) {
                        load1m = tokens[0].toFloatOrNull() ?: 0f
                    }
                }
                "UPTIME" -> {
                    val tokens = sectionContent.split("\\s+".toRegex())
                    val seconds = tokens.firstOrNull()?.toDoubleOrNull()?.toLong() ?: 0L
                    uptimeStr = formatUptime(seconds)
                }
            }
        }

        return ServerMetrics(
            profileId = profileId,
            profileName = profileName,
            host = host,
            isOnline = true,
            cpuPercent = cpuPercent,
            ramUsedMb = ramUsedMb,
            ramTotalMb = ramTotalMb,
            ramPercent = ramPercent,
            diskUsedGb = diskUsedGb,
            diskTotalGb = diskTotalGb,
            diskPercent = diskPercent,
            load1m = load1m,
            uptimeStr = uptimeStr,
            errorMessage = null,
        )
    }

    private fun calculateCpuPercent(sample1: String, sample2: String): Int {
        fun parseFields(line: String): LongArray {
            return line.removePrefix("cpu")
                .trim()
                .split("\\s+".toRegex())
                .mapNotNull { it.toLongOrNull() }
                .toLongArray()
        }

        val f1 = parseFields(sample1)
        val f2 = parseFields(sample2)
        if (f1.size < 4 || f2.size < 4) return 0

        val total1 = f1.sum()
        val idle1 = f1[3] + (f1.getOrNull(4) ?: 0L)

        val total2 = f2.sum()
        val idle2 = f2[3] + (f2.getOrNull(4) ?: 0L)

        val diffTotal = total2 - total1
        val diffIdle = idle2 - idle1

        return if (diffTotal > 0) {
            val busy = (diffTotal - diffIdle).coerceAtLeast(0L)
            ((busy * 100) / diffTotal).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }

    private fun formatUptime(seconds: Long): String {
        if (seconds <= 0) return ""
        val days = seconds / 86400
        val hours = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60
        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
}
