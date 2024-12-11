# **🌶 CiMon - Chili Monitoring 🌶**
🌶 **CiMon** is an application designed to assist chili farmers 👩‍🌾👨‍🌾 in detecting diseases in their plants quickly and accurately using image recognition technology. By leveraging a smartphone camera 📸, users can capture photos of diseased chili plant leaves 🌿. CiMon offers several features that are incredibly helpful for both beginner and experienced farmers, including the following:

## **Tech Stacks**
* Design  & UI/UX
  - UI/UX : Figma, Prototype
  - Vector Design (Logo and Illustration) : Adobe Illustrator, Adobe Photoshop
  - Animation & SFX : After Effect, DaVinci Ressolve
* Mobile Development
  - Android Studio utilizing Native Kotlin language with SDK 26
  - XML Layout file providing dynamic and responsive layouts
  - MVVM ViewModel to manage UI-related data and handling business logic to ensure data persists during configuration changes
  - Offline-support utilizing Room database to store API responses locally, ensuring that users can access data even without an internet connection.
  - Firebase allows application runs in real-time using Firebase, which enables seamless integration with the internet and allows for dynamic updates.
  - Cloud Computing: The authentication APIs provided by Cloud Computing are used for forum, detection, blog, and other related functionalities.
  - Retrofit to retrieve API response and integrate it into the app, allowing seamless synchronization of data between the local Room database and remote servers for a consistent user experience.
  - Glide as image loading and caching library for efficient handling of images in your application.
  - uCrop Yalantis to crop an image based on respective resolution
  - TFLite to implement embedded machine learning model
  - Gemini API to implement chatbot feature utilizing Gemini AI
  - Coroutine support allowing background process to make apk run smoothly
  - Lottie Library to allow animation to be played on splash screen

## **Flow of the Apps**
1. User Interface: An attractive, dynamic, and user-friendly interface is created using XML layout files. The interface is designed to provide a smooth and intuitive experience for users.
2. User Authentication: The application integrates with Firebase Authentication allowing user to register with email or with google account. Users can register and log in to the application securely using their own private credentials.
3. Article Forum Retrieval: The application sends requests to APIs that provide articles or forum from others user allowing the app to be semi-media social. These APIs are utilized to retrieve the necessary information for display purposes.
4. Prediction Functionality: Predictions are embed on apk so user can use image classification with or without internet using TFLite model. The prediction then combined with API to retrieve more data about the prediction.
5. Real-time Functionality: The application utilizes Firebase to run in real-time. This allows for seamless integration with the internet and provides dynamic updates as new data becomes available.
6. Offline Functionality: Application mostly integrated with Room Database to store any API's that successfuly fetched to be used when connection lost

## **Main Features**
1. Diseases Detection 🔍: <br>
   Analyzes images of diseased chili plant leaves to provide an initial diagnosis.

3. Articles or Blogs 🌶📖: <br>
Provides comprehensive guides and information about chili cultivation, including essential tips and tricks.

3. ChatBot AI 🤖: <br>
Helps farmers get quick answers to questions related to chili cultivation and other relevant topics.

4. Farmer’s Discussion Forum 💬: <br>
A platform for farmers to share experiences, tips, and solutions for caring for chili plants.

5. Growth Tracker 📈: <br>
Allows farmers to record and monitor the growth and development of their chili plants independently, creating a personalized growth diary.

## **Instalation and Instruction**
Our application is based on Android OS. In order to use our application, you will need an Android device that can runs at least Android OS Version 8.0 (Oreo). You can download our application (.apk) from the release tab on github or by link down below. <br>

[CiMon APK Stable v1.0.0](https://github.com/CiMon-Capstone-Project/Mobile-Development/releases/tag/V1.0.0-stable)

We designing our app using figma. Here are the link of the Grand design, Lo-Fi, Hi-Fi, Prototype, and more. <br>
[CiMon Figma](https://www.figma.com/design/1WbH8Mr0UFSYiHrvQGHxNu/PROJECT-CiMon?node-id=0-1)

## **Depedency used :**
```
// image manipulation
implementation ("com.github.bumptech.glide:glide:4.16.0")

// viewmodel
implementation ("androidx.activity:activity-ktx:1.9.3")
implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

// uCrop
implementation ("com.github.yalantis:ucrop:2.2.8")
implementation ("de.hdodenhof:circleimageview:3.1.0")

// room
implementation ("androidx.room:room-runtime:2.6.1")
implementation ("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// coroutine support
implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// retrofit
implementation ("com.squareup.retrofit2:retrofit:2.11.0")
implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

// error handling
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// TF Lite
implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
implementation("com.google.android.gms:play-services-tflite-support:16.1.0")
implementation("com.google.android.gms:play-services-tflite-gpu:16.2.0")
implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")
implementation("org.tensorflow:tensorflow-lite-gpu:2.16.1")

// chatbot
implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

// data store
implementation("androidx.datastore:datastore-preferences:1.1.1")

// firebase related
implementation ("com.google.firebase:firebase-auth:23.1.0")
implementation ("com.google.android.gms:play-services-auth:21.2.0")
implementation ("androidx.credentials:credentials:1.3.0")

// splash screen animation
implementation ("com.airbnb.android:lottie:6.6.0")
```

## Contact 
If you have any questions, suggestions, or feedback, please feel free to reach out to us:
* **Rifa** Indra Setiawan [Linkedin](https://www.linkedin.com/in/rifa-indra-setiawan/)
* Ahmad **Syauqi** Taqiyuddin [Linkedin](https://www.linkedin.com/in/ahmadsyauqitaqiyuddin/)
 
