package ru.voboost.components.select;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.popup.Popup;
import ru.voboost.components.theme.Theme;

/**
 * Internal popup containing a WheelView for the Select component.
 * Uses Popup as the overlay container.
 */
class SelectPopup {
    private final Popup popup;
    private final SelectWheel wheel;
    private final Context context;
    private Theme currentTheme;
    private OnSelectionListener onSelectionListener;
    private Select.Mode mode = Select.Mode.CURVED;
    private String confirmText = "Confirm";
    private String cancelText = "Cancel";
    private Button confirmButton;
    private Button cancelButton;

    interface OnSelectionListener {
        void onSelected(int position, String value);
    }

    SelectPopup(Context context) {
        this.context = context;
        this.popup = new Popup(context);
        this.wheel = new SelectWheel(context);

        applyMode();

        // Vertical container: wheel on top, Confirm/Cancel buttons below.
        // Scrolling only moves the wheel; the Confirm button commits the
        // value (explicit step, matching the original date/time dialogs).
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        FrameLayout.LayoutParams rootParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        rootParams.gravity = Gravity.CENTER;
        root.setLayoutParams(rootParams);

        LinearLayout.LayoutParams wheelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) SelectDimensions.WHEEL_HEIGHT_PX);
        wheelParams.gravity = Gravity.CENTER;
        wheel.setLayoutParams(wheelParams);
        root.addView(wheel);

        LinearLayout buttonRow = new LinearLayout(context);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams buttonRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonRowParams.topMargin = (int) SelectDimensions.POPUP_BUTTON_TOP_MARGIN_PX;
        buttonRow.setLayoutParams(buttonRowParams);

        cancelButton = new Button(context);
        cancelButton.setStyle(ButtonStyle.SECONDARY);
        cancelButton.setText(cancelText);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
                0, (int) SelectDimensions.POPUP_BUTTON_HEIGHT_PX, 1f);
        cancelParams.rightMargin = (int) (SelectDimensions.POPUP_BUTTON_GAP_PX / 2);
        cancelButton.setLayoutParams(cancelParams);
        cancelButton.setOnClickListener(v -> dismiss());
        buttonRow.addView(cancelButton);

        confirmButton = new Button(context);
        confirmButton.setStyle(ButtonStyle.PRIMARY);
        confirmButton.setText(confirmText);
        LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(
                0, (int) SelectDimensions.POPUP_BUTTON_HEIGHT_PX, 1f);
        confirmParams.leftMargin = (int) (SelectDimensions.POPUP_BUTTON_GAP_PX / 2);
        confirmButton.setLayoutParams(confirmParams);
        confirmButton.setOnClickListener(v -> {
            if (onSelectionListener != null) {
                onSelectionListener.onSelected(
                        wheel.getCurrentPosition(), wheel.getCurrentItem());
            }
        });
        buttonRow.addView(confirmButton);

        root.addView(buttonRow);

        popup.setPopupContentView(root);
    }

    void setMode(Select.Mode mode) {
        this.mode = mode;
        applyMode();
    }

    private void applyMode() {
        boolean curved = mode == Select.Mode.CURVED;
        // FLAT matches production WheelDefault (no curve, no fade).
        // CURVED adds curved 3D rotation + atmospheric fade.
        // Both: non-cyclic (first to last), no curtain/indicator (selection
        // shown by size + color: selected item large and white).
        wheel.setCurvedEnabled(curved);
        wheel.setAtmosphericEnabled(curved);
        wheel.setCyclicEnabled(false);
        wheel.setIndicatorEnabled(false);
        wheel.setCurtainEnabled(false);
        wheel.setSelectedTextBold(true);
    }

    void setConfirmText(String text) {
        this.confirmText = text != null ? text : "";
        if (confirmButton != null) {
            confirmButton.setText(this.confirmText);
        }
    }

    void setCancelText(String text) {
        this.cancelText = text != null ? text : "";
        if (cancelButton != null) {
            cancelButton.setText(this.cancelText);
        }
    }

    void setTheme(Theme theme) {
        this.currentTheme = theme;
        popup.setTheme(theme);
        SelectColors colors = SelectTheme.getColors(theme);
        wheel.applyColors(colors);
        if (confirmButton != null) {
            confirmButton.setTheme(theme);
        }
        if (cancelButton != null) {
            cancelButton.setTheme(theme);
        }
    }

    void setData(java.util.List<String> items) {
        wheel.setData(items);
    }

    void setData(java.util.List<String> items, int defaultPosition) {
        wheel.setData(items, defaultPosition);
    }

    void setOnSelectionListener(OnSelectionListener listener) {
        this.onSelectionListener = listener;
    }

    void show() {
        popup.show();
    }

    void dismiss() {
        popup.dismissWithAnimation();
    }

    boolean isShowing() {
        return popup.isShowing();
    }
}
