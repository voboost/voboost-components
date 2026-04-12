package ru.voboost.components.select;

import android.content.Context;
import android.widget.FrameLayout;

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

    interface OnSelectionListener {
        void onSelected(int position, String value);
    }

    SelectPopup(Context context) {
        this.context = context;
        this.popup = new Popup(context);
        this.wheel = new SelectWheel(context);

        // Configure wheel defaults
        wheel.setCurvedEnabled(true);
        wheel.setAtmosphericEnabled(true);
        wheel.setCurtainEnabled(true);
        wheel.setSelectedTextBold(true);

        // Wheel layout inside popup
        FrameLayout.LayoutParams wheelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        wheel.setLayoutParams(wheelParams);

        popup.setPopupContentView(wheel);

        // Listen for selection
        wheel.setOnWheelChangedListener(new SelectWheel.OnWheelChangedListener() {
            @Override
            public void onWheelSelected(SelectWheel w, int position) {
                if (onSelectionListener != null) {
                    onSelectionListener.onSelected(position, w.getCurrentItem());
                }
            }

            @Override
            public void onWheelScrolled(SelectWheel w, int scrollOffset) {
                // not used
            }

            @Override
            public void onWheelScrollStateChanged(SelectWheel w, int state) {
                // not used
            }
        });
    }

    void setTheme(Theme theme) {
        this.currentTheme = theme;
        popup.setTheme(theme);
        SelectColors colors = SelectTheme.getColors(theme);
        wheel.applyColors(colors);
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
