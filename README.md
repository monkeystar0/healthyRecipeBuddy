# HealthyRecipeBuddy

Android app that helps users track nutrition, understand body metrics, and get personalized healthy recipes. It combines local storage (Room), health calculations (BMI, body fat, calorie targets), and **Google Gemini** (`gemini-1.5-pro-latest`) for friendly greetings and recipe generation.

## Project description

HealthyRecipeBuddy is a Kotlin **single-module** app (`app`) built with View Binding, Material components, and coroutines. First launch walks users through profile setup; the home screen shows BMI and body fat (with color-coded cards), estimated daily calories, a donut chart of today’s intake versus target, and an AI-generated buddy message tailored to the user’s stats. Food logging and AI features read the same profile and today’s log so recommendations stay consistent with actual intake.

---

## Features

| Area | Description |
|------|-------------|
| **Onboarding & profile** | `UserProfileSettingUpActivity` collects weight, height, age, gender, and related fields until the profile is marked complete. `UserProfileEditActivity` lets users update that information later. |
| **Dashboard** | `MainActivity` computes BMI, body fat category, and maintenance calories from profile and activity level. A donut chart (Futured Donut) compares **calories needed** to **today’s logged total**. Gemini produces a short, friendly greeting and eating tips based on BMI and body fat. |
| **Set target** | `SetTargetActivity` stores goal weight and activity level used for target BMI/body fat previews and calorie math elsewhere in the app. |
| **Food log** | `FoodLogActivity` logs meals, desserts, and drinks with calories into a Room database (`FoodLogDatabase`). The same day’s entries power the dashboard donut and recipe prompts. `FoodLogHistoryActivity` exposes historical logs. |
| **Recommended recipe** | `RecommendedRecipeActivity` asks Gemini for a meal idea using profile metrics, today’s food list, intake calories, and calorie target. Users can regenerate for a new suggestion, then save the text under a custom name. |
| **Create recipe** | `CreateRecipeActivity` builds recipes from a user-defined ingredient list (with optional quantity/unit), scoped to meal type (meal / dessert / drink). Gemini returns a structured recipe; users can save it like a recommended recipe. |
| **Saved recipes** | `SavedRecipeActivity` lists recipes persisted in `SavedRecipeDatabase` (name, date, full text from AI screens). |

---

## Tech stack

- **Language:** Kotlin  
- **UI:** AppCompat, Material, View Binding, ConstraintLayout  
- **Storage:** Room (food log + saved recipes), `SharedPreferences` for profile and targets  
- **AI:** [Google AI SDK for Android](https://github.com/google/generative-ai-android) (`com.google.ai.client.generativeai`)  
- **Charts:** [Donut](https://github.com/futuredapp/donut) (`app.futured.donut`)  
- **Build:** Android Gradle Plugin 8.5.x, `compileSdk` / `targetSdk` 34, `minSdk` 26  

---

## Configuration

1. Copy the template and add your key:

   ```bash
   cp app/src/main/res/raw/secrets.properties.example app/src/main/res/raw/secrets.properties
   ```

2. Edit `secrets.properties` so it contains one line (no quotes):

   ```text
   API_KEY=your_gemini_api_key_here
   ```

The app reads the value after `=` (see `MainViewModel`). `secrets.properties` is listed in `.gitignore` and must not be committed.

### If this repository was ever private with a real key committed

**Rotate the key** in [Google AI Studio](https://aistudio.google.com/apikey) (or Google Cloud) and revoke the old one. Git history can still contain past file contents; ignoring the file only affects future commits. To remove secrets from history you would need a tool such as [`git filter-repo`](https://github.com/newren/git-filter-repo) and a force-push, which rewrites all clones—rotating the key is the essential step before going public.

---

## Build

Open the project in Android Studio and run the `app` configuration, or from the project root:

```bash
./gradlew assembleDebug
```
