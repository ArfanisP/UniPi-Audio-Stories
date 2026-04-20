[UniPi-Audio-Stories-README.md](https://github.com/user-attachments/files/26899858/UniPi-Audio-Stories-README.md)
# UniPi Audio Stories

**UniPi Audio Stories** is an Android application designed for reading and narrating children's stories using Text-to-Speech (TTS) technology.

## 📖 Overview

The application allows users to browse a list of stories, view their accompanying illustrations and text, and listen to the narration directly through their device. Furthermore, the application persistently tracks user engagement by recording listening sessions in a cloud database and presenting usage statistics.

## ✨ Key Features

* **Story Library:** A comprehensive list of stories fetched dynamically from the cloud, where users can browse and select their preferred tale.
* **Interactive Story View:** Displays the story's title, author, cover image, and the full text content.
* **Text-to-Speech (TTS):** Utilizes the native Android speech engine. Users can start, pause, and stop the narration, as well as adjust the audio volume.
* **Personalized Statistics:** Tracks the total number of story listening sessions per user and presents these metrics in a dedicated statistics view.
* **Multilingual Interface:** The UI automatically adapts its language (English as default, Greek, and Spanish) based on the device's system settings.

## 🏗️ Architecture

The application is structured around three primary Activities:

1. **`MainActivity`:** Responsible for fetching and displaying the library of stories from Firestore using a `RecyclerView`.
2. **`StoryActivity`:** Handles the presentation of individual story details, controls the Text-to-Speech engine for narration, and logs each completed listening session to the database.
3. **`StatsActivity`:** Retrieves and visualizes the user's specific usage statistics, such as the total count of stories listened to.

## 🛠️ Technology Stack

* **Environment:** Android Studio
* **Language:** Java
* **Backend as a Service (BaaS):** * Firebase Firestore (NoSQL Database)
  * Firebase Authentication
* **Image Loading:** Glide
* **Audio:** Native Android Text-to-Speech API

## 🗄️ Database Structure (Firebase)

The app leverages **Firebase Firestore** for data storage and **Firebase Authentication** (Anonymous Sign-In) to securely manage user data without requiring account creation.

* **Collections:**
  * `stories/`: Contains document entries for each story (fields: `title`, `author`, `year`, `ImageUrl`, `text`).
  * `users/`: Contains document entries for anonymous users.
    * **Sub-collection:** `listens/`: Nested under each user document, recording individual listening events.

**Security Rules:**
* Stories are publicly readable but strictly write-protected from the client app.
* User statistics (`listens`) are strictly isolated; a user can only read and write to their own specific records based on their authenticated ID.

## 📝 Ethical Note & Content Disclaimer

For the purpose of this academic project, the texts of the stories were entirely generated using Artificial Intelligence. The accompanying images are random selections retrieved from the internet by the AI. Additionally, metadata such as the "author", "title", and "publication year" are entirely fictional.

---
**Author:** Panagiotis Arfanis (Π22017)  
**Date:** February 2026
