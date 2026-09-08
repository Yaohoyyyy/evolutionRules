# AGENT.md — evolutionRules

## Проект
Android-приложение «Правила Эволюции» — справочник свойств.
Пакет `com.example.evolutionrules`. Язык UI — русский.

## Стек
- Kotlin, Jetpack Compose + Material3, Adaptive NavigationSuite
- `compileSdk/targetSdk 36`, `minSdk 31`, Java 11
- Данные: `assets/properties/properties.json` -> `JSONObject` (без kotlinx.serialization)

## Структура
- `src/main/java/com/example/evolutionrules/MainActivity.kt` — `EvolutionRulesApp()`, навигация `selectedEntry: PropertyEntry?`, `rememberEntries()`
- `HomeScreen.kt` — поиск + `LazyColumn` + `ListItem`. Поиск через `String.normalized()` (lowercase + ё→е)
- `DetailScreen.kt` — детали, `BackHandler`, инлайн-иконки в тексте через `InlineTextContent`
- `data/PropertyEntry.kt` — `name, theme, description`
- `data/PropertyRepository.kt` — `loadEntries(context)`, сортировка `sortedBy name`
- `ui/theme/` — `Theme.kt, Color.kt, Type.kt`
- Ресурсы: `ic_home, ic_animal (Ж), ic_food_red (КК), ic_food_blue (СК), ic_food_yellow (ЖК)`

## Токены в DetailScreen
- `КК, СК, ЖК` заменяются на иконки всегда.
- `Ж` / `ЖЖ` -> иконка животного только если дальше пробел/разделитель/конец (`ANIMAL_BOUNDARY`, `spaceOrEnd()`).
- Логика: `parseSegments()` -> `buildDescriptionAnnotatedString()`.

## Команды
```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
.\gradlew.bat lint
.\gradlew.bat test
```

## Эмулятор
- АВД лежит на D: (`D:\Android\avd\`), переменная `ANDROID_AVD_HOME=D:\Android\avd`.
- Запускать эмулятор нужно **всегда с диска D** (на C: не хватает места):
  ```powershell
  $env:ANDROID_AVD_HOME = "D:\Android\avd"
  Start-Process -FilePath "D:\Work\Soft\Android\Sdk\emulator\emulator.exe" -ArgumentList "-avd Pixel_6_API_31 -no-snapshot -no-boot-anim"
  ```
- Установка APK и запуск:
  ```powershell
  & "D:\Work\Soft\Android\Sdk\platform-tools\adb.exe" install -r "build\outputs\apk\debug\evolutionRules-debug.apk"
  & "D:\Work\Soft\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.evolutionrules/.MainActivity
  ```

## Правила для агента
1. UI-строки только на русском.
2. Поиск обязан оставаться ё-толерантным (`normalized()`).
3. Новые свойства — только в `assets/properties/properties.json` с полями `name/theme/description`, не хардкодить в Kotlin.
4. Новые токены фишек/символов — добавлять в `ALWAYS_TOKENS` или `ANIMAL_BOUNDARY` + в `inlineMap`, с `contentDescription` на русском.
5. Навигацию держать простой (один экран `selectedEntry`), `rememberSaveable` для `query`.
6. Не понижать `minSdk`, не менять namespace без запроса.
7. Запуск эмулятора — только через `D:\Android\avd\` (на C: не хватает места).
