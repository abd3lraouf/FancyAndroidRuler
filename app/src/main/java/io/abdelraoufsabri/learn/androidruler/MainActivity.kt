package io.abdelraoufsabri.learn.androidruler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myScrollingValuePicker.setOnScrollChangedListener({
            value_text.text = myScrollingValuePicker.getReading(it).toString()
        }, 250)

        myScrollingValuePicker2.setOnScrollChangedListener({
            value_text2.text = myScrollingValuePicker2.getReading(it).toString()
        }, 250)
    }
}
