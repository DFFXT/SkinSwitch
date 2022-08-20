package com.skin.skinswitch

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skincore.provider.TestResourceProvider
import java.util.*

val map = WeakHashMap<View, Int>()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SkinManager.init(
            this,
            object : DefaultProviderFactory() {
                override fun getPathProvider(theme: Int): ISkinPathProvider? {
                    if (theme == 1) return object : ISkinPathProvider {
                        override fun getSkinPath(): String {
                            return TestResourceProvider::class.simpleName!!
                        }
                    }
                    return null
                }
            }
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // map[findViewById<TextView>(R.id.tv_click)] = 0
        findViewById<TextView>(R.id.tv_click).setOnClickListener {
            SkinManager.switchTheme(1)
        }
        findViewById<View>(R.id.tv_click1).setOnClickListener {
            SecondActivity.startActivity(this)
        }
    }
}
