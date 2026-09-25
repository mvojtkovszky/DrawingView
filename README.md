# DrawingView
A simple view to draw on supporting:
- undo, redo, eraser, clear
- set brush color and size
- set callback listeners for drawing and canvas state changes

<br/>
Simply create view in your code or in xml and you're good to go.
<br/>

## Nice! How do I get started?
Make sure root build.gradle repositories include JitPack
``` gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

And DrawingView dependency is added to app build.gradle
``` gradle
dependencies {
    implementation 'com.github.mvojtkovszky:DrawingView:$latest_version'
}
```

## Keep strokes inside the view

By default, DrawingView preserves its original behavior and accepts coordinates from a gesture
that moves outside its bounds. Enable clamping when adjacent UI must never contain drawing data:

```xml
<com.vojtkovszky.drawingview.DrawingView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:clampDrawingToBounds="true" />
```

Or configure it programmatically:

```kotlin
drawingView.clampDrawingToBounds = true
```
