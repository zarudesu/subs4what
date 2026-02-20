# Subs4What — Setup Guide

Полный пошаговый гайд по запуску приложения "Subscription for Nothing".

---

## Содержание

1. [Обзор проекта](#1-обзор-проекта)
2. [Требования](#2-требования)
3. [Шаг 1: Firebase — создание проекта](#3-шаг-1-firebase--создание-проекта)
4. [Шаг 2: Firebase — Anonymous Auth](#4-шаг-2-firebase--anonymous-auth)
5. [Шаг 3: Firebase — Firestore](#5-шаг-3-firebase--firestore)
6. [Шаг 4: Скачать google-services.json](#6-шаг-4-скачать-google-servicesjson)
7. [Шаг 5: Открыть проект в Android Studio](#7-шаг-5-открыть-проект-в-android-studio)
8. [Шаг 6: Первая сборка](#8-шаг-6-первая-сборка)
9. [Шаг 7: Google Play Console — подписка](#9-шаг-7-google-play-console--подписка)
10. [Шаг 8: Тестирование](#10-шаг-8-тестирование)
11. [Архитектура проекта](#11-архитектура-проекта)
12. [FAQ / Troubleshooting](#12-faq--troubleshooting)

---

## 1. Обзор проекта

**Subs4What** — сатирическое Android-приложение, которое *ничего не делает* за подписку $1/месяц.

Приложение максимально серьёзно и пафосно преподносит полное отсутствие функционала как премиальный продукт. Пользователь получает уникальный глобальный номер участника "клуба ничего".

### Что внутри:
- 4 экрана: Welcome → Subscribe → Nothing → Member Card
- Google Play Billing v7 (реальная подписка $1/мес)
- Firebase Anonymous Auth (без логина/пароля)
- Firebase Firestore (глобальный счётчик участников)
- Jetpack Compose + Material 3 (тёмная тема, золотые акценты)

---

## 2. Требования

| Что | Версия | Проверка |
|-----|--------|----------|
| Android Studio | Ladybug+ (2024+) | Установлен ✓ |
| Android SDK | Platform 35 | Установлен ✓ |
| JDK | 17+ (встроен в Studio) | JBR 21 ✓ |
| Gradle | 8.11.1 (wrapper) | Настроен ✓ |
| Аккаунт Google | Для Firebase Console | Нужен |
| Google Play Console | Для подписки ($25 разово) | Нужен для billing |

---

## 3. Шаг 1: Firebase — создание проекта

1. Открой https://console.firebase.google.com/
2. Нажми **"Create a project"** (или "Add project")
3. Введи имя: `Subs4What`
4. Google Analytics — можно отключить (не нужен), нажми **Create project**
5. Дождись создания, нажми **Continue**

### Добавить Android-приложение:

6. На главной странице проекта нажми иконку **Android** (робот)
7. Заполни:
   - **Android package name**: `com.subs4what.app`
   - **App nickname**: `Subs4What`
   - **Debug signing certificate SHA-1**: пока пропусти (нажми "Register app")
8. Нажми **Register app**

> Не закрывай эту страницу — следующий шаг будет скачивание файла.

---

## 4. Шаг 2: Firebase — Anonymous Auth

1. В Firebase Console → левое меню → **Build** → **Authentication**
2. Нажми **Get started**
3. Во вкладке **Sign-in method** найди **Anonymous**
4. Нажми на него → переключи **Enable** → **Save**

Готово. Теперь приложение может авторизовать пользователей без логина.

---

## 5. Шаг 3: Firebase — Firestore

### Создать базу:

1. Firebase Console → **Build** → **Firestore Database**
2. Нажми **Create database**
3. Выбери расположение (ближайшее к тебе, например `europe-west1`)
4. Security rules: выбери **Start in test mode** (потом поправим)
5. Нажми **Create**

### Создать начальный документ для счётчика:

6. В Firestore нажми **Start collection**
7. Collection ID: `counters`
8. Document ID: `members`
9. Добавь поле:
   - Field name: `count`
   - Type: `number`
   - Value: `0`
10. Нажми **Save**

### Настроить правила безопасности:

11. Перейди на вкладку **Rules**
12. Замени содержимое на:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Счётчик участников
    match /counters/{document} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }

    // Участники — каждый видит только свой документ
    match /members/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Маппинг подписок → номеров (для restore после переустановки)
    match /subscriptions/{tokenHash} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

13. Нажми **Publish**

---

## 6. Шаг 4: Скачать google-services.json

1. Firebase Console → шестерёнка (⚙️) рядом с "Project Overview" → **Project settings**
2. Прокрути вниз до секции **Your apps**
3. Найди Android-приложение `com.subs4what.app`
4. Нажми **google-services.json** → скачается файл
5. **Перемести файл** в папку проекта:

```bash
mv ~/Downloads/google-services.json ~/Projects/subs4what/app/
```

> ВАЖНО: файл должен лежать именно в `app/`, НЕ в корне проекта!

### Проверь:

```bash
ls -la ~/Projects/subs4what/app/google-services.json
```

Должен быть файл ~2-4 KB.

---

## 7. Шаг 5: Открыть проект в Android Studio

1. Открой **Android Studio**
2. **File** → **Open...**
3. Выбери папку: `/Users/zardes/Projects/subs4what`
4. Нажми **Open**
5. Android Studio начнёт **Gradle Sync** — подождите 1-3 минуты
6. Внизу должно появиться: **"BUILD SUCCESSFUL"**

### Если sync упал:

- Проверь что `google-services.json` лежит в `app/`
- **File** → **Invalidate Caches** → **Invalidate and Restart**
- **File** → **Sync Project with Gradle Files**

---

## 8. Шаг 6: Первая сборка

### Вариант A: Через Android Studio

1. Подключи телефон по USB (включи USB Debugging) или запусти эмулятор
2. Вверху выбери устройство в dropdown
3. Нажми зелёную кнопку **Run** (▶️) или `Ctrl+R`
4. Дождись сборки и установки

### Вариант B: Через терминал

```bash
# Из корня проекта
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
  ./gradlew assembleDebug
```

APK будет в: `app/build/outputs/apk/debug/app-debug.apk`

Установить на подключённый телефон:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Что ты увидишь при первом запуске:

```
┌─────────────────────────────────┐
│                                 │
│       ◯  (пульсирующий)        │
│                                 │
│  Welcome to the                 │
│  Premium Nothing                │
│  Experience™                    │
│                                 │
│  Finally, an app that promises  │
│  nothing. And delivers.         │
│                                 │
│  For just $1/month, you get     │
│  absolutely nothing.            │
│  You're welcome.                │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ BEGIN YOUR JOURNEY INTO     │ │
│ │ NOTHING                     │ │
│ └───── (golden shimmer) ──────┘ │
│                                 │
└─────────────────────────────────┘
```

> Billing пока не будет работать без настройки Google Play Console (см. шаг 7).

---

## 9. Шаг 7: Google Play Console — подписка

Этот шаг нужен для реальной оплаты. Без него приложение работает, но кнопка "Subscribe" выдаст ошибку.

### Предварительно:

- Нужен аккаунт Google Play Developer ($25 единоразово): https://play.google.com/console/signup
- Нужен хотя бы internal testing track

### Создать приложение:

1. Google Play Console → **Create app**
2. Заполни:
   - App name: `Subs4What`
   - Default language: English
   - App or game: App
   - Free or paid: Free (подписка — это in-app)
3. Согласись с политиками → **Create app**

### Загрузить AAB для Internal Testing:

4. Собери release bundle:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
  ./gradlew bundleRelease
```

> Для release нужна подпись. Создай keystore в Android Studio:
> **Build** → **Generate Signed Bundle/APK** → **Android App Bundle** → **Create new...**

5. Play Console → **Testing** → **Internal testing** → **Create new release**
6. Загрузи AAB из `app/build/outputs/bundle/release/`
7. Нажми **Review release** → **Start rollout**

### Создать подписку:

8. Play Console → **Monetize** → **Subscriptions**
9. **Create subscription**:
   - Product ID: `nothing_monthly`  ← ИМЕННО ЭТО, совпадает с кодом
   - Name: `Nothing Premium`
10. Добавь **Base plan**:
    - Billing period: **1 month**
    - Price: **$1.00** (или эквивалент)
    - Auto-renewing
11. **Activate** подписку

### Добавить тестеров:

12. Play Console → **Testing** → **Internal testing**
13. Нажми **Testers** → создай email list
14. Добавь свой Google-аккаунт (тот, что на телефоне)
15. Скопируй **opt-in URL** и открой его на телефоне — подтверди участие

### License Testing (необязательные платежи):

16. Play Console → **Settings** → **License testing**
17. Добавь свой email
18. Response: **RESPOND_NORMALLY** или **LICENSED**

> Теперь при покупке подписки с тестового аккаунта деньги НЕ списываются.

---

## 10. Шаг 8: Тестирование

### Тест 1: Навигация (без billing)

1. Запусти приложение
2. Увидишь Welcome Screen с анимацией
3. Нажми "BEGIN YOUR JOURNEY INTO NOTHING"
4. Увидишь Subscribe Screen с ценой и фичами

### Тест 2: Подписка (нужен Play Console)

1. На Subscribe Screen нажми "SUBSCRIBE TO NOTHING"
2. Появится Google Play payment sheet
3. Подтверди покупку (тестовый аккаунт — бесплатно)
4. Приложение перейдёт на Nothing Screen
5. Увидишь свой уникальный номер: "Member #1 of the Void"

### Тест 3: Member Card

1. На Nothing Screen нажми "VIEW MEMBER CARD"
2. Увидишь золотую VIP-карточку с номером
3. Нажми "SHARE YOUR NOTHING" — откроется шаринг

### Тест 4: Перезапуск

1. Закрой приложение
2. Открой заново
3. Должен сразу попасть на Nothing Screen (данные кэшируются)

### Тест 5: Firebase

1. Открой Firebase Console → Firestore
2. Проверь:
   - `counters/members` → `count` = 1
   - `members/{uid}` → документ с `memberNumber`, `createdAt`, `subscriptionActive`

---

## 11. Архитектура проекта

```
subs4what/
├── build.gradle.kts              ← Root Gradle (плагины)
├── settings.gradle.kts           ← Модули и репозитории
├── gradle.properties             ← JVM args, AndroidX
├── local.properties              ← Путь к SDK (не коммитить!)
├── gradlew / gradlew.bat         ← Gradle Wrapper
│
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml        ← Version Catalog (все версии тут)
│
└── app/
    ├── build.gradle.kts          ← App Gradle (зависимости, SDK)
    ├── google-services.json      ← Firebase config (не коммитить!)
    ├── proguard-rules.pro        ← ProGuard для release
    │
    └── src/main/
        ├── AndroidManifest.xml   ← Permissions, Activity
        │
        ├── java/com/subs4what/app/
        │   ├── SubsApp.kt               ← Application: Firebase init
        │   ├── MainActivity.kt           ← Single Activity, навигация
        │   │
        │   ├── navigation/
        │   │   └── NavGraph.kt           ← 4 маршрута, Compose Navigation
        │   │
        │   ├── ui/
        │   │   ├── theme/
        │   │   │   ├── Color.kt          ← Gold, VoidBlack, палитра
        │   │   │   ├── Type.kt           ← Типографика (Black, Bold, Light)
        │   │   │   └── Theme.kt          ← Material 3 dark theme
        │   │   │
        │   │   ├── screens/
        │   │   │   ├── WelcomeScreen.kt  ← Онбординг с анимацией
        │   │   │   ├── SubscribeScreen.kt← Billing + список "фич"
        │   │   │   ├── NothingScreen.kt  ← Главный экран пустоты
        │   │   │   └── MemberCardScreen.kt← VIP-карточка + Share
        │   │   │
        │   │   └── components/
        │   │       ├── GoldButton.kt     ← Shimmer-кнопка
        │   │       ├── MemberBadge.kt    ← Бейдж "#00042"
        │   │       └── NothingAnimation.kt← Пульсирующие круги
        │   │
        │   ├── billing/
        │   │   ├── BillingManager.kt     ← Google Play Billing wrapper
        │   │   └── BillingViewModel.kt   ← State: Loading→NotSubscribed→Subscribed
        │   │
        │   ├── data/
        │   │   ├── PreferencesManager.kt ← DataStore (кэш номера)
        │   │   └── MemberRepository.kt   ← Firebase + кэш координация
        │   │
        │   └── firebase/
        │       └── FirebaseService.kt    ← Anon Auth + Firestore транзакция
        │
        └── res/
            ├── drawable/
            │   ├── ic_launcher_background.xml ← Чёрный фон иконки
            │   └── ic_launcher_foreground.xml ← Золотой круг + $
            ├── mipmap-anydpi-v26/
            │   ├── ic_launcher.xml       ← Adaptive icon
            │   └── ic_launcher_round.xml
            └── values/
                ├── strings.xml           ← Имя приложения
                └── themes.xml            ← XML-тема (splash/status bar)
```

### Поток данных:

```
┌──────────┐    ┌────────────────┐    ┌─────────────────┐
│ UI Screen│───→│ BillingViewModel│───→│ BillingManager   │
│ (Compose)│    │   (StateFlow)  │    │ (Play Billing)   │
└──────────┘    └───────┬────────┘    └─────────────────┘
                        │
                        ▼
                ┌───────────────┐
                │MemberRepository│
                └───────┬───────┘
                   ┌────┴────┐
                   ▼         ▼
           ┌────────────┐ ┌──────────────┐
           │Preferences │ │FirebaseService│
           │ (DataStore)│ │(Auth+Firestore│
           └────────────┘ └──────────────┘
```

---

## 12. FAQ / Troubleshooting

### "File google-services.json is missing"
→ Скачай из Firebase Console и положи в `app/` (шаг 4)

### "No matching client found for package name"
→ Package name в `google-services.json` не совпадает с `com.subs4what.app`. Пересоздай приложение в Firebase Console с правильным package name.

### Billing: "Item not found" или "Error: 3"
→ Подписка `nothing_monthly` не создана в Play Console, или приложение не опубликовано в internal testing track.

### Billing: вообще не показывает payment sheet
→ Убедись, что:
  - На устройстве залогинен Google-аккаунт из списка тестеров
  - APK/AAB подписан тем же ключом, что и в Play Console
  - Подписка `nothing_monthly` в статусе **Active**

### Firebase: "PERMISSION_DENIED"
→ Проверь Firestore Security Rules (шаг 3). Убедись, что Anonymous Auth включён (шаг 2).

### Приложение крашится при старте
→ Проверь Logcat в Android Studio. Скорее всего отсутствует `google-services.json` или неверный package.

### Как собрать release APK?
```bash
# Создай keystore (один раз):
# Android Studio → Build → Generate Signed Bundle/APK → Create new...

# Затем:
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
  ./gradlew assembleRelease
```

### Что не коммитить в git:
```
google-services.json   ← Firebase credentials
local.properties       ← Локальный путь к SDK
*.jks / *.keystore     ← Ключи подписи
```

---

## Быстрый старт (TL;DR)

```bash
# 1. Создай Firebase проект, включи Anon Auth + Firestore
# 2. Скачай google-services.json в app/
# 3. Создай документ counters/members {count: 0} в Firestore
# 4. Открой в Android Studio:
open -a "Android Studio" ~/Projects/subs4what

# 5. Подожди Gradle Sync
# 6. Run на эмуляторе/устройстве
# 7. (Опционально) Настрой Play Console для реального billing
```
