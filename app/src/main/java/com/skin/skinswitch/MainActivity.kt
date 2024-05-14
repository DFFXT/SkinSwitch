package com.skin.skinswitch

import android.app.Presentation
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.PopupMenuCompat
import com.CustomV
import com.example.skinswitch.R
import com.skin.log.Logger
import com.skin.skincore.OnThemeChangeListener
import com.skin.skincore.SkinManager
import com.skin.skincore.collector.isNight
import com.skin.skincore.provider.MergeResource
import com.skin.skinswitch.const.AppConst
import com.skin.skinswitch.module.MainFragment
import java.util.WeakHashMap

val map = WeakHashMap<View, Int>()

class MainActivity : AppCompatActivity(), OnThemeChangeListener {
    val path =
        Environment.getExternalStorageDirectory().absolutePath + "/skinPack-cartoon-debug - 副本.rar"

    private val dayMode by lazy {
        findViewById<RadioButton>(R.id.radio_dayMode)
    }
    private val nightMode by lazy {
        findViewById<RadioButton>(R.id.radio_nightMode)
    }

    override fun onThemeChanged(theme: Int, isNight: Boolean, eventType: IntArray) {
        notifyRadioButton()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SkinManager.addSkinChangeListener(this)
        setContentView(R.layout.activity_main)
        val defaultSkin = findViewById<RadioButton>(R.id.radio_defaultSkin)
        val customSkin = findViewById<RadioButton>(R.id.radio_customSkin)
        val btnDialog = findViewById<View>(R.id.btn_dialog)
        // findViewById<ViewGroup>(R.id.root).addView(CustomV(this))

        val btnPopupWindow = findViewById<View>(R.id.btn_popupWindow)

        notifyRadioButton()

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

        btnDialog.setOnClickListener {
            AppCompatDialog(this, R.style.Dialog).apply {
                setContentView(R.layout.test_dialog)
                val btn = findViewById<View>(R.id.tv_switch)
                btn?.setOnClickListener {
                    SkinManager.applyThemeNight(!SkinManager.isNightMode())
                }
            }.show()
        }

        val popupWindow = PopupWindow(this).apply {
            contentView = LayoutInflater.from(this@MainActivity).inflate(R.layout.test_dialog, null, false)
            val btn = contentView.findViewById<View>(R.id.tv_switch)
            btn?.setOnClickListener {
                SkinManager.applyThemeNight(!SkinManager.isNightMode())
            }
        }
        btnPopupWindow.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            } else {
                popupWindow.showAsDropDown(it)
            }
        }


        findViewById<View>(R.id.view).setOnClickListener {
            Logger.i("sss", "click")
            TestActivity.startActivity(this)
        }
    }

    private fun notifyRadioButton() {
        if (resources.isNight()) {
            nightMode.isChecked = true
        } else {
            dayMode.isChecked = true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 对接系统白天黑夜模式
        if (newConfig.isNight() != SkinManager.isNightMode()) {
            SkinManager.applyThemeNight(newConfig.isNight())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinManager.removeSkinChangeListener(this)
    }
}
