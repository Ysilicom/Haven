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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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

/**
 * Mobile-focused TMUX session manager bottom sheet.
 *
 * Provides pure, zero-friction session lifecycle actions without over-engineered
 * client-side state locks or artificial disable gates:
 *  - Session Entry:
 *    • New Session (custom naming dialog -> tmux new -A -s <name>)
 *    • Attach Session (specified naming dialog + tmux ls quick button -> tmux attach -t <name>)
 *  - Session Control:
 *    • Detach Session (sends Ctrl+B d to keep background tasks running)
 *    • Kill Session (confirmation dialog -> tmux kill-session)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TmuxQuickBottomSheet(
    onDismiss: () -> Unit,
    onSendBytes: (ByteArray) -> Unit,
) {
    var showNewDialog by remember { mutableStateOf(false) }
    var showAttachDialog by remember { mutableStateOf(false) }
    var showKillConfirmDialog by remember { mutableStateOf(false) }
    var sessionNameInput by remember { mutableStateOf("main") }

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
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.toolbar_tmux_cancel),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action 1: New Named Session
            TmuxSessionActionItem(
                icon = Icons.Filled.Add,
                title = stringResource(R.string.toolbar_tmux_new_session),
                subtitle = stringResource(R.string.toolbar_tmux_new_session_sub),
                badge = "tmux new -s",
                onClick = {
                    sessionNameInput = "main"
                    showNewDialog = true
                },
            )

            // Action 2: Attach Specified Session
            TmuxSessionActionItem(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                title = stringResource(R.string.toolbar_tmux_attach_session_named),
                subtitle = stringResource(R.string.toolbar_tmux_attach_session_named_sub),
                badge = "tmux a -t",
                onClick = {
                    sessionNameInput = "main"
                    showAttachDialog = true
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )

            // Action 3: Detach Session (Background)
            TmuxSessionActionItem(
                icon = Icons.Filled.ExitToApp,
                title = stringResource(R.string.toolbar_tmux_detach_named),
                subtitle = stringResource(R.string.toolbar_tmux_detach_named_sub),
                badge = "Ctrl+B d",
                onClick = {
                    onSendBytes(byteArrayOf(0x02, 'd'.code.toByte()))
                    onDismiss()
                },
            )

            // Action 4: Kill Session
            TmuxSessionActionItem(
                icon = Icons.Filled.DeleteForever,
                title = stringResource(R.string.toolbar_tmux_kill_session_named),
                subtitle = stringResource(R.string.toolbar_tmux_kill_session_named_sub),
                badge = "kill-session",
                isDanger = true,
                onClick = {
                    showKillConfirmDialog = true
                },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Dialog 1: New Named Session
    if (showNewDialog) {
        AlertDialog(
            onDismissRequest = { showNewDialog = false },
            title = { Text(text = stringResource(R.string.toolbar_tmux_dialog_new_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = sessionNameInput,
                        onValueChange = { input ->
                            sessionNameInput = input.filter { c -> c.isLetterOrDigit() || c == '-' || c == '_' }
                        },
                        label = { Text(stringResource(R.string.toolbar_tmux_dialog_name_label)) },
                        placeholder = { Text("main") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = sessionNameInput.trim().ifEmpty { "main" }
                        onSendBytes("tmux new -A -s $name\n".toByteArray(Charsets.UTF_8))
                        showNewDialog = false
                        onDismiss()
                    },
                ) {
                    Text(text = stringResource(R.string.toolbar_tmux_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewDialog = false }) {
                    Text(text = stringResource(R.string.toolbar_tmux_cancel))
                }
            },
        )
    }

    // Dialog 2: Attach Specified Session
    if (showAttachDialog) {
        AlertDialog(
            onDismissRequest = { showAttachDialog = false },
            title = { Text(text = stringResource(R.string.toolbar_tmux_dialog_attach_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = sessionNameInput,
                        onValueChange = { input ->
                            sessionNameInput = input.filter { c -> c.isLetterOrDigit() || c == '-' || c == '_' }
                        },
                        label = { Text(stringResource(R.string.toolbar_tmux_dialog_name_label)) },
                        placeholder = { Text("main") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            onSendBytes("tmux ls\n".toByteArray(Charsets.UTF_8))
                            showAttachDialog = false
                            onDismiss()
                        },
                        modifier = Modifier.align(Alignment.Start),
                    ) {
                        Text(
                            text = stringResource(R.string.toolbar_tmux_dialog_list_sessions),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = sessionNameInput.trim().ifEmpty { "main" }
                        onSendBytes("tmux attach -t $name\n".toByteArray(Charsets.UTF_8))
                        showAttachDialog = false
                        onDismiss()
                    },
                ) {
                    Text(text = stringResource(R.string.toolbar_tmux_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAttachDialog = false }) {
                    Text(text = stringResource(R.string.toolbar_tmux_cancel))
                }
            },
        )
    }

    // Dialog 3: Kill Session Confirm Dialog
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
private fun TmuxSessionActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    isDanger: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
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
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = badge,
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
