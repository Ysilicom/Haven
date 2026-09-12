package sh.haven.app.widget

/**
 * Snapshot of hardware status on a remote SSH host, shaped for
 * the ServerBox-style desktop AppWidget display.
 */
data class ServerMetrics(
    val profileId: String,
    val profileName: String,
    val host: String,
    val isOnline: Boolean,
    val cpuPercent: Int = 0,
    val ramUsedMb: Long = 0,
    val ramTotalMb: Long = 0,
    val ramPercent: Int = 0,
    val diskUsedGb: Float = 0f,
    val diskTotalGb: Float = 0f,
    val diskPercent: Int = 0,
    val load1m: Float = 0f,
    val uptimeStr: String = "",
    val errorMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    val formattedRamText: String
        get() {
            val usedG = ramUsedMb.toFloat() / 1024f
            val totalG = ramTotalMb.toFloat() / 1024f
            return if (totalG >= 1.0f) {
                String.format("%.1fG / %.1fG (%d%%)", usedG, totalG, ramPercent)
            } else {
                "${ramUsedMb}M / ${ramTotalMb}M ($ramPercent%)"
            }
        }

    val formattedDiskText: String
        get() = String.format("%.1fG / %.1fG (%d%%)", diskUsedGb, diskTotalGb, diskPercent)

    val formattedCpuText: String
        get() = "$cpuPercent%"

    val formattedLoadAndUptime: String
        get() {
            val parts = mutableListOf<String>()
            if (load1m > 0f) {
                parts.add(String.format("Load: %.2f", load1m))
            }
            if (uptimeStr.isNotEmpty()) {
                parts.add("Up: $uptimeStr")
            }
            return if (parts.isNotEmpty()) parts.joinToString("  |  ") else "Online"
        }
}
