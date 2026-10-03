package sh.haven.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Unit test for default fullscreen preference when opening a terminal session.
 */
class AutoFullscreenPrefTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun repo(): UserPreferencesRepository {
        val ds: DataStore<Preferences> = PreferenceDataStoreFactory.create {
            tempFolder.newFile("prefs_${System.nanoTime()}.preferences_pb")
        }
        val mockContext = mockk<Context>(relaxed = true)
        return UserPreferencesRepository(mockContext, ds)
    }

    @Test
    fun defaultIsOn() = runBlocking {
        assertEquals(
            "auto fullscreen must stay on by default",
            true,
            repo().autoFullscreenInTerminal.first(),
        )
    }

    @Test
    fun roundTripsBothWays() = runBlocking {
        val repo = repo()
        for (value in listOf(false, true)) {
            repo.setAutoFullscreenInTerminal(value)
            assertEquals(value, repo.autoFullscreenInTerminal.first())
        }
    }
}
