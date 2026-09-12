package sh.haven.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import sh.haven.app.MainActivity
import sh.haven.app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * ServerBox-style desktop AppWidget displaying real-time hardware status
 * (CPU, RAM, Disk, Load, Uptime) for a chosen Haven SSH host.
 */
class ServerMonitorWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH_WIDGET = "sh.haven.app.widget.ACTION_REFRESH_WIDGET"
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun buildRemoteViews(
            context: Context,
            appWidgetId: Int,
            metrics: ServerMetrics?,
            profileName: String,
            host: String,
            isRefreshing: Boolean = false,
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_server_monitor)

            // Basic Header Info
            views.setTextViewText(R.id.widget_server_name, profileName.ifBlank { "Server" })

            if (isRefreshing) {
                views.setTextViewText(R.id.widget_server_host, "Refreshing...")
            } else {
                views.setTextViewText(R.id.widget_server_host, host.ifBlank { "SSH Host" })
            }

            if (metrics != null) {
                // Status Dot
                val statusDotRes = if (metrics.isOnline) {
                    R.drawable.widget_status_online
                } else {
                    R.drawable.widget_status_offline
                }
                views.setImageViewResource(R.id.widget_status_dot, statusDotRes)

                // Timestamp
                val updatedTime = timeFormat.format(Date(metrics.timestamp))
                views.setTextViewText(R.id.widget_last_updated, updatedTime)

                if (metrics.isOnline) {
                    // CPU
                    views.setProgressBar(R.id.widget_cpu_progress, 100, metrics.cpuPercent, false)
                    views.setTextViewText(R.id.widget_cpu_text, metrics.formattedCpuText)

                    // RAM
                    views.setProgressBar(R.id.widget_ram_progress, 100, metrics.ramPercent, false)
                    views.setTextViewText(R.id.widget_ram_text, metrics.formattedRamText)

                    // Disk
                    views.setProgressBar(R.id.widget_disk_progress, 100, metrics.diskPercent, false)
                    views.setTextViewText(R.id.widget_disk_text, metrics.formattedDiskText)

                    // Footer (Load & Uptime)
                    views.setTextViewText(R.id.widget_footer_text, metrics.formattedLoadAndUptime)
                } else {
                    // Offline display
                    views.setProgressBar(R.id.widget_cpu_progress, 100, 0, false)
                    views.setTextViewText(R.id.widget_cpu_text, "--")
                    views.setProgressBar(R.id.widget_ram_progress, 100, 0, false)
                    views.setTextViewText(R.id.widget_ram_text, "--")
                    views.setProgressBar(R.id.widget_disk_progress, 100, 0, false)
                    views.setTextViewText(R.id.widget_disk_text, "--")

                    val err = metrics.errorMessage ?: "Host unreachable"
                    views.setTextViewText(R.id.widget_footer_text, "Offline: $err")
                }
            } else {
                views.setImageViewResource(R.id.widget_status_dot, R.drawable.widget_status_offline)
                views.setTextViewText(R.id.widget_last_updated, "--:--")
                views.setTextViewText(R.id.widget_footer_text, "Tap refresh to connect")
            }

            // Click Refresh button
            val refreshIntent = Intent(context, ServerMonitorWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)

            // Click entire card: open Haven MainActivity
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (metrics != null) {
                    putExtra("profile_id", metrics.profileId)
                }
            }
            val launchPendingIntent = PendingIntent.getActivity(
                context,
                1000 + appWidgetId,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.widget_root, launchPendingIntent)

            return views
        }

        fun schedulePeriodicUpdates(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<ServerWidgetWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                ServerWidgetWorker.UNIQUE_PERIODIC_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest,
            )
        }

        fun triggerImmediateRefresh(context: Context, appWidgetId: Int) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<ServerWidgetWorker>()
                .setConstraints(constraints)
                .setInputData(workDataOf(ServerWidgetWorker.KEY_APP_WIDGET_ID to appWidgetId))
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val widgetPrefs = ServerWidgetPreferences(context)
        for (appWidgetId in appWidgetIds) {
            val cached = widgetPrefs.getCachedMetrics(appWidgetId)
            val profileId = widgetPrefs.getProfileId(appWidgetId)
            if (profileId != null) {
                val views = buildRemoteViews(
                    context = context,
                    appWidgetId = appWidgetId,
                    metrics = cached,
                    profileName = cached?.profileName ?: "Server",
                    host = cached?.host ?: "Connecting...",
                )
                appWidgetManager.updateAppWidget(appWidgetId, views)
                triggerImmediateRefresh(context, appWidgetId)
            }
        }
        schedulePeriodicUpdates(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID,
            )
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val widgetPrefs = ServerWidgetPreferences(context)
                val cached = widgetPrefs.getCachedMetrics(appWidgetId)
                val appWidgetManager = AppWidgetManager.getInstance(context)

                // Show "Refreshing..." immediately in UI
                val views = buildRemoteViews(
                    context = context,
                    appWidgetId = appWidgetId,
                    metrics = cached,
                    profileName = cached?.profileName ?: "Server",
                    host = cached?.host ?: "",
                    isRefreshing = true,
                )
                appWidgetManager.updateAppWidget(appWidgetId, views)

                triggerImmediateRefresh(context, appWidgetId)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val widgetPrefs = ServerWidgetPreferences(context)
        for (appWidgetId in appWidgetIds) {
            widgetPrefs.removeWidgetConfig(appWidgetId)
        }
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ServerWidgetWorker.UNIQUE_PERIODIC_WORK)
    }
}
