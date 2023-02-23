package com.skin.skinswitch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.collector.isNight
import com.skin.skinswitch.const.AppConst

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val nightMode = findViewById<RadioButton>(R.id.radio_nightMode)
        val dayMode = findViewById<RadioButton>(R.id.radio_dayMode)
        val defaultSkin = findViewById<RadioButton>(R.id.radio_defaultSkin)
        val customSkin = findViewById<RadioButton>(R.id.radio_customSkin)
        if (resources.isNight()) {
            nightMode.isChecked = true
        } else {
            dayMode.isChecked = true
        }
        if (SkinManager.DEFAULT_THEME == SkinManager.getCurrentTheme()) {
            defaultSkin.isChecked = true
        } else {
            defaultSkin.isChecked = true
        }
        nightMode.setOnClickListener {
            SkinManager.applyThemeNight(true, null)
        }
        dayMode.setOnClickListener {
            SkinManager.applyThemeNight(false, null)
        }
        defaultSkin.setOnClickListener {
            SkinManager.switchTheme(SkinManager.DEFAULT_THEME)
        }
        customSkin.setOnClickListener {
            SkinManager.switchTheme(AppConst.THEME_CARTOON)
        }
    }

    companion object {
        @JvmStatic
        fun startActivity(ctx: Context) {
            ctx.startActivity(Intent(ctx, SecondActivity::class.java))
        }
    }
}
