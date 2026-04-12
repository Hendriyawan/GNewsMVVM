# 📰 GNews - Modern News App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture%20+%20MVVM-green.svg)](https://developer.android.com/topic/architecture)
[![Hilt](https://img.shields.io/badge/Dependency%20Injection-Hilt-orange.svg)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Database-Room-blue.svg)](https://developer.android.com/topic/libraries/architecture/room)
[![Retrofit](https://img.shields.io/badge/Networking-Retrofit-red.svg)](https://square.github.io/retrofit/)

<p align="center">
  <img src="GNewsSS/GnewsMVVM.jpg.jpg" width="200" />
</p>

**GNews** is a high-performance news application built with **Clean Architecture** and **MVVM** pattern. It fetches real-time news data from [NewsAPI.org](https://newsapi.org/) and provides a seamless user experience with local caching, dependency injection, and modern UI components.

---

## ✨ Key Features

- **Real-time News**: Get the latest headlines and breaking news globally.
- **Search & Explore**: Easily search for articles or discover news from specific publishers.
- **Category Filtering**: Browse news by categories (Business, Technology, Sports, etc.) using interactive Material Chips.
- **Offline Persistence**: Save your favorite articles using Room Database for offline reading.
- **Swipe-to-Refresh**: Update your news feed instantly with a simple gesture.
- **Clean UI/UX**: Minimalist design following Material Design 3 guidelines.

---

## 📸 Screenshots

<p align="center">
  <img src="GNewsSS/Screenshot_20260413_034616_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034628_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034640_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034648_GNews.jpg" width="200" />
</p>
<p align="center">
  <img src="GNewsSS/Screenshot_20260413_034701_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034744_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034803_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034813_GNews.jpg" width="200" />
</p>
<p align="center">
  <img src="GNewsSS/Screenshot_20260413_034826_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034835_GNews.jpg" width="200" />
  <img src="GNewsSS/Screenshot_20260413_034713_Chrome.jpg" width="200" />
</p>

---

## 🛠 Tech Stack

- **Kotlin**: Primary language for modern Android development.
- **Clean Architecture**: Ensures separation of concerns, scalability, and testability.
- **MVVM Pattern**: Facilitates clean UI logic and state management.
- **Dagger Hilt**: Standard library for Dependency Injection.
- **Retrofit & OkHttp**: Robust networking and API handling.
- **Room Database**: Local data persistence for caching "Saved" news.
- **Coroutines & Flow**: Reactive programming and asynchronous task handling.
- **Glide**: High-performance image loading and caching.
- **ViewBinding**: Type-safe access to UI components.

---

## 🏗 Project Structure

This project follows **Clean Architecture** principles, divided into three main layers:

```
app/src/main/java/com/hdev/gnews/
├── core/               # Shared utilities, Extensions, and Constants.
├── data/               # Data Layer: Implementation of Repositories.
│   ├── local/          # Room DB (DAO, Entity, Database class).
│   ├── remote/         # Retrofit API Service and DTO (Data Transfer Objects).
│   └── repository/     # Repository implementations (Logic for choosing Local vs Remote).
├── domain/             # Domain Layer: Pure Business Logic.
│   ├── model/          # Domain Models (POJO used by UI).
│   ├── repository/     # Repository Interfaces (Abstraction for Data Layer).
│   └── usecase/        # Specific business rules/actions.
└── presenter/          # Presentation Layer: UI Components (MVVM).
    ├── home/           # Home screen logic (Feed & Detail).
    ├── trends/         # Trending news and search functionality.
    ├── sources/        # News sources (publishers) catalog.
    └── saved/          # Offline saved articles management.
```

### Layer Responsibilities:
*   **Domain**: The most stable layer. It contains the business logic and rules. It is independent of any Android-specific libraries or frameworks.
*   **Data**: Orchestrates data from different sources (Remote API via Retrofit and Local Cache via Room).
*   **Presenter (UI)**: Manages UI states using ViewModels and displays data using Fragments/Activities. It communicates only with the Domain Layer via UseCases.

---

## 🚀 Getting Started

1.  **Clone the project**:
    ```sh
    git clone https://github.com/yourusername/GNews.git
    ```
2.  **Get an API Key**:
    Register at [NewsAPI.org](https://newsapi.org/) and copy your API key.
3.  **Setup API Key**:
    Add your API key to `local.properties`
    ```kotlin
    val API_KEY = "YOUR_KEY_HERE"
    ```
4.  **Build & Run**:
    Open in Android Studio and run on an Emulator or Physical Device.

---

**Developed by [Your Name]** - 2024
