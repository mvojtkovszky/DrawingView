# CHANGELOG

## 1.5.0 (2022-05-02)
* all properties in `DrawingViewState` are custom, allowing us to completely control serialization

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