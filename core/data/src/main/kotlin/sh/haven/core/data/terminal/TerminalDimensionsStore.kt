package sh.haven.core.data.terminal

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import sh.haven.core.data.preferences.UserPreferencesRepository
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

/**
 * Stores and computes terminal grid dimensions (columns and rows) so that
 * SSH/local/serial PTY channels and terminal emulators can be initialized
 * with the exact device viewport dimensions *before* opening the channel.
 *
 * Modeled after Termius's pre-measurement architecture:
 * Termius measures the screen display and font metrics before requesting
 * a PTY channel, passing the real columns/rows in pty-req. This prevents
 * the initial 80x24 MOTD wrap / prompt interleaving glitch that occurs
 * when the server starts at 80 cols and is immediately resized via SIGWINCH.
 */
@Singleton
class TerminalDimensionsStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: UserPreferencesRepository,
) {
    data class Dimensions(val columns: Int, val rows: Int)

    private val lastMeasured = AtomicReference<Dimensions?>(null)

    /**
     * Update the store with live measured dimensions from a rendered terminal view.
     */
    fun updateMeasured(columns: Int, rows: Int) {
        if (columns >= MIN_COLUMNS && rows >= MIN_ROWS) {
            lastMeasured.set(Dimensions(columns, rows))
        }
    }

    /**
     * Get the best dimensions for initializing a new PTY or emulator:
     * 1. Returns last measured dimensions if available.
     * 2. Otherwise calculates dimensions from the device's DisplayMetrics and font size.
     */
    fun getDimensions(): Dimensions {
        lastMeasured.get()?.let { return it }
        return computeDefaultDimensions()
    }

    /**
     * Pre-computes initial columns and rows based on display metrics and terminal font size.
     * Uses sample string measurement for sub-pixel accuracy across columns (Termius technique).
     */
    fun computeDefaultDimensions(): Dimensions {
        return try {
            val metrics = context.resources.displayMetrics
            val widthPx = metrics.widthPixels
            val heightPx = metrics.heightPixels

            if (widthPx <= 0 || heightPx <= 0) {
                return FALLBACK_DIMENSIONS
            }

            val fontSizeSp = try {
                runBlocking { preferencesRepository.terminalFontSize.first() }
            } catch (_: Exception) {
                UserPreferencesRepository.DEFAULT_FONT_SIZE
            }

            val textPaint = Paint().apply {
                typeface = Typeface.MONOSPACE
                textSize = fontSizeSp * metrics.scaledDensity
                isAntiAlias = true
            }

            // High-precision character width measurement over a multi-character sample string,
            // preventing accumulated sub-pixel rounding errors over 40-80 columns.
            val sample = "The_string_of_text_of_big_length_enough_to_properly_measure_size"
            val totalWidth = textPaint.measureText(sample)
            val charWidth = if (totalWidth > 0f) (totalWidth / sample.length) else textPaint.measureText("M")

            val fontMetrics = textPaint.fontMetrics
            val rawCharHeight = ceil(fontMetrics.descent - fontMetrics.ascent)
            val charHeight = if (rawCharHeight > 0f) rawCharHeight else 20f

            // Account for system status bar, action bar/tab bar, and terminal toolbar (approx 130dp)
            val overheadPx = 130f * metrics.density
            val availableHeightPx = (heightPx - overheadPx).coerceAtLeast(heightPx * 0.5f)

            val cols = (widthPx / charWidth).toInt().coerceIn(MIN_COLUMNS, MAX_COLUMNS)
            val rows = (availableHeightPx / charHeight).toInt().coerceIn(MIN_ROWS, MAX_ROWS)

            Dimensions(columns = cols, rows = rows)
        } catch (_: Exception) {
            FALLBACK_DIMENSIONS
        }
    }

    companion object {
        const val MIN_COLUMNS = 20
        const val MAX_COLUMNS = 300
        const val MIN_ROWS = 10
        const val MAX_ROWS = 150

        val FALLBACK_DIMENSIONS = Dimensions(columns = 80, rows = 24)
    }
}
