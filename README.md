# App Actions Visualizer

## Background
App Actions was [announced](https://developers.google.com/assistant/app/) at Google I/O 2018 as a program for Android apps to integrate with Google and Assistant. With App Actions, Android apps can sign up for fulfilling user queries in the Assistant by including a short actions.xml configuration file in their APK. The [actions.xml](https://developers.google.com/assistant/app/action-schema) file associates deep links in the app with semantic intents (AoG [built-in intents](https://developers.google.com/assistant/app/reference/built-in-intents)). Deep links can be provided either as a URL template with placeholders or as an “entity set reference”, a pointer for Google to discover relevant deep links in app-linked Web content or vertical-specific feeds.

## Project description
This project is to build an Android app that could help showcase the all the fulfillment options/deeplinks various Android apps provide to App Actions in an organized and detailed way.

## Major functionality
For different build-in intent categories(common; finance; food and drink; health and fitness; transportation), App Actions Visualizer could present 
1. all the Android apps that provide fulfillment app actions in this category,
2. For each Android app, App Actions Visualizer can present the content of the deeplinks and what parameters are required to complete the deeplink
3. After user input/select all the required parameter, App Actions Visualizer could fire the actual deeplink and trigger corresponding fulfillment in the Android app


**This is not an officially supported Google product.**

















































