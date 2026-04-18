package ru.voboost.components.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.MainThread;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.font.Font;
import ru.voboost.components.popup.Popup;
import ru.voboost.components.theme.Theme;

/**
 * Dialog component — uses Popup as container, adds title, message, and buttons.
 *
 * <p>
 * Provides a standard confirmation dialog with customizable title,
 * message, and up to 2 action buttons (confirm + cancel).
 * Uses the Popup component for overlay and animations.
 */
public class Dialog {
    private final Popup popup;
    private final Context context;
    private Theme currentTheme = null;
    private DialogColors colors = DialogTheme.getColors(Theme.FREE_DARK);

    private String title;
    private String message;
    private String confirmText;
    private String cancelText;
    private Runnable onConfirm;
    private Runnable onCancel;
    private Runnable onDismiss;
    private ButtonStyle confirmButtonStyle = ButtonStyle.PRIMARY;
    private ButtonStyle cancelButtonStyle = ButtonStyle.SECONDARY;

    // Performance optimization: avoid rebuilding UI on every show()
    private boolean contentBuilt = false;

    public Dialog(Context context) {
        this.context = context;
        this.popup = new Popup(context);
    }

    // --- Public API ---

    @MainThread
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        this.currentTheme = theme;
        this.colors = DialogTheme.getColors(theme);
        this.popup.setTheme(theme);
    }

    @MainThread
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    @MainThread
    public void setTitle(String title) {
        this.title = title;
        resetContentBuild();
    }

    public String getTitle() {
        return title;
    }

    @MainThread
    public void setMessage(String message) {
        this.message = message;
        resetContentBuild();
    }

    public String getMessage() {
        return message;
    }

    @MainThread
    public void setConfirmButton(String text, Runnable action) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Confirm button text cannot be null or empty");
        }
        this.confirmText = text;
        this.onConfirm = action;
        resetContentBuild();
    }

    @MainThread
    public void setCancelButton(String text, Runnable action) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancel button text cannot be null or empty");
        }
        this.cancelText = text;
        this.onCancel = action;
        resetContentBuild();
    }

    @MainThread
    public void setOnDismissListener(Runnable onDismiss) {
        this.onDismiss = onDismiss;
    }

    /**
     * Forces content to be rebuilt on next show().
     * Use this if you've changed title, message, or buttons after the first show().
     */
    public void rebuildContent() {
        contentBuilt = false;
    }

    /**
     * Resets the content build flag. Call this when any content property changes.
     * Content will be rebuilt on the next show() call.
     */
    private void resetContentBuild() {
        contentBuilt = false;
    }

    /**
     * Sets the style for the confirm button.
     *
     * @param style Button style (PRIMARY or SECONDARY)
     */
    @MainThread
    public void setConfirmButtonStyle(ButtonStyle style) {
        this.confirmButtonStyle = style;
    }

    /**
     * Sets the style for the cancel button.
     *
     * @param style Button style (PRIMARY or SECONDARY)
     */
    @MainThread
    public void setCancelButtonStyle(ButtonStyle style) {
        this.cancelButtonStyle = style;
    }

    /**
     * Shows the dialog.
     *
     * <p>NOTE: Due to performance optimization, content (title, message, buttons)
     * is built only on the first show(). If you need to change content after
     * the first show(), call rebuildContent() before calling show() again.
     *
     * @throws IllegalStateException if theme is not set
     */
    @MainThread
    public void show() {
        if (currentTheme == null) {
            throw new IllegalStateException("Theme must be set before show()");
        }

        // Only build content once for performance
        if (!contentBuilt) {
            buildContent();
            contentBuilt = true;
        }

        popup.setOnDismissListener(() -> {
            if (onDismiss != null) {
                onDismiss.run();
            }
        });
        popup.show();
    }

    @MainThread
    public void dismiss() {
        popup.dismissWithAnimation();
    }

    @MainThread
    public boolean isShowing() {
        return popup.isShowing();
    }

    // --- Content building ---

    private void buildContent() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Title
        if (title != null && !title.isEmpty()) {
            TextView titleView = new TextView(context);
            titleView.setText(title);
            titleView.setTextSize(0, DialogDimensions.TITLE_TEXT_SIZE_PX);
            titleView.setTypeface(Font.getBold(context, title));
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextColor(colors.titleText);

            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            titleParams.bottomMargin = (int) DialogDimensions.TITLE_BOTTOM_MARGIN_PX;
            titleView.setLayoutParams(titleParams);
            layout.addView(titleView);
        }

        // Message
        if (message != null && !message.isEmpty()) {
            TextView messageView = new TextView(context);
            messageView.setText(message);
            messageView.setTextSize(0, DialogDimensions.MESSAGE_TEXT_SIZE_PX);
            messageView.setTypeface(Font.getRegular(context));
            messageView.setGravity(Gravity.CENTER);
            messageView.setTextColor(colors.messageText);

            LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            messageParams.bottomMargin = (int) DialogDimensions.MESSAGE_BOTTOM_MARGIN_PX;
            messageView.setLayoutParams(messageParams);
            layout.addView(messageView);
        }

        // Buttons container (horizontal)
        if (cancelText != null || confirmText != null) {
            LinearLayout buttonContainer = new LinearLayout(context);
            buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
            buttonContainer.setGravity(Gravity.CENTER);

            // Cancel button (SECONDARY, left)
            if (cancelText != null) {
                Button cancelButton = new Button(context);
                cancelButton.setTheme(currentTheme);
                cancelButton.setStyle(cancelButtonStyle);
                cancelButton.setText(cancelText);
                cancelButton.setOnClickListener(v -> {
                    if (onCancel != null) {
                        onCancel.run();
                    }
                    dismiss();
                });

                LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
                        0, (int) DialogDimensions.BUTTON_HEIGHT_PX, 1f);
                if (confirmText != null) {
                    cancelParams.rightMargin = (int) (DialogDimensions.BUTTON_GAP_PX / 2);
                }
                cancelButton.setLayoutParams(cancelParams);
                buttonContainer.addView(cancelButton);
            }

            // Confirm button (PRIMARY, right)
            if (confirmText != null) {
                Button confirmButton = new Button(context);
                confirmButton.setTheme(currentTheme);
                confirmButton.setStyle(confirmButtonStyle);
                confirmButton.setText(confirmText);
                confirmButton.setOnClickListener(v -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                    dismiss();
                });

                LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(
                        0, (int) DialogDimensions.BUTTON_HEIGHT_PX, 1f);
                if (cancelText != null) {
                    confirmParams.leftMargin = (int) (DialogDimensions.BUTTON_GAP_PX / 2);
                }
                confirmButton.setLayoutParams(confirmParams);
                buttonContainer.addView(confirmButton);
            }

            layout.addView(buttonContainer);
        }

        popup.setPopupContentView(layout);
    }
}
