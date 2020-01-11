package io.abdelraoufsabri.learn.androidruler

import android.os.Bundle
import android.view.SoundEffectConstants
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myScrollingValuePicker.setOnScrollChangedListener({
            value_text.text = myScrollingValuePicker.getReading(it).toString()
        }, 1000)
    }

}
