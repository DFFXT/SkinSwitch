package com.skin.skinswitch

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skinswitch.const.AppConst
import com.skin.skinswitch.module.MainFragment
import com.skin.skinswitch.skin.NightProvider
import java.util.*

val map = WeakHashMap<View, Int>()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SkinManager.init(
            this,
            object : DefaultProviderFactory() {
                // private val nightProvider = NightProvider(application)
                override fun getPathProvider(theme: Int): ISkinPathProvider? {
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
        )
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
        supportFragmentManager.beginTransaction()
            .add(R.id.container, MainFragment())
            .commit()
    }
}
