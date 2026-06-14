package ru.voboost.components.demo.kotlin.cunba

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import ru.voboost.components.button.ButtonStyle
import ru.voboost.components.buttons.ButtonConfig
import ru.voboost.components.demo.shared.DemoHelpers
import ru.voboost.components.demo.shared.DemoState
import ru.voboost.components.i18n.Language
import ru.voboost.components.panel.Panel
import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.screen.Screen
import ru.voboost.components.section.Section
import ru.voboost.components.section.addButton
import ru.voboost.components.section.addButtons
import ru.voboost.components.section.addCheckbox
import ru.voboost.components.section.addRadio
import ru.voboost.components.section.addSelect
import ru.voboost.components.select.SelectOption
import ru.voboost.components.tabs.TabItem
import ru.voboost.components.tabs.Tabs
import ru.voboost.components.theme.Theme

/**
 * Demo Activity for CunBA 3 interface.
 *
 * <p>Reproduces the CunBA 3.0.0 HTML prototype using voboost-components.
 * Five tabs: Launcher, Applications, Interface, Vehicle, Settings.
 * All content is inline.
 */
class MainActivity : Activity() {

    companion object {
        private const val TAG = "CunbaDemo"

        private val TAB_VALUES = listOf(
            "launcher", "applications", "interface", "vehicle", "settings",
        )

        private const val DEFAULT_LANGUAGE = "ru"
        private const val DEFAULT_THEME = "dark"
    }

    private lateinit var screen: Screen
    private lateinit var demoState: DemoState

    // Settings radios for state application in tests
    private var languageRadio: Radio? = null
    private var themeRadio: Radio? = null
    private var carTypeRadio: Radio? = null

    // ============================================================
    // Helpers
    // ============================================================

    private fun opt(value: String, en: String, ru: String): SelectOption =
        SelectOption(value, mapOf("en" to en, "ru" to ru))

    private fun getMappedCombinedTheme(): String {
        val theme = demoState.currentTheme
        val mapped = if (theme == "auto") "dark" else theme

        return demoState.currentCarType + "-" + mapped
    }

    private fun currentTheme(): Theme = Theme.fromValue(getMappedCombinedTheme())

    private fun currentLanguage(): Language = Language.fromCode(demoState.currentLanguage)

    // ============================================================
    // Lifecycle
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        demoState = DemoState()
        demoState.currentLanguage = DEFAULT_LANGUAGE
        demoState.currentTheme = DEFAULT_THEME

        setupFullScreenMode()
        setupComponentHierarchy()
        updateAllComponents()
    }

    private fun setupFullScreenMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = window.insetsController
                if (controller != null) {
                    controller.hide(WindowInsets.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                val decorView = window.decorView
                if (decorView != null) {
                    decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } catch (e: NullPointerException) {
            Log.d(TAG, "Ignoring NullPointerException in setupFullScreenMode (Robolectric)")
        }
    }

    private fun setupComponentHierarchy() {
        val theme = currentTheme()
        val language = currentLanguage()

        val tabItems = getTabItems()
        val panels = createAllPanels()

        val tabs = Tabs.create(this, theme, language, tabItems).build()

        screen = Screen.create(this, theme)
            .tabs(tabs)
            .panels(panels)
            .build()
        setContentView(screen)

        tabs.setSelectedValue(demoState.selectedTab, false)
        tabs.setOnValueChangeListener { selectedTab ->
            demoState.selectedTab = selectedTab
            updateAllComponents()
        }
    }

    private fun createAllPanels(): Array<Panel> =
        TAB_VALUES.map { createPanelForTab(it) }.toTypedArray()

    private fun createPanelForTab(tabValue: String): Panel {
        return when (tabValue) {
            "launcher" -> createLauncherPanel()
            "applications" -> createApplicationsPanel()
            "interface" -> createInterfacePanel()
            "vehicle" -> createVehiclePanel()
            "settings" -> createSettingsPanel()
            else -> throw IllegalStateException("Unknown tab: $tabValue")
        }
    }

    private fun updateAllComponents() {
        if (!::screen.isInitialized) return
        val combinedTheme = getMappedCombinedTheme()
        screen.setBackgroundColor(DemoHelpers.getBackgroundColor(combinedTheme))
        screen.setTheme(Theme.fromValue(combinedTheme))
        screen.setLanguage(currentLanguage())
    }

    /**
     * Applies a full settings state. Used by visual tests.
     */
    fun applyState(language: String, theme: String, carType: String) {
        demoState.currentLanguage = language
        demoState.currentTheme = theme
        demoState.currentCarType = carType
        languageRadio?.setSelectedValue(language)
        themeRadio?.setSelectedValue(theme)
        carTypeRadio?.setSelectedValue(carType)
        updateAllComponents()
    }

    // Getters for testing
    fun getTabs(): Tabs? = if (::screen.isInitialized) screen.tabs else null

    fun getScreen(): Screen = screen

    fun getDemoState(): DemoState = demoState

    fun getPanel(): Panel? {
        if (!::screen.isInitialized) return null
        val panels = screen.panels ?: return null
        val tabIndex = TAB_VALUES.indexOf(demoState.selectedTab)
        return if (tabIndex in panels.indices) panels[tabIndex] else null
    }

    // ============================================================
    // Tab items
    // ============================================================

    private fun getTabItems(): List<TabItem> =
        listOf(
            TabItem.create("launcher", mapOf("en" to "Launcher", "ru" to "Лаунчер")).build(),
            TabItem.create("applications", mapOf("en" to "Applications", "ru" to "Приложения")).build(),
            TabItem.create("interface", mapOf("en" to "Interface", "ru" to "Интерфейс")).build(),
            TabItem.create("vehicle", mapOf("en" to "Vehicle", "ru" to "Машина")).build(),
            TabItem.create("settings", mapOf("en" to "Settings", "ru" to "Настройки")).build(),
        )

    // ============================================================
    // Panel: Settings
    // ============================================================

    private fun createSettingsPanel(): Panel {
        val theme = currentTheme()
        val language = currentLanguage()

        val panel = Panel(this)
        panel.setTheme(theme)

        val settingsSection = Section(this).apply {
            setTitle(
                mapOf(
                    "en" to "CunBA 3.0.0.2025012501, trial period until February 1, 2025 11:30",
                    "ru" to "CunBA 3.0.0.2025012501, пробный период до 01 февраля 2025 11:30",
                ),
            )
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(settingsSection)

        languageRadio = settingsSection.addRadio(
            buttons = listOf(
                RadioButton("ru", mapOf("en" to "Russian", "ru" to "Русский")),
                RadioButton("en", mapOf("en" to "English", "ru" to "English")),
            ),
            selectedValue = demoState.currentLanguage,
            theme = theme,
            language = language,
            title = mapOf("en" to "Language", "ru" to "Язык"),
            onValueChange = { newValue ->
                demoState.currentLanguage = newValue
                updateAllComponents()
            },
        )

        themeRadio = settingsSection.addRadio(
            buttons = listOf(
                RadioButton("auto", mapOf("en" to "Auto", "ru" to "Авто")),
                RadioButton("dark", mapOf("en" to "Dark", "ru" to "Тёмная")),
                RadioButton("light", mapOf("en" to "Light", "ru" to "Светлая")),
            ),
            selectedValue = demoState.currentTheme,
            theme = theme,
            language = language,
            title = mapOf("en" to "Appearance", "ru" to "Внешний вид"),
            onValueChange = { newValue ->
                demoState.currentTheme = newValue
                updateAllComponents()
            },
        )

        carTypeRadio = settingsSection.addRadio(
            buttons = listOf(
                RadioButton("free", mapOf("en" to "Free", "ru" to "Free")),
                RadioButton("dreamer", mapOf("en" to "Dreamer", "ru" to "Dreamer")),
            ),
            selectedValue = demoState.currentCarType,
            theme = theme,
            language = language,
            title = mapOf("en" to "Car Model", "ru" to "Модель автомобиля"),
            onValueChange = { newValue ->
                demoState.currentCarType = newValue
                updateAllComponents()
            },
        )

        settingsSection.addButtons(
            buttons = listOf(
                ButtonConfig("update_cunba", "Обновить CunBA"),
                ButtonConfig("update_components", "Обновить компоненты"),
            ),
            selectedValue = "update_cunba",
            theme = theme,
            language = language,
            title = mapOf("en" to "Update", "ru" to "Обновление"),
        )

        panel.addView(createActivationSection(theme, language))
        return panel
    }

    // ============================================================
    // Activation section (custom Android Views, 3 horizontal columns)
    // ============================================================

    private fun createActivationSection(theme: Theme, language: Language): Section {
        val section = Section(this).apply {
            setTitle(mapOf("en" to "Activation", "ru" to "Активация"))
            setTheme(theme)
            setLanguage(language)
            setPaddingTop(30)
            setPaddingBottom(30)
        }

        val activation = LinearLayout(this)
        activation.orientation = LinearLayout.HORIZONTAL

        // Column 1: Photograph QR with VIN
        val col1 = LinearLayout(this)
        col1.orientation = LinearLayout.VERTICAL
        col1.addView(titleView(mapOf("en" to "Photograph QR code with VIN", "ru" to "Сфотографируйте QR-код с VIN")))
        val qr1ResId = resources.getIdentifier("activation_qr1", "drawable", packageName)
        if (qr1ResId != 0) {
            col1.addView(qrImageView(qr1ResId))
        }
        col1.addView(hintView(mapOf("en" to "LDP95H966PE302009", "ru" to "LDP95H966PE302009")))

        // Column 2: Choose subscription (.activation__step_price width:100% -> fills remaining)
        val col2 = LinearLayout(this)
        col2.orientation = LinearLayout.VERTICAL
        val col2Params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        col2Params.leftMargin = 80
        col2.addView(titleView(mapOf("en" to "Choose subscription", "ru" to "Выберите подписку")))
        col2.addView(priceTitleView(mapOf("en" to "10,000 rubles per year", "ru" to "10 000 рублей в год")))
        col2.addView(
            priceHintView(
                mapOf(
                    "en" to "Annual subscription, full functionality without localization.",
                    "ru" to "Подписка на год, доступна полностью вся функциональность без русификации.",
                ),
            ),
        )
        col2.addView(priceTitleView(mapOf("en" to "25,000 rubles, forever", "ru" to "25 000 рублей, бессрочно")))
        col2.addView(
            priceHintView(
                mapOf(
                    "en" to "Full functionality\nwithout localization.",
                    "ru" to "Доступна полностью вся функциональность\nбез русификации.",
                ),
            ),
        )
        col2.addView(priceTitleView(mapOf("en" to "50,000 rubles, forever", "ru" to "50 000 рублей, бессрочно")))
        col2.addView(
            priceHintView(
                mapOf(
                    "en" to "Full functionality\nwith localization.",
                    "ru" to "Доступна полностью вся функциональность\nс русификацией.",
                ),
            ),
        )

        // Column 3: Send photo to @cunba_ru
        val col3 = LinearLayout(this)
        col3.orientation = LinearLayout.VERTICAL
        val col3Params = LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT)
        col3Params.leftMargin = 80
        col3.addView(titleView(mapOf("en" to "Send photo to @cunba_ru", "ru" to "Отправьте фото @cunba_ru")))
        val qr2ResId = resources.getIdentifier("activation_qr2", "drawable", packageName)
        if (qr2ResId != 0) {
            col3.addView(qrImageView(qr2ResId))
        }
        col3.addView(
            hintView(
                mapOf(
                    "en" to "Pay for activation\nafter sending QR",
                    "ru" to "Оплатите активацию\nпосле отправки QR",
                ),
            ),
        )

        activation.addView(col1, LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT))
        activation.addView(col2, col2Params)
        activation.addView(col3, col3Params)

        val activationParams = android.view.ViewGroup.MarginLayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        activationParams.bottomMargin = 0
        section.addView(activation, activationParams)
        return section
    }

    private fun titleView(text: Map<String, String>): LocalizableTextView {
        val tv = LocalizableTextView(this, 28f, LocalizableTextView.ROLE_TITLE)
        tv.setData(text)
        tv.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )
        return tv
    }

    private fun hintView(text: Map<String, String>): LocalizableTextView {
        val tv = LocalizableTextView(this, 24f, LocalizableTextView.ROLE_HINT)
        tv.setData(text)
        tv.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )
        return tv
    }

    private fun priceTitleView(text: Map<String, String>): LocalizableTextView {
        val tv = LocalizableTextView(this, 28f, LocalizableTextView.ROLE_TITLE)
        tv.setData(text)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        params.topMargin = 28
        tv.setLayoutParams(params)
        return tv
    }

    private fun priceHintView(text: Map<String, String>): LocalizableTextView {
        val tv = LocalizableTextView(this, 24f, LocalizableTextView.ROLE_HINT)
        tv.setData(text)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        params.topMargin = 20
        tv.setLayoutParams(params)
        return tv
    }

    private fun qrImageView(resId: Int): ImageView {
        val iv = ImageView(this)
        iv.setImageResource(resId)
        val params = LinearLayout.LayoutParams(240, 240)
        params.topMargin = 45
        params.bottomMargin = 45
        iv.setLayoutParams(params)
        return iv
    }

    // ============================================================
    // Panel: Launcher
    // ============================================================

    private fun createLauncherPanel(): Panel {
        val theme = currentTheme()
        val language = currentLanguage()

        val panel = Panel(this)
        panel.setTheme(theme)

        // Section: Stop Applications
        val stopSection = Section(this).apply {
            setTitle(mapOf("en" to "Stop Applications", "ru" to "Остановка приложений"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(stopSection)

        stopSection.addButton(
            text = "Остановить приложения",
            style = ButtonStyle.SECONDARY,
            theme = theme,
            language = language,
            description = mapOf(
                "en" to "Stop all running applications to free memory.",
                "ru" to "Остановить все запущенные приложения для освобождения памяти.",
            ),
        )

        // Section: Application Scaling
        val scaleSection = Section(this).apply {
            setTitle(mapOf("en" to "Application Scaling", "ru" to "Масштаб приложений"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(scaleSection)

        scaleSection.addRadio(
            buttons = listOf(
                RadioButton("default", mapOf("en" to "Default", "ru" to "По умолчанию")),
                RadioButton("medium", mapOf("en" to "Medium", "ru" to "Средний")),
                RadioButton("large", mapOf("en" to "Large", "ru" to "Крупный")),
                RadioButton("maximum", mapOf("en" to "Maximum", "ru" to "Максимальный")),
            ),
            selectedValue = "default",
            theme = theme,
            language = language,
            descriptionAbove = mapOf(
                "en" to "Change scaling for all applications. It can be overridden per application in the launcher by long-pressing the application and choosing a different scale.",
                "ru" to "Изменить масштаб для всех приложений. Можно переопределить его для конкретного приложения\nв лаунчере, сделав долгое нажатие на приложении и выбрав другой масштаб.",
            ),
        )

        // Section: Third-party Applications in Launcher
        val thirdPartySection = Section(this).apply {
            setTitle(mapOf("en" to "Third-party Applications in Launcher", "ru" to "Сторонние приложения в лаунчере"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(thirdPartySection)

        thirdPartySection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "Yandex Keyboard", "ru" to "Яндекс Клавиатура"),
        )
        thirdPartySection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf("en" to "Yandex Music", "ru" to "Яндекс Музыка"),
        )
        thirdPartySection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf("en" to "Yandex Navigator", "ru" to "Яндекс Навигатор"),
        )

        // Section: Original Applications in Launcher
        val originalSection = Section(this).apply {
            setTitle(mapOf("en" to "Original Applications in Launcher", "ru" to "Оригинальные приложения в лаунчере"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(originalSection)

        originalSection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "Android Settings", "ru" to "Настройки Андроида"),
        )
        originalSection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf("en" to "DVR", "ru" to "Регистратор (DVR)"),
        )
        originalSection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "User Center", "ru" to "Центр пользователя (User center)"),
        )

        return panel
    }

    // ============================================================
    // Panel: Applications
    // ============================================================

    private fun createApplicationsPanel(): Panel {
        val theme = currentTheme()
        val language = currentLanguage()

        val panel = Panel(this)
        panel.setTheme(theme)

        val centralApps = listOf(
            opt("yandex_navigator", "Yandex Navigator", "Яндекс Навигатор"),
            opt("original", "Original App", "Оригинальное приложение"),
            opt("none", "No App Selected", "Приложение не выбрано"),
        )

        // Section: Central Screen
        val centralSection = Section(this).apply {
            setTitle(mapOf("en" to "Central Screen", "ru" to "Центральный экран"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(centralSection)

        addAppSelect(
            centralSection, theme, language, centralApps, "yandex_navigator",
            mapOf(
                "en" to "Navigation, override left panel button.\nTap and select an application to launch.",
                "ru" to "Навигация, переопределить кнопку на левой панели.\nНажмите и выберите приложение для запуска.",
            ),
        )
        addAppSelect(
            centralSection, theme, language, centralApps, "original",
            mapOf(
                "en" to "Music, override left panel button.\nTap and select an application to launch.",
                "ru" to "Музыка, переопределить кнопку на левой панели.\nНажмите и выберите приложение для запуска.",
            ),
        )
        addAppSelect(
            centralSection, theme, language, centralApps, "none",
            mapOf(
                "en" to "Auto-start application on central screen.\nTap and select an application to launch.",
                "ru" to "Автоматический запуск приложения на центральном экране.\nНажмите и выберите приложение для запуска.",
            ),
        )

        // Section: Passenger Screen
        val passengerSection = Section(this).apply {
            setTitle(mapOf("en" to "Passenger Screen", "ru" to "Экран пассажира"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(passengerSection)

        val passengerApps = listOf(
            opt("fmplay", "FMPlay", "FMPlay"),
            opt("rutube", "RUTUBE", "RUTUBE"),
            opt("efir", "Efir TV", "Эфир ТВ"),
            opt("kinopoisk", "KinoPoisk", "КиноПоиск"),
            opt("youtube", "YouTube", "YouTube"),
            opt("none", "No App Selected", "Приложение не выбрано"),
        )
        val passengerDescs = listOf(
            arrayOf(
                "Override first button on left panel.\nTap and select an application to launch.",
                "Переопределить первую кнопку на левой панели.\nНажмите и выберите приложение для запуска.",
            ),
            arrayOf(
                "Override second button on left panel.\nTap and select an application to launch.",
                "Переопределить вторую кнопку на левой панели.\nНажмите и выберите приложение для запуска.",
            ),
            arrayOf(
                "Override third button on left panel.\nTap and select an application to launch.",
                "Переопределить третью кнопку на левой панели.\nНажмите и выберите приложение для запуска.",
            ),
            arrayOf(
                "Override fourth button on left panel.\nTap and select an application to launch.",
                "Переопределить четвёртую кнопку на левой панели.\nНажмите и выберите приложение для запуска.",
            ),
            arrayOf(
                "Override fifth button on left panel.\nTap and select an application to launch.",
                "Переопределить пятую кнопку на левой панели.\nНажмите и выберите приложение для запуска.",
            ),
        )
        val passengerKeys = listOf("fmplay", "rutube", "efir", "kinopoisk", "youtube")
        for (i in passengerKeys.indices) {
            addAppSelect(
                passengerSection, theme, language, passengerApps, passengerKeys[i],
                mapOf("en" to passengerDescs[i][0], "ru" to passengerDescs[i][1]),
            )
        }
        addAppSelect(
            passengerSection, theme, language, passengerApps, "none",
            mapOf(
                "en" to "Auto-start application on passenger screen.\nTap and select an application to launch.",
                "ru" to "Автоматический запуск приложения на экране пассажира.\nНажмите и выберите приложение для запуска.",
            ),
        )

        // Section: Disable Original Applications
        val disableSection = Section(this).apply {
            setTitle(mapOf("en" to "Disable Original Applications", "ru" to "Отключение оригинальных приложений"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(disableSection)

        val disableApps = listOf(
            arrayOf("Voice Control", "Голосовое управление"),
            arrayOf("Video", "Видео"),
            arrayOf("Radio", "Радио"),
            arrayOf("Navigator", "Навигатор"),
            arrayOf("User Center", "Центр пользователя"),
            arrayOf("Other Applications", "Другие приложения"),
        )
        for (app in disableApps) {
            disableSection.addCheckbox(
                checked = false,
                theme = theme,
                language = language,
                label = mapOf("en" to app[0], "ru" to app[1]),
            )
        }

        return panel
    }

    private fun addAppSelect(
        section: Section,
        theme: Theme,
        language: Language,
        options: List<SelectOption>,
        selectedValue: String,
        description: Map<String, String>,
    ) {
        section.addSelect(
            options = options,
            selectedValue = selectedValue,
            theme = theme,
            language = language,
            description = description,
        )
    }

    // ============================================================
    // Panel: Interface
    // ============================================================

    private fun createInterfacePanel(): Panel {
        val theme = currentTheme()
        val language = currentLanguage()

        val panel = Panel(this)
        panel.setTheme(theme)

        val keyboardApps = listOf(
            opt("yandex_keyboard", "Yandex Keyboard", "Яндекс Клавиатура"),
            opt("original_keyboard", "Original Keyboard", "Оригинальная клавиатура"),
            opt("none", "No App Selected", "Приложение не выбрано"),
        )

        // Section: Language
        val langSection = Section(this).apply {
            setTitle(mapOf("en" to "Language", "ru" to "Язык"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(langSection)

        langSection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "Car system localization to Russian", "ru" to "Русификация системы автомобиля"),
            description = mapOf(
                "en" to "Translates the car system to Russian.\nAfter enabling this option, Chinese language must be enabled in settings.",
                "ru" to "Переводит на русский язык систему автомобиля.\nПосле включения этой опции надо включить китайский язык в настройках.",
            ),
        )
        langSection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "Remove hieroglyphs from car name", "ru" to "Убрать иероглифы из имени автомобиля"),
            description = mapOf(
                "en" to "Removes hieroglyphs from the car name used in Bluetooth and AP Host.",
                "ru" to "Убирает иероглифы из имени автомобиля, которое используется в Bluetooth и AP Host.",
            ),
        )
        langSection.addRadio(
            buttons = listOf(
                RadioButton("original", mapOf("en" to "Original", "ru" to "Оригинальный")),
                RadioButton("voyah_tweaks", mapOf("en" to "VoyahTweaks", "ru" to "VoyahTweaks")),
                RadioButton("alice", mapOf("en" to "Yandex Alice", "ru" to "Яндекс Алиса")),
            ),
            selectedValue = "original",
            theme = theme,
            language = language,
            title = mapOf("en" to "Voice Assistant", "ru" to "Голосовой помощник"),
        )

        // Section: Central Screen
        val centralSection = Section(this).apply {
            setTitle(mapOf("en" to "Central Screen", "ru" to "Центральный экран"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(centralSection)

        addAppSelect(
            centralSection, theme, language, keyboardApps, "yandex_keyboard",
            mapOf(
                "en" to "Override keyboard on central screen.\nTap and select a keyboard application.",
                "ru" to "Переопределить клавиатуру на центральном экране.\nНажмите и выберите приложение клавиатуры.",
            ),
        )

        centralSection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf("en" to "Show Arrow", "ru" to "Показать стрелку"),
            description = mapOf(
                "en" to "Tapping the arrow hides the side panel, long press opens CunBA settings.",
                "ru" to "Нажатие на стрелку скрывает боковую панель, долгое нажатие открывает настройки CunBA.",
            ),
        )
        centralSection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf(
                "en" to "Long press climate opens driving settings",
                "ru" to "Долгое нажатие на кнопку климата открывает настройки движения",
            ),
            description = mapOf(
                "en" to "Open driving settings on long press of the climate button on the left panel.",
                "ru" to "Открывать настройки движения при длинном нажатии на кнопку климата на левой панели.",
            ),
        )
        centralSection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf(
                "en" to "Press Navigator moves it to dashboard",
                "ru" to "Нажатие на кнопку Навигатора переносит его на приборную панель",
            ),
            description = mapOf(
                "en" to "1. Launch the original Navigator application once, agree to all terms.\n2. Enable the dashboard variant with fullscreen navigator display mode: Settings -> Display -> FS NAV.\n3. Enable fullscreen navigator display mode on the dashboard by pressing the \"two rectangles\" button on the steering wheel several times.\n4. Launch the navigator you selected on the left panel of the central screen.\n5. Press and hold the navigator button on the left panel, it will move to the dashboard.",
                "ru" to "1. Запустите один раз оригинальное приложение Навигатора, согласитесь со всеми пунктами.\n2. Включите вариант приборной панели с режимом отображения полноэкранного навигатора: Настройки -> Отображение -> FS NAV.\n3. Включите на приборной панели режим отображения полноэкранного навигатора, нажимая на руле несколько раз кнопку «два прямоугольника».\n4. Запустите навигатор, который вы выбрали на левой панели центрального экрана.\n5. Нажмите и удерживайте кнопку навигатора на левой панели, он перебросится на приборную панель.",
            ),
        )

        centralSection.addRadio(
            buttons = listOf(
                RadioButton("cunba_original", mapOf("en" to "CunBA / Original", "ru" to "CunBA / Оригинальный")),
                RadioButton("original_cunba", mapOf("en" to "Original / CunBA", "ru" to "Оригинальный / CunBA")),
            ),
            selectedValue = "cunba_original",
            theme = theme,
            language = language,
            title = mapOf("en" to "Launcher", "ru" to "Лаунчер"),
            descriptionAbove = mapOf(
                "en" to "Normal and long press on the launcher button in the left panel.",
                "ru" to "Обычное и долгое нажатие на кнопку лаунчера в левой панели.",
            ),
        )

        // Section: Passenger Screen
        val passengerSection = Section(this).apply {
            setTitle(mapOf("en" to "Passenger Screen", "ru" to "Экран пассажира"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(passengerSection)

        addAppSelect(
            passengerSection, theme, language, keyboardApps, "original_keyboard",
            mapOf(
                "en" to "Override keyboard on passenger screen.\nTap and select a keyboard application.",
                "ru" to "Переопределить клавиатуру на экране пассажира.\nНажмите и выберите приложение клавиатуры.",
            ),
        )

        passengerSection.addRadio(
            buttons = listOf(
                RadioButton("cunba_original", mapOf("en" to "CunBA / Original", "ru" to "CunBA / Оригинальный")),
                RadioButton("original_cunba", mapOf("en" to "Original / CunBA", "ru" to "Оригинальный / CunBA")),
            ),
            selectedValue = "original_cunba",
            theme = theme,
            language = language,
            title = mapOf("en" to "Launcher", "ru" to "Лаунчер"),
            descriptionAbove = mapOf(
                "en" to "Normal and long press on the launcher button in the left panel.",
                "ru" to "Обычное и долгое нажатие на кнопку лаунчера в левой панели.",
            ),
        )

        // Section: Home Button
        val homeSection = Section(this).apply {
            setTitle(mapOf("en" to "Home Button", "ru" to "Домик"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(homeSection)

        homeSection.addRadio(
            buttons = listOf(
                RadioButton("home", mapOf("en" to "Home", "ru" to "Домой")),
                RadioButton("back", mapOf("en" to "Back", "ru" to "Назад")),
                RadioButton("stop", mapOf("en" to "Stop App", "ru" to "Остановить приложение")),
            ),
            selectedValue = "back",
            theme = theme,
            language = language,
            title = mapOf(
                "en" to "Press Home Button in Left Panel",
                "ru" to "Нажатие на кнопку Домик в левой панели",
            ),
        )
        homeSection.addRadio(
            buttons = listOf(
                RadioButton("home", mapOf("en" to "Home", "ru" to "Домой")),
                RadioButton("back", mapOf("en" to "Back", "ru" to "Назад")),
                RadioButton("stop", mapOf("en" to "Stop App", "ru" to "Остановить приложение")),
            ),
            selectedValue = "stop",
            theme = theme,
            language = language,
            title = mapOf(
                "en" to "Long Press Home Button in Left Panel",
                "ru" to "Долгое нажатие на кнопку Домик в левой панели",
            ),
        )

        homeSection.addSelect(
            options = listOf(
                opt("original_widget", "Original Widget", "Оригинальный виджет"),
                opt("yandex_navigator", "Yandex Navigator", "Яндекс Навигатор"),
                opt("none", "No App Selected", "Приложение не выбрано"),
            ),
            selectedValue = "original_widget",
            theme = theme,
            language = language,
            title = mapOf("en" to "Navigation", "ru" to "Навигация"),
            description = mapOf(
                "en" to "Select an application for the navigation widget.",
                "ru" to "Выберите приложение для навигационного виджета.",
            ),
        )

        homeSection.addSelect(
            options = listOf(
                opt("original", "Original", "Оригинальный"),
                opt("yandex_weather", "Yandex Weather", "Яндекс Погода"),
                opt("none", "No App Selected", "Приложение не выбрано"),
            ),
            selectedValue = "original",
            theme = theme,
            language = language,
            title = mapOf("en" to "Weather", "ru" to "Погода"),
            description = mapOf(
                "en" to "Select a data source for the weather.",
                "ru" to "Выберите источник данных для погоды.",
            ),
        )

        homeSection.addButton(
            text = "Обновить погоду",
            style = ButtonStyle.SECONDARY,
            theme = theme,
            language = language,
            description = mapOf(
                "en" to "Update the weather widget data on the home screen.",
                "ru" to "Обновить данные погодного виджета на домашнем экране.",
            ),
        )

        // Section: Time Zone
        val tzSection = Section(this).apply {
            setTitle(mapOf("en" to "Time Zone", "ru" to "Часовой пояс"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(tzSection)

        tzSection.addSelect(
            options = listOf(
                opt("gmt+02", "(GMT+02) Kaliningrad", "(GMT+02) Калининград"),
                opt("gmt+03", "(GMT+03) Moscow", "(GMT+03) Москва"),
                opt("gmt+04", "(GMT+04) Samara", "(GMT+04) Самара"),
                opt("gmt+05", "(GMT+05) Yekaterinburg", "(GMT+05) Екатеринбург"),
                opt("gmt+07", "(GMT+07) Krasnoyarsk", "(GMT+07) Красноярск"),
                opt("gmt+09", "Petropavlovsk-Kamchatsky (GMT+09)", "Петропавловск-Камчатский (GMT+09)"),
            ),
            selectedValue = "gmt+09",
            theme = theme,
            language = language,
            descriptionAbove = mapOf(
                "en" to "Select your time zone.\nThe dashboard time will change after a reboot;\npress and hold the star and two-rectangles buttons on the steering wheel.",
                "ru" to "Выберите свой часовой пояс.\nНа приборной панели время изменится после перезагрузки,\nзажмите и держите на руле звёздочка и два прямоугольника.",
            ),
        )

        return panel
    }

    // ============================================================
    // Panel: Vehicle
    // ============================================================

    private fun createVehiclePanel(): Panel {
        val theme = currentTheme()
        val language = currentLanguage()

        val panel = Panel(this)
        panel.setTheme(theme)

        // Section: Seats and Mirrors
        val seatsSection = Section(this).apply {
            setTitle(mapOf("en" to "Seats and Mirrors", "ru" to "Сиденья и зеркала"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(seatsSection)

        val seatDescs = listOf(
            arrayOf(
                "Driving, driver seat and mirror positions.",
                "Вождение, положение сиденья водителя и зеркал.",
            ),
            arrayOf(
                "Rest, driver seat and mirror positions.",
                "Отдых, положение сиденья водителя и зеркал.",
            ),
            arrayOf(
                "Second driver, driver seat and mirror positions.",
                "Второй водитель, положение сиденья водителя и зеркал.",
            ),
        )
        for (desc in seatDescs) {
            seatsSection.addButtons(
                buttons = listOf(
                    ButtonConfig("restore", "Восстановить"),
                    ButtonConfig("save", "Сохранить"),
                ),
                selectedValue = "restore",
                theme = theme,
                language = language,
                rightText = mapOf("en" to desc[0], "ru" to desc[1]),
            )
        }

        // Section: Driving
        val drivingSection = Section(this).apply {
            setTitle(mapOf("en" to "Driving", "ru" to "Движение"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(drivingSection)

        drivingSection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf("en" to "Save Driving Mode", "ru" to "Сохранять режим вождения"),
            description = mapOf(
                "en" to "Restores the previous driving mode when the car starts.\nUsed only for the guest account.",
                "ru" to "Восстанавливает предыдущий режим вождения при включении автомобиля.\nИспользуется только для гостевого аккаунта.",
            ),
        )
        drivingSection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf("en" to "Save Energy Mode", "ru" to "Сохранять режим энергии"),
            description = mapOf(
                "en" to "Restores the previous energy mode when the car starts.\nUsed only for the guest account.",
                "ru" to "Восстанавливает предыдущий режим энергии при включении автомобиля.\nИспользуется только для гостевого аккаунта.",
            ),
        )
        drivingSection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "Forced EV", "ru" to "Forced EV"),
            description = mapOf(
                "en" to "Used only in electric driving mode.\nEnables reduced ICE usage in hybrid mode (do not enable below 10% traction battery\ndischarge, do not enable during sharp acceleration).",
                "ru" to "Используется только при включённом режиме движения от электричества.\nВключает режим меньшего использования ДВС на гибриде (не включать до 10% разряда\nтяговой батареи, не включать при резких ускорениях).",
            ),
        )
        drivingSection.addCheckbox(
            checked = true,
            theme = theme,
            language = language,
            label = mapOf("en" to "Disable Pedestrian Warning Sound", "ru" to "Отключить звук предупреждения пешеходов"),
            description = mapOf(
                "en" to "Disables the \"flying saucer\" sound at low speed.\nA yellow icon appears in the top-left of the driver's cluster,\nindicating the sound is disabled.",
                "ru" to "Отключает звук «летающей тарелки» при движении на низкой скорости.\nНа приборке водителя появится жёлтый значок в левом верхнем углу,\nсигнализирующий о том, что звук отключен.",
            ),
        )

        // Section: Other
        val otherSection = Section(this).apply {
            setTitle(mapOf("en" to "Other", "ru" to "Другое"))
            setTheme(theme)
            setLanguage(language)
        }
        panel.addView(otherSection)

        otherSection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "Disable Activation QR Code on Startup", "ru" to "Отключить QR-код активации при запуске"),
            description = mapOf(
                "en" to "Used only for unactivated vehicles.\nDisables the QR code when the car starts.",
                "ru" to "Используется только для неактивированных машин.\nОтключает QR-код при запуске автомобиля.",
            ),
        )
        otherSection.addCheckbox(
            checked = false,
            theme = theme,
            language = language,
            label = mapOf("en" to "Enable OTA Firmware Update", "ru" to "Включить систему обновления прошивки автомобиля (OTA, Over The Air)"),
            description = mapOf(
                "en" to "Enable this option to receive car updates over the internet.\nCunBA may be removed during the update; recovery requires a laptop.",
                "ru" to "Включите эту опцию, если хотите получать обновления автомобиля через интернет.\nПри обновлении CunBA может быть удалена, восстановление с использованием ноутбука.",
            ),
        )

        return panel
    }
}
