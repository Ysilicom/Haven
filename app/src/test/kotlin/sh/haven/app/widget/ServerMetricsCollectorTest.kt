package sh.haven.app.widget

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import sh.haven.app.agent.HeadlessSshExec
import sh.haven.core.data.repository.ConnectionRepository

class ServerMetricsCollectorTest {

    private val collector = ServerMetricsCollector(
        headlessSshExec = mockk(relaxed = true),
        connectionRepository = mockk(relaxed = true),
    )

    @Test
    fun testParseMetricsStandardLinuxOutput() {
        val sampleOutput = """
            ===CPU===
            cpu  1000 0 500 8500 0 0 0 0 0 0
            cpu  1050 0 550 8600 0 0 0 0 0 0
            ===MEM===
            MemTotal:        8000000 kB
            MemFree:         1000000 kB
            MemAvailable:    4000000 kB
            Buffers:          200000 kB
            Cached:          1800000 kB
            ===DISK===
            /dev/sda1 104857600 41943040 62914560   40% /
            ===LOAD===
            1.25 0.85 0.50 2/250 12345
            ===UPTIME===
            259200.00 123456.00
        """.trimIndent()

        val metrics = collector.parseMetrics(
            profileId = "test-id",
            profileName = "My VPS",
            host = "1.2.3.4",
            output = sampleOutput,
        )

        assertTrue(metrics.isOnline)
        assertEquals("My VPS", metrics.profileName)
        assertEquals("1.2.3.4", metrics.host)

        // CPU:
        // sample1: total = 10000, idle = 8500
        // sample2: total = 10200, idle = 8600
        // diffTotal = 200, diffIdle = 100
        // cpuPercent = (100 * 100) / 200 = 50%
        assertEquals(50, metrics.cpuPercent)

        // MEM:
        // total: 8000000 kB (~7812 MB)
        // avail: 4000000 kB
        // used: 4000000 kB (~3906 MB)
        // pct: 50%
        assertEquals(50, metrics.ramPercent)

        // DISK:
        // total: 104857600 kB = 100 GB
        // used: 41943040 kB = 40 GB
        // pct: 40%
        assertEquals(40, metrics.diskPercent)

        // LOAD:
        assertEquals(1.25f, metrics.load1m, 0.01f)

        // UPTIME:
        // 259200 s = 3 days
        assertEquals("3d 0h", metrics.uptimeStr)
    }

    @Test
    fun testFormattedStrings() {
        val metrics = ServerMetrics(
            profileId = "test-id",
            profileName = "Server",
            host = "example.com",
            isOnline = true,
            cpuPercent = 25,
            ramUsedMb = 2048,
            ramTotalMb = 8192,
            ramPercent = 25,
            diskUsedGb = 30.5f,
            diskTotalGb = 100.0f,
            diskPercent = 30,
            load1m = 0.75f,
            uptimeStr = "5d 12h",
        )

        assertEquals("25%", metrics.formattedCpuText)
        assertEquals("2.0G / 8.0G (25%)", metrics.formattedRamText)
        assertEquals("30.5G / 100.0G (30%)", metrics.formattedDiskText)
        assertEquals("Load: 0.75  |  Up: 5d 12h", metrics.formattedLoadAndUptime)
    }
}
