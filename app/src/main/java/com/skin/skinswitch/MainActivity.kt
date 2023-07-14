package com.skin.skinswitch

import android.app.Presentation
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.collector.isNight
import com.skin.skinswitch.const.AppConst
import com.skin.skinswitch.module.MainFragment
import java.util.*

val map = WeakHashMap<View, Int>()

class MainActivity : AppCompatActivity() {
    val path =
        Environment.getExternalStorageDirectory().absolutePath + "/skinPack-cartoon-debug - 副本.rar"

    override fun getResources(): Resources {
        return super.getResources()
    }

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
            customSkin.isChecked = true
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
        supportFragmentManager.beginTransaction()
            .add(R.id.container, MainFragment())
            .commit()


        var presentation: Presentation? = null
        findViewById<View>(R.id.view).setOnClickListener {
            //TestActivity.startActivity(this)
            /*if (presentation != null) {
                presentation?.dismiss()
                presentation = null
                return@setOnClickListener
            }
            getSystemService(DisplayManager::class.java).displays.getOrNull(1)?.let {
                object : Presentation(this, it) {
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        setContentView(R.layout.layout_presentaion)
                    }
                }.apply {
                    SkinManager.makeContextSkinAble(this.context)
                    show()
                    presentation = this
                }
            }*/
        }
    }
}
