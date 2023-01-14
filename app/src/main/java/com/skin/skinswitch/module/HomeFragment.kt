package com.skin.skinswitch.module

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.skinswitch.R
import com.skin.skinswitch.module.base.BaseFragment

class HomeFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        return super.onGetLayoutInflater(savedInstanceState)
    }

    override fun onLayoutId(): Int = R.layout.fragment_home

    override fun initView(root: View) {
        val g= 0
    }
}
