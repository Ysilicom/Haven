package sh.haven.app.widget

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages SharedPreferences storage for desktop Server Monitor AppWidgets.
 * Associates each [appWidgetId] with its selected [ConnectionProfile.id]
 * and caches the latest metrics snapshot for instant rendering.
 */
@Singleton
class ServerWidgetPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val PREFS_NAME = "sh.haven.app.widget.server_monitor"
        private const val KEY_PROFILE_ID_PREFIX = "widget_profile_"
        private const val KEY_METRICS_PREFIX = "widget_metrics_"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveWidgetConfig(appWidgetId: Int, profileId: String) {
        prefs.edit()
            .putString(KEY_PROFILE_ID_PREFIX + appWidgetId, profileId)
            .apply()
    }

    fun getProfileId(appWidgetId: Int): String? {
        return prefs.getString(KEY_PROFILE_ID_PREFIX + appWidgetId, null)
    }

    fun removeWidgetConfig(appWidgetId: Int) {
        prefs.edit()
            .remove(KEY_PROFILE_ID_PREFIX + appWidgetId)
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_cpu")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_ram_used")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_ram_total")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_ram_pct")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_disk_used")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_disk_total")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_disk_pct")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_load")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_uptime")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_online")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_error")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_time")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_name")
            .remove(KEY_METRICS_PREFIX + appWidgetId + "_host")
            .apply()
    }

    fun saveCachedMetrics(appWidgetId: Int, metrics: ServerMetrics) {
        prefs.edit()
            .putInt(KEY_METRICS_PREFIX + appWidgetId + "_cpu", metrics.cpuPercent)
            .putLong(KEY_METRICS_PREFIX + appWidgetId + "_ram_used", metrics.ramUsedMb)
            .putLong(KEY_METRICS_PREFIX + appWidgetId + "_ram_total", metrics.ramTotalMb)
            .putInt(KEY_METRICS_PREFIX + appWidgetId + "_ram_pct", metrics.ramPercent)
            .putFloat(KEY_METRICS_PREFIX + appWidgetId + "_disk_used", metrics.diskUsedGb)
            .putFloat(KEY_METRICS_PREFIX + appWidgetId + "_disk_total", metrics.diskTotalGb)
            .putInt(KEY_METRICS_PREFIX + appWidgetId + "_disk_pct", metrics.diskPercent)
            .putFloat(KEY_METRICS_PREFIX + appWidgetId + "_load", metrics.load1m)
            .putString(KEY_METRICS_PREFIX + appWidgetId + "_uptime", metrics.uptimeStr)
            .putBoolean(KEY_METRICS_PREFIX + appWidgetId + "_online", metrics.isOnline)
            .putString(KEY_METRICS_PREFIX + appWidgetId + "_error", metrics.errorMessage)
            .putLong(KEY_METRICS_PREFIX + appWidgetId + "_time", metrics.timestamp)
            .putString(KEY_METRICS_PREFIX + appWidgetId + "_name", metrics.profileName)
            .putString(KEY_METRICS_PREFIX + appWidgetId + "_host", metrics.host)
            .apply()
    }

    fun getCachedMetrics(appWidgetId: Int): ServerMetrics? {
        val profileId = getProfileId(appWidgetId) ?: return null
        val time = prefs.getLong(KEY_METRICS_PREFIX + appWidgetId + "_time", 0L)
        if (time == 0L) return null

        return ServerMetrics(
            profileId = profileId,
            profileName = prefs.getString(KEY_METRICS_PREFIX + appWidgetId + "_name", "") ?: "",
            host = prefs.getString(KEY_METRICS_PREFIX + appWidgetId + "_host", "") ?: "",
            isOnline = prefs.getBoolean(KEY_METRICS_PREFIX + appWidgetId + "_online", false),
            cpuPercent = prefs.getInt(KEY_METRICS_PREFIX + appWidgetId + "_cpu", 0),
            ramUsedMb = prefs.getLong(KEY_METRICS_PREFIX + appWidgetId + "_ram_used", 0L),
            ramTotalMb = prefs.getLong(KEY_METRICS_PREFIX + appWidgetId + "_ram_total", 0L),
            ramPercent = prefs.getInt(KEY_METRICS_PREFIX + appWidgetId + "_ram_pct", 0),
            diskUsedGb = prefs.getFloat(KEY_METRICS_PREFIX + appWidgetId + "_disk_used", 0f),
            diskTotalGb = prefs.getFloat(KEY_METRICS_PREFIX + appWidgetId + "_disk_total", 0f),
            diskPercent = prefs.getInt(KEY_METRICS_PREFIX + appWidgetId + "_disk_pct", 0),
            load1m = prefs.getFloat(KEY_METRICS_PREFIX + appWidgetId + "_load", 0f),
            uptimeStr = prefs.getString(KEY_METRICS_PREFIX + appWidgetId + "_uptime", "") ?: "",
            errorMessage = prefs.getString(KEY_METRICS_PREFIX + appWidgetId + "_error", null),
            timestamp = time,
        )
    }

    fun getAllConfiguredWidgetIds(): List<Int> {
        return prefs.all.keys
            .filter { it.startsWith(KEY_PROFILE_ID_PREFIX) }
            .mapNotNull { it.removePrefix(KEY_PROFILE_ID_PREFIX).toIntOrNull() }
    }
}
