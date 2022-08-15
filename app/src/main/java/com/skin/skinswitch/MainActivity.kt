package com.skin.skinswitch

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.DefaultResourceProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SkinManager.init(this, DefaultProviderFactory())
        val f = LayoutInflater.from(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.tv_click).setOnClickListener {
            val f = 0
        }

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun getSystemService(name: String): Any {
        return super.getSystemService(name)
    }
}
