package com.skin.skinswitch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.skinswitch.R

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_type)
        val p1 = findViewById<View>(R.id.test_id_ConstraintLayout)
       /* val p2 = findViewById<View>(R.id.test_id_View)
        val p3 = findViewById<View>(R.id.test_id_View1)*/

        val f = 0
    }

    companion object {
        fun startActivity(ctx: Context) {
            ctx.startActivity(Intent(ctx, TestActivity::class.java))
        }
    }
}