package sh.haven.core.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TmuxQuickBottomSheet(
    onDismiss: () -> Unit,
    onSendBytes: (ByteArray) -> Unit,
) {
    var showKillConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Terminal,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp),
                )
                Text(
                    text = stringResource(R.string.toolbar_tmux_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.toolbar_tmux_cancel),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Section 1: Session Management
            TmuxSectionHeader(title = stringResource(R.string.toolbar_tmux_section_session))

            TmuxActionItem(
                icon = Icons.Filled.PlayArrow,
                title = stringResource(R.string.toolbar_tmux_attach_session),
                shortcut = stringResource(R.string.toolbar_tmux_attach_session_sub),
                onClick = {
                    onSendBytes("tmux new -A -s main\n".toByteArray(Charsets.UTF_8))
                    onDismiss()
                },
            )

            TmuxActionItem(
                icon = Icons.Filled.ExitToApp,
                title = stringResource(R.string.toolbar_tmux_detach),
                shortcut = stringResource(R.string.toolbar_tmux_detach_sub),
                onClick = {
                    onSendBytes(byteArrayOf(0x02, 'd'.code.toByte()))
                    onDismiss()
                },
            )

            TmuxActionItem(
                icon = Icons.Filled.DeleteForever,
                title = stringResource(R.string.toolbar_tmux_kill_session),
                shortcut = stringResource(R.string.toolbar_tmux_kill_session_sub),
                isDanger = true,
                onClick = {
                    showKillConfirmDialog = true
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )

            // Section 2: Window Management
            TmuxSectionHeader(title = stringResource(R.string.toolbar_tmux_section_window))

            TmuxActionItem(
                icon = Icons.Filled.AddBox,
                title = stringResource(R.string.toolbar_tmux_new_window),
                shortcut = stringResource(R.string.toolbar_tmux_new_window_sub),
                onClick = {
                    onSendBytes(byteArrayOf(0x02, 'c'.code.toByte()))
                    onDismiss()
                },
            )

            TmuxActionItem(
                icon = Icons.Filled.ViewStream,
                title = stringResource(R.string.toolbar_tmux_switch_window),
                shortcut = stringResource(R.string.toolbar_tmux_switch_window_sub),
                onClick = {
                    onSendBytes(byteArrayOf(0x02, 'w'.code.toByte()))
                    onDismiss()
                },
            )

            TmuxActionItem(
                icon = Icons.Filled.Close,
                title = stringResource(R.string.toolbar_tmux_close_window),
                shortcut = stringResource(R.string.toolbar_tmux_close_window_sub),
                onClick = {
                    onSendBytes("exit\n".toByteArray(Charsets.UTF_8))
                    onDismiss()
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )

            // Section 3: Panes & Layout
            TmuxSectionHeader(title = stringResource(R.string.toolbar_tmux_section_pane))

            TmuxActionItem(
                icon = Icons.Filled.Splitscreen,
                title = stringResource(R.string.toolbar_tmux_split_horizontal),
                shortcut = stringResource(R.string.toolbar_tmux_split_horizontal_sub),
                onClick = {
                    onSendBytes(byteArrayOf(0x02, '"'.code.toByte()))
                    onDismiss()
                },
            )

            TmuxActionItem(
                icon = Icons.Filled.VerticalSplit,
                title = stringResource(R.string.toolbar_tmux_split_vertical),
                shortcut = stringResource(R.string.toolbar_tmux_split_vertical_sub),
                onClick = {
                    onSendBytes(byteArrayOf(0x02, '%'.code.toByte()))
                    onDismiss()
                },
            )

            TmuxActionItem(
                icon = Icons.Filled.AspectRatio,
                title = stringResource(R.string.toolbar_tmux_zoom_pane),
                shortcut = stringResource(R.string.toolbar_tmux_zoom_pane_sub),
                onClick = {
                    onSendBytes(byteArrayOf(0x02, 'z'.code.toByte()))
                    onDismiss()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showKillConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showKillConfirmDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.toolbar_tmux_kill_confirm_title),
                    color = MaterialTheme.colorScheme.error,
                )
            },
            text = {
                Text(text = stringResource(R.string.toolbar_tmux_kill_confirm_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showKillConfirmDialog = false
                        onSendBytes("tmux kill-session\n".toByteArray(Charsets.UTF_8))
                        onDismiss()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.toolbar_tmux_kill_confirm_button),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showKillConfirmDialog = false }) {
                    Text(text = stringResource(R.string.toolbar_tmux_cancel))
                }
            },
        )
    }
}

@Composable
private fun TmuxSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
    )
}

@Composable
private fun TmuxActionItem(
    icon: ImageVector,
    title: String,
    shortcut: String,
    isDanger: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isDanger) {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
        } else {
            MaterialTheme.colorScheme.surface
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.weight(1f),
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(start = 4.dp),
            ) {
                Text(
                    text = shortcut,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}
