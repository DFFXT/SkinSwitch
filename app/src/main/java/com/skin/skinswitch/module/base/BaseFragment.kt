package com.skin.skinswitch.module.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.skin.skincore.inflater.SkinLayoutInflater

abstract class BaseFragment : Fragment() {
    private lateinit var mRootView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this::mRootView.isInitialized) {
            return mRootView
        } else {
            mRootView = inflater.inflate(onLayoutId(), container, false)
            return mRootView
        }
    }

    /*override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        return LayoutInflater.from(requireContext()).apply {
            this as SkinLayoutInflater
            val factory2Method = FragmentManager::class.java.getDeclaredMethod("getLayoutInflaterFactory")
            factory2Method.isAccessible = true
            this.factory2 = factory2Method.invoke(childFragmentManager) as LayoutInflater.Factory2?
        }
    }*/

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        return super.onGetLayoutInflater(savedInstanceState)
    }

    abstract fun onLayoutId(): Int

    abstract fun initView(root: View)
}
