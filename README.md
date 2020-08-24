# DrawingView
A simple view to draw on supporting:
- undo, redo, eraser, clear
- set brush color and size
- set callback listeners for drawing and canvas state changes

<br/>
Simply create view in your code or in xml and you're good to go.
<br/>

## Nice! How do I get started?
Add it in your root build.gradle at the end of repositories:
``` gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

And make sure this is in your app build.gradle
``` gradle
dependencies {
  implementation 'com.github.mvojtkovszky:DrawingView:$latest_version'
}
```
