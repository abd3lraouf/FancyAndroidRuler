# Fancy android ruler
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fgjbae1212%2Fhit-counter&count_bg=%2379C83D&title_bg=%23555555&icon=googlefit.svg&icon_color=%23F3A2A2&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)  [![](https://jitpack.io/v/AbdElraoufSabri/FancyAndroidRuler.svg)](https://jitpack.io/#AbdElraoufSabri/FancyAndroidRuler) [![API](https://img.shields.io/badge/API-19%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=19)  [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

## Ruler in action

This image is from one of my currently under-development apps
### RTL Support

[![Length][1]][1]

### LTR Support _by default_
[![enter image description here][2]][2]

## Usage
**Step 1.** Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency: Latest version: [![](https://jitpack.io/v/AbdElraoufSabri/FancyAndroidRuler.svg)](https://jitpack.io/#AbdElraoufSabri/FancyAndroidRuler)

```groovy
dependencies {
        implementation 'com.github.AbdElraoufSabri:FancyAndroidRuler:<VERSION>'
}
```

## Usage
### XML
```xml
    <TextView
        android:id="@+id/value_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textSize="32sp"
        tools:text="1" />

    <dev.abd3lraouf.custom.ruler.FancyRuler
        android:id="@+id/myScrollingValuePicker"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rulerMinValue="15"
        app:rulerMaxValue="108"
        app:rulerDefaultPosition="107.75"
        app:rulerSystem="imperial"
        app:rulerPointerColor="#FF0000FF"
        app:rulerNormalUnitBarColor="#FF0000FF"
        app:rulerMiddleUnitBarColor="#00BCD4"
        app:rulerMiddleUnitTextColor="#00BCD4"
        app:rulerQuarterUnitBarColor="#E91E63"
        app:rulerQuarterUnitTextColor="#E91E63"
        app:rulerThreeQuartersUnitBarColor="#FFEB3B"
        app:rulerThreeQuartersUnitTextColor="#FFEB3B"
        app:rulerMainUnitBarColor="#4CAF50"
        app:rulerMainUnitTextColor="#4CAF50"
        app:rulerBackgroundColor="#000"
        />
```

### Kotlin
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myScrollingValuePicker.setOnScrollChangedListener({
            value_text.text = myScrollingValuePicker.getReading(it).toString()
        }, 250)
    }
}

```
## All available themes
### RTL Support

[![RTL][3]][3]

### LTR Support _by default_

[![LTR][4]][4]


  [1]: https://i.stack.imgur.com/sPlfr.gif
  [2]: https://i.stack.imgur.com/TYVLr.gif
  [3]: https://i.stack.imgur.com/nA2hK.png
  [4]: https://i.stack.imgur.com/uGiTD.png
