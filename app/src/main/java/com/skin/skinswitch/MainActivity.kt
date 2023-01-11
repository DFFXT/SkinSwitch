package com.skin.skinswitch

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.*
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
        //addAssetPathMethod.invoke(this.resources.assets, path)
        val d = getDrawable(R.drawable.icon_default_head)
        intent.extras?.keySet()?.forEach {
            Log.i("--->", "$it:${intent.extras!![it]}")
        }
        val e = getDrawable(R.drawable.icon_default_head)
      /*  val cls = Class.forName("android.content.res.ApkAssets")
        val mtd: Method = cls.getDeclaredMethod("loadFromPath", String::class.java)
        mtd.isAccessible = true
        val apkAsset = mtd.invoke(null, path)
        setApkAssetMethod.invoke(resources.assets, apkAsset, true)
        val e = getDrawable(R.drawable.icon_default_head)*/
        /*SkinManager.init(
            this,
            object : DefaultProviderFactory() {
                // private val nightProvider = NightProvider(application)

                override fun getPathProvider(theme: Int): ISkinPathProvider? {
                    *//*if (theme == AppConst.THEME_NIGHT) {
                        return DefaultSkinPathProvider(Environment.getExternalStorageDirectory().absolutePath + "/skinPack-cartoon-debug - 副本.rar")
                    }*//*
                    return null
                }

                override fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider {
                    val mode = if (theme == AppConst.THEME_NIGHT) {
                        Configuration.UI_MODE_NIGHT_YES
                    } else {
                        Configuration.UI_MODE_NIGHT_NO
                    }
                    val configuration = resources.configuration
                    configuration.uiMode = mode
                    resources.updateConfiguration(
                        configuration,
                        resources.displayMetrics
                    )
                    val color = getColor(R.color.main_background)
                    val night = color == 0xff000000.toInt()
                    val day = color == 0xffffffff.toInt()
                    return super.getResourceProvider(ctx, theme)
                }
            }
        )*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // map[findViewById<TextView>(R.id.tv_click)] = 0

        findViewById<TextView>(R.id.tv_click).setOnClickListener {
            SkinManager.switchTheme(AppConst.THEME_NIGHT)
        }
        findViewById<View>(R.id.tv_click1).setOnClickListener {
            // SecondActivity.startActivity(this)
            SkinManager.switchTheme(AppConst.THEME_DEFAULT)
        }
        findViewById<View>(R.id.tv_click2).setOnClickListener {
           DialogFragment(R.layout.activity_main).show(supportFragmentManager, "1")
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.container, MainFragment())
            .commit()
    }
}
