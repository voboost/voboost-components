package ru.voboost.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ru.voboost.components.theme.Theme

/**
 * Dialog component for Jetpack Compose.
 *
 * Shows a themed dialog with title, message, and action buttons.
 * Uses Popup as the overlay container.
 *
 * @param theme Theme enum value
 * @param title Dialog title text
 * @param message Dialog message text
 * @param confirmText Text for the confirm button (optional)
 * @param cancelText Text for the cancel button (optional)
 * @param onConfirm Callback when confirm is pressed
 * @param onCancel Callback when cancel is pressed
 * @param onDismiss Callback when dialog is dismissed
 */
@Composable
fun Dialog(
    theme: Theme,
    title: String = "",
    message: String = "",
    confirmText: String? = null,
    cancelText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    // Track visibility state separately
    var isVisible by remember { mutableStateOf(false) }

    // Create dialog once
    val dialog =
        remember {
            ru.voboost.components.dialog.Dialog(context)
                .apply {
                    setOnDismissListener {
                        isVisible = false
                        onDismiss?.invoke()
                    }
                }
        }

    // Update content when parameters change
    DisposableEffect(
        theme,
        title,
        message,
        confirmText,
        cancelText,
        onConfirm,
        onCancel,
        onDismiss,
    ) {
        dialog.setTheme(theme)
        dialog.setTitle(title)
        dialog.setMessage(message)

        if (confirmText != null) {
            dialog.setConfirmButton(confirmText) {
                onConfirm?.invoke()
                isVisible = false
            }
        }

        if (cancelText != null) {
            dialog.setCancelButton(cancelText) {
                onCancel?.invoke()
                isVisible = false
            }
        }

        // Force content rebuild
        dialog.rebuildContent()

        onDispose {}
    }

    // Show/hide dialog based on visibility state
    DisposableEffect(isVisible) {
        if (isVisible) {
            dialog.show()
        } else {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }

        onDispose {}
    }

    // Auto-show when first composed
    DisposableEffect(Unit) {
        isVisible = true
        onDispose {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    // Handle lifecycle events (dismiss on pause)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE) {
                    isVisible = false
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
