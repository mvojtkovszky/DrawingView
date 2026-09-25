# CHANGELOG

## 1.7.1 (unreleased)
* Render tap-only gestures as solid round-capped strokes instead of hollow circles.

## 1.7.0 (2026-09-25)
* Add optional XML/programmatic clamping of drawing gestures to the view bounds.
* Bump Gradle to 9.7.1, Android Gradle Plugin to 9.4.1, Kotlin to 2.4.20,
  Dokka to 2.2.0, kotlinx.serialization to 1.11.0, and AndroidX dependencies.
* Bump build tools, compile SDK, and target SDK to 37 and Java to 21.

## 1.6.0 (2024-11-04)
* All `DrawingViewState` objects now adopt `@Serializable` annotation.
* Make `DrawingViewState.isHistoryEmpty`, `DrawingViewState.isUndoneEmpty`,
  `DrawingViewState.numHistorySteps` and `DrawingViewState.numUndoneSteps` public.
* bump Gradle plugin to 8.7.2, Kotlin to 2.0.20
* bump buildToolsVersion 35.0.0, targetSdkVersion, compileSdkVersion to 35

## 1.5.1 (2023-08-31)
* bump Gradle plugin to 8.1.1, Kotlin to 1.9.0
* bump buildToolsVersion 34.0.0, targetSdkVersion, compileSdkVersion to 34
* bump core-ktx to 1.10.1, appcompat to 1.6.1, material to 1.9.0

## 1.5.0 (2022-05-03)
* all properties in `DrawingViewState` are custom, allowing us to fully control serialization

## 1.4.0 (2022-04-29)
* add `DrawingViewState` containing all the needed information to draw image on canvas
* bump Kotlin to 1.6.21, Gradle plugin to 7.1.3
* bump buildToolsVersion to 32.0.0, compileSdkVersion and targetSdkVersion to 32

## 1.3.0 (2020-08-05)
* bump Gradle plugin to 7.0.0
* update publish scripts

## 1.2.0 (2021-03-24)
* add `undoAll()`, `redoAll()`, `clearRedoHistory()`, `isDrawingEmpty()`
* add explicit callbacks parameter names to `listenerEmptyState` and `listenerDrawingInProgress`
* add option to define canvas colour
* remove reference to additional canvas
* better documentation of public methods and parameters

## 1.1.0 (2021-03-23)
* add `sizeChanged` flag to determine when to create new canvas instead of creating it from 
  bitmap on every size change
* bump Kotlin to 1.4.31, Gradle plugin to 4.1.3, Build tools to 30.0.3

## 1.0.2 (2020-12-05)
* add documentation
* remove deprecated Kotlin extensions from example app
* bump to Kotlin 1.4.20 and Gradle plugin 4.1.1

## 1.0.1 (2020-08-24)
* add license
* bump Kotlin to 1.4.0, Gradle plugin to 4.0.1 and build tools to 30.0.2

## 1.0.0 (2020-05-26)
* initial public release
