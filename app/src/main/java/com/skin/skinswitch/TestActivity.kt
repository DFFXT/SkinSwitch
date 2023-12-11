package com.skin.skinswitch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.skinswitch.R
import com.example.skinswitch.databinding.LayoutTestItemBinding
import com.example.viewdebug.rv.ItemHandle
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.skin.log.Logger

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_type)
        val rv = findViewById<RecyclerView>(R.id.rv_list)
        val adapter = MultiTypeRecyclerAdapter<String>()
        adapter.registerItemHandler(object : ItemHandle<String>() {
            override fun handle(item: String): Boolean {
                return true
            }

            override fun onBindView(item: String, position: Int, vh: RecyclerView.ViewHolder) {

            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val v = LayoutTestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return object : RecyclerView.ViewHolder(v.root){}
            }

        })
        adapter.update(listOf("", "", "", ""))
        rv.adapter = adapter
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