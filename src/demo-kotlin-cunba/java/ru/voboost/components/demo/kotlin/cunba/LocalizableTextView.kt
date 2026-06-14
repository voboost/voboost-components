package ru.voboost.components.demo.kotlin.cunba

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView

import ru.voboost.components.i18n.ILocalizable
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.IThemable
import ru.voboost.components.theme.Theme

/**
 * Plain text view that resolves its text by language and its color by theme.
 *
 * <p>Used by the custom Activation section so its title/hint texts stay in sync
 * with the rest of the demo (Section propagates theme/language to its
 * {@link IThemable}/{@link ILocalizable} descendants).
 */
class LocalizableTextView(context: Context, textSizePx: Float, role: Int) :
    TextView(context), IThemable, ILocalizable {

    companion object {
        /** Primary text color role (titles, price titles). */
        const val ROLE_TITLE = 0
        /** Muted hint text color role. */
        const val ROLE_HINT = 1
    }

    private val role: Int = role

    private var textMap: Map<String, String>? = null
    private var currentTheme: Theme? = null
    private var currentLanguage: Language? = null

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
    }

    /** Sets the localized text (language code -> text). */
    fun setData(text: Map<String, String>) {
        this.textMap = text
        resolve()
    }

    private fun isDark(): Boolean {
        return currentTheme != null && currentTheme?.value != null && currentTheme?.value?.endsWith("-dark") == true
    }

    private fun resolve() {
        if (textMap != null && textMap!!.isNotEmpty()) {
            val langCode = currentLanguage?.code ?: "en"
            setText(textMap?.get(langCode) ?: textMap?.values?.first())
        }
        if (currentTheme != null) {
            val color = if (role == ROLE_HINT) {
                if (isDark()) Color.parseColor("#919397") else Color.parseColor("#8f949e")
            } else {
                if (isDark()) Color.parseColor("#ffffff") else Color.parseColor("#2d3442")
            }
            setTextColor(color)
        }
    }

    override fun setTheme(theme: Theme) {
        this.currentTheme = theme
        resolve()
    }

    override fun propagateTheme(theme: Theme) {
        // Leaf view
    }

    override fun setLanguage(language: Language) {
        this.currentLanguage = language
        resolve()
    }

    override fun getCurrentLanguage(): Language? {
        return currentLanguage
    }

    override fun propagateLanguage(language: Language) {
        // Leaf view
    }
}
