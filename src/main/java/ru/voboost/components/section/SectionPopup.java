package ru.voboost.components.section;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.popup.Popup;
import ru.voboost.components.theme.Theme;

/**
 * Popup that shows the info text associated with a Section.
 * Header: close button (×) on the left, title centered.
 * Body: scrollable content blocks (title + text pairs).
 */
class SectionPopup {
    private static final int HEADER_HEIGHT_PX = 124;
    private static final int CLOSE_ICON_SIZE_PX = 30;
    private static final int CLOSE_ICON_MARGIN_PX = 47;
    private static final int TITLE_WIDTH_PX = 1000;
    private static final int TITLE_TOP_MARGIN_PX = 38;
    private static final int TITLE_LEFT_MARGIN_PX = 124;
    private static final int TITLE_TEXT_SIZE_PX = 36;
    private static final int BODY_TEXT_SIZE_PX = 32;
    private static final int BODY_TITLE_TEXT_SIZE_PX = 32;
    private static final int BODY_TOP_MARGIN_PX = 8;
    private static final int BLOCK_GAP_PX = 38;
    private static final int SUBTITLE_TO_TEXT_GAP_PX = 19;
    private static final int BODY_HORIZONTAL_PADDING_PX = 120;
    private static final int BODY_MAX_HEIGHT_PX = 402;
    private static final int FADING_EDGE_LENGTH_PX = 150;
    private static final float SUBTITLE_LINE_EXTRA_PX = 20f;
    private static final float TEXT_LINE_EXTRA_PX = 10f;

    private static final Map<Theme, Bitmap> CLOSE_ICON_CACHE = new EnumMap<>(Theme.class);

    private final Context context;
    private final Popup popup;
    private Theme theme;
    private Language language;
    private Map<String, String> title;
    private List<Section.PopupBlock> blocks;
    private boolean contentBuilt = false;

    SectionPopup(Context context) {
        this.context = context;
        this.popup = new Popup(context);
        this.popup.setDismissOnTouchOutside(true);
    }

    void setTheme(Theme theme) {
        if (theme == null) throw new IllegalArgumentException("Theme cannot be null");
        this.theme = theme;
        this.popup.setTheme(theme);
        contentBuilt = false;
    }

    void setLanguage(Language language) {
        if (language == null) throw new IllegalArgumentException("Language cannot be null");
        this.language = language;
        contentBuilt = false;
    }

    void setTitle(@Nullable Map<String, String> title) {
        this.title = title;
        contentBuilt = false;
    }

    void setBlocks(@Nullable List<Section.PopupBlock> blocks) {
        this.blocks = blocks;
        contentBuilt = false;
    }

    void show() {
        if (theme == null || language == null) return;
        if (!contentBuilt) {
            buildContent();
            contentBuilt = true;
        }
        popup.show();
    }

    void dismiss() {
        if (popup.isShowing()) {
            popup.dismissWithAnimation();
        }
    }

    boolean isShowing() {
        return popup.isShowing();
    }

    private void buildContent() {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        FrameLayout header = new FrameLayout(context);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, HEADER_HEIGHT_PX);
        header.setLayoutParams(headerParams);

        ImageView closeIcon = new ImageView(context);
        Bitmap closeBitmap = getCloseIcon(theme);
        if (closeBitmap != null) {
            closeIcon.setImageBitmap(closeBitmap);
        }
        closeIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(
                CLOSE_ICON_SIZE_PX, CLOSE_ICON_SIZE_PX);
        closeParams.gravity = Gravity.START | Gravity.TOP;
        closeParams.leftMargin = CLOSE_ICON_MARGIN_PX;
        closeParams.topMargin = CLOSE_ICON_MARGIN_PX;
        closeIcon.setLayoutParams(closeParams);
        closeIcon.setOnClickListener(v -> dismiss());
        header.addView(closeIcon);

        TextView titleView = new TextView(context);
        titleView.setText(localized(title));
        titleView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, TITLE_TEXT_SIZE_PX);
        titleView.setTypeface(Font.getBold(context, localized(title)));
        titleView.setTextColor(getHeaderTextColor());
        titleView.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
                TITLE_WIDTH_PX,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        titleParams.gravity = Gravity.TOP | Gravity.START;
        titleParams.topMargin = TITLE_TOP_MARGIN_PX;
        titleParams.leftMargin = TITLE_LEFT_MARGIN_PX;
        titleView.setLayoutParams(titleParams);
        header.addView(titleView);

        root.addView(header);

        FadingScrollView scroll = new FadingScrollView(context, FADING_EDGE_LENGTH_PX);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, BODY_MAX_HEIGHT_PX);
        scrollParams.topMargin = BODY_TOP_MARGIN_PX;
        scroll.setLayoutParams(scrollParams);
        scroll.setVerticalScrollBarEnabled(false);

        LinearLayout bodyContainer = new LinearLayout(context);
        bodyContainer.setOrientation(LinearLayout.VERTICAL);
        bodyContainer.setPadding(BODY_HORIZONTAL_PADDING_PX, 0, BODY_HORIZONTAL_PADDING_PX, 0);
        ScrollView.LayoutParams containerParams = new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
        scroll.addView(bodyContainer, containerParams);

        if (blocks != null) {
            for (int i = 0; i < blocks.size(); i++) {
                Section.PopupBlock block = blocks.get(i);
                Map<String, String> blockTitle = block.getTitle();
                Map<String, String> blockText = block.getText();

                LinearLayout blockLayout = new LinearLayout(context);
                blockLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    blockParams.topMargin = BLOCK_GAP_PX;
                }
                blockLayout.setLayoutParams(blockParams);

                if (blockTitle != null && !blockTitle.isEmpty()) {
                    TextView titleTv = new TextView(context);
                    titleTv.setText(localized(blockTitle));
                    titleTv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, BODY_TITLE_TEXT_SIZE_PX);
                    titleTv.setTypeface(Font.getBold(context, localized(blockTitle)));
                    titleTv.setTextColor(getSubtitleTextColor());
                    titleTv.setLineSpacing(SUBTITLE_LINE_EXTRA_PX, 1f);
                    blockLayout.addView(titleTv);
                }

                TextView textTv = new TextView(context);
                textTv.setText(localized(blockText));
                textTv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, BODY_TEXT_SIZE_PX);
                textTv.setTypeface(Font.getRegular(context));
                textTv.setTextColor(getBodyTextColor());
                textTv.setLineSpacing(TEXT_LINE_EXTRA_PX, 1f);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (blockTitle != null && !blockTitle.isEmpty()) {
                    textParams.topMargin = SUBTITLE_TO_TEXT_GAP_PX;
                }
                textTv.setLayoutParams(textParams);
                blockLayout.addView(textTv);

                bodyContainer.addView(blockLayout);
            }
        }

        root.addView(scroll);

        popup.setPopupContentView(root);
    }

    private String localized(@Nullable Map<String, String> map) {
        if (map == null || map.isEmpty()) return "";
        String code = language != null ? language.getCode() : null;
        String value = code != null ? map.get(code) : null;
        if (value == null) value = map.get("en");
        if (value == null) value = map.values().iterator().next();
        return value != null ? value : "";
    }

    private int getHeaderTextColor() {
        switch (theme) {
            case FREE_LIGHT:
            case DREAMER_LIGHT:
                return Color.parseColor("#ff2d3442");
            case FREE_DARK:
            case DREAMER_DARK:
            default:
                return Color.parseColor("#ffffff");
        }
    }

    private int getSubtitleTextColor() {
        return getHeaderTextColor();
    }

    private int getBodyTextColor() {
        switch (theme) {
            case FREE_LIGHT:
            case DREAMER_LIGHT:
                return Color.parseColor("#cc2d3442");
            case FREE_DARK:
            case DREAMER_DARK:
            default:
                return Color.parseColor("#b3ffffff");
        }
    }

    View getOverlayView() {
        return popup.getOverlayViewForTest();
    }

    private static Bitmap getCloseIcon(Theme theme) {
        if (theme == null) return null;
        synchronized (CLOSE_ICON_CACHE) {
            Bitmap cached = CLOSE_ICON_CACHE.get(theme);
            if (cached != null) {
                return cached;
            }
            Bitmap loaded = loadBitmap("Section_theme_" + theme.getValue() + ".png");
            if (loaded != null) {
                CLOSE_ICON_CACHE.put(theme, loaded);
            }
            return loaded;
        }
    }

    private static Bitmap loadBitmap(String name) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        try (InputStream in = SectionPopup.class.getResourceAsStream(name)) {
            if (in == null) return null;
            return BitmapFactory.decodeStream(in, null, opts);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * ScrollView that fades its top and bottom edges to transparency, revealing the
     * panel background beneath instead of compositing toward a solid color. This
     * matches the original Voyah dialog, where body text fades smoothly into the
     * panel background near the scroll edges.
     */
    private static class FadingScrollView extends ScrollView {
        private final int fadeLength;
        private final Paint fadePaint = new Paint();
        private final Matrix shaderMatrix = new Matrix();
        private final Rect clipBounds = new Rect();
        private final Shader topFade;
        private final Shader bottomFade;

        FadingScrollView(Context context, int fadeLength) {
            super(context);
            this.fadeLength = fadeLength;
            setVerticalFadingEdgeEnabled(false);
            setHorizontalFadingEdgeEnabled(false);
            fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            topFade = new LinearGradient(
                    0, 0, 0, fadeLength,
                    0xff000000, 0x00000000, Shader.TileMode.CLAMP);
            bottomFade = new LinearGradient(
                    0, 0, 0, fadeLength,
                    0x00000000, 0xff000000, Shader.TileMode.CLAMP);
        }

        @Override
        public void draw(Canvas canvas) {
            boolean fadeTop = canScrollVertically(-1);
            boolean fadeBottom = canScrollVertically(1);
            if ((!fadeTop && !fadeBottom) || !canvas.getClipBounds(clipBounds)) {
                super.draw(canvas);
                return;
            }
            int left = clipBounds.left;
            int right = clipBounds.right;
            int top = clipBounds.top;
            int bottom = clipBounds.bottom;
            int saveCount = canvas.saveLayer(left, top, right, bottom, null);
            super.draw(canvas);
            if (fadeTop) {
                shaderMatrix.setTranslate(0, top);
                topFade.setLocalMatrix(shaderMatrix);
                fadePaint.setShader(topFade);
                canvas.drawRect(left, top, right, top + fadeLength, fadePaint);
            }
            if (fadeBottom) {
                shaderMatrix.setTranslate(0, bottom - fadeLength);
                bottomFade.setLocalMatrix(shaderMatrix);
                fadePaint.setShader(bottomFade);
                canvas.drawRect(left, bottom - fadeLength, right, bottom, fadePaint);
            }
            canvas.restoreToCount(saveCount);
        }
    }
}
