package sh.haven.core.data.terminal

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.util.DisplayMetrics
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import sh.haven.core.data.preferences.UserPreferencesRepository

class TerminalDimensionsStoreTest {

    private fun createStore(
        isFullscreen: Boolean,
        widthPx: Int = 1080,
        heightPx: Int = 2400,
        density: Float = 2.625f,
    ): TerminalDimensionsStore {
        val displayMetrics = DisplayMetrics().apply {
            this.widthPixels = widthPx
            this.heightPixels = heightPx
            this.density = density
            this.scaledDensity = density
        }
        val resources = mockk<Resources> {
            every { this@mockk.displayMetrics } returns displayMetrics
        }
        val context = mockk<Context> {
            every { this@mockk.resources } returns resources
        }
        val prefs = mockk<UserPreferencesRepository> {
            every { terminalFontSize } returns flowOf(14)
            every { autoFullscreenInTerminal } returns flowOf(isFullscreen)
        }
        return TerminalDimensionsStore(context, prefs)
    }

    @Test
    fun testFullscreenYieldsMoreRowsThanWindowed() {
        mockkConstructor(Paint::class)
        every { anyConstructed<Paint>().measureText(any<String>()) } returns 600f
        val mockFontMetrics = Paint.FontMetrics().apply {
            ascent = -20f
            descent = 5f
        }
        every { anyConstructed<Paint>().fontMetrics } returns mockFontMetrics

        val fullscreenStore = createStore(isFullscreen = true)
        val windowedStore = createStore(isFullscreen = false)

        val fsDims = fullscreenStore.computeDefaultDimensions()
        val winDims = windowedStore.computeDefaultDimensions()

        assertEquals("Columns should be identical", fsDims.columns, winDims.columns)
        assertTrue(
            "Fullscreen should yield more rows than windowed mode (got fs=${fsDims.rows}, win=${winDims.rows})",
            fsDims.rows > winDims.rows,
        )
    }

    @Test
    fun testUpdateMeasuredOverridesDefaultCalculation() {
        val store = createStore(isFullscreen = true)
        store.updateMeasured(columns = 120, rows = 45)

        val dims = store.getDimensions()
        assertEquals(120, dims.columns)
        assertEquals(45, dims.rows)
    }

    @Test
    fun testUpdateMeasuredIgnoresInvalidDimensions() {
        val store = createStore(isFullscreen = true)
        val defaultDims = store.getDimensions()

        store.updateMeasured(columns = 5, rows = 5) // Below MIN_COLUMNS / MIN_ROWS
        val dims = store.getDimensions()

        assertEquals(defaultDims.columns, dims.columns)
        assertEquals(defaultDims.rows, dims.rows)
    }
}
