package dev.abd3lraouf.custom.androidruler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.abd3lraouf.custom.androidruler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myScrollingValuePicker.setOnScrollChangedListener(
            listener = {
                binding.valueText.text = binding.myScrollingValuePicker.getReading(it).toString()
            }, throttleMillis = 250
        )

        binding.myScrollingValuePicker2.setOnScrollChangedListener(listener = {
            binding.valueText2.text = binding.myScrollingValuePicker2.getReading(it).toString()
        }, throttleMillis = 250)

        binding.myScrollingValuePicker2.moveTo(150F)
    }
}
