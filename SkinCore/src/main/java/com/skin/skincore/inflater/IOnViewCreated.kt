package com.skin.skincore.inflater

import android.util.AttributeSet
import android.view.View

interface IOnViewCreated {
    fun onViewCreated(parent: View?, view: View, name: String, attributeSet: AttributeSet)

    /**
     * xml Inflate结束后调用
     * 注意 注意 注意！！！
     * 如果开发的应用没有系统权限（API29+），则Fragment中的inflate无法在结束时回调[onInflateFinish]
     * 解决方法1：Activity（FragmentActivity的子类）中重写onGetLayoutInflater函数，返回LayoutInflater.from(Context)即可
     *
     */
    fun onInflateFinish(root: View)
}