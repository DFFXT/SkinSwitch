package com.skin.skinswitch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skin.log.Logger

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.viewdebug.R.layout.view_debug_compile_test)
        //val p1 = findViewById<View>(R.id.test_id_ConstraintLayout)
       /* val p2 = findViewById<View>(R.id.test_id_View)
        val p3 = findViewById<View>(R.id.test_id_View1)*/

        Logger.d("sss", "newactivity")
        val f = 0
    }

    companion object {
        fun startActivity(ctx: Context) {
            ctx.startActivity(Intent(ctx, TestActivity::class.java))
        }
    }
}