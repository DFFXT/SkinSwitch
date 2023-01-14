package com.skin.skinswitch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skinswitch.R
import com.skin.skincore.SkinManager

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /*SkinManager.init(
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
        )*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // map[findViewById<TextView>(R.id.tv_click)] = 1
        findViewById<TextView>(R.id.tv_click).apply {
            text = System.currentTimeMillis().toString()
            setOnClickListener {
                SkinManager.switchTheme(0)
            }
        }
    }

    companion object {
        @JvmStatic
        fun startActivity(ctx: Context) {
            ctx.startActivity(Intent(ctx, SecondActivity::class.java))
        }
    }
}
