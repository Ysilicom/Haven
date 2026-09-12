package sh.haven.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background worker that fetches remote hardware stats via SSH
 * and refreshes all active [ServerMonitorWidgetProvider] instances.
 */
@HiltWorker
class ServerWidgetWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val metricsCollector: ServerMetricsCollector,
    private val widgetPrefs: ServerWidgetPreferences,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG = "ServerWidgetWorker"
        const val KEY_APP_WIDGET_ID = "appWidgetId"
        const val UNIQUE_PERIODIC_WORK = "haven_server_widget_periodic"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val appWidgetManager = AppWidgetManager.getInstance(appContext) ?: return@withContext Result.success()

        val specificId = inputData.getInt(KEY_APP_WIDGET_ID, -1)
        val targetIds = if (specificId != -1) {
            listOf(specificId)
        } else {
            widgetPrefs.getAllConfiguredWidgetIds()
        }

        if (targetIds.isEmpty()) {
            return@withContext Result.success()
        }

        for (widgetId in targetIds) {
            val profileId = widgetPrefs.getProfileId(widgetId) ?: continue
            try {
                val metrics = metricsCollector.collect(profileId)
                widgetPrefs.saveCachedMetrics(widgetId, metrics)

                val views = ServerMonitorWidgetProvider.buildRemoteViews(
                    context = appContext,
                    appWidgetId = widgetId,
                    metrics = metrics,
                    profileName = metrics.profileName,
                    host = metrics.host,
                )
                appWidgetManager.updateAppWidget(widgetId, views)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update widget $widgetId: ${e.message}")
            }
        }

        Result.success()
    }
}
