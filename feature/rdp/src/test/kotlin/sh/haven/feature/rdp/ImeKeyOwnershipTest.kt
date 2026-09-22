package sh.haven.feature.rdp

import androidx.compose.ui.input.key.Key
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImeKeyOwnershipTest {
    @Test
    fun `composition keys and backspace belong to the text field`() {
        assertTrue(imeOwnsRdpKey(Key.I))
        assertTrue(imeOwnsRdpKey(Key.N))
        assertTrue(imeOwnsRdpKey(Key.Spacebar))
        assertTrue(imeOwnsRdpKey(Key.Backspace))
    }

    @Test
    fun `navigation and modifiers stay on the scancode path`() {
        assertFalse(imeOwnsRdpKey(Key.DirectionLeft))
        assertFalse(imeOwnsRdpKey(Key.Enter))
        assertFalse(imeOwnsRdpKey(Key.CtrlLeft))
        assertFalse(imeOwnsRdpKey(Key.Tab))
    }
}
