package com.skin.skinswitch.module

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.CustomSkinPathProvider
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skinswitch.const.AppConst
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
