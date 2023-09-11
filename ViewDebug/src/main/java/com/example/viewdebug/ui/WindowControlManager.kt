package com.example.viewdebug.ui

import android.annotation.SuppressLint
import android.app.Application
import com.example.viewdebug.dex.DexLoadManager
import com.example.viewdebug.ui.page.ViewImageShowPage
import com.example.viewdebug.ui.page.info.ModifyListPage
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.launch
import com.fxf.debugwindowlibaray.ViewDebugManager
import com.fxf.debugwindowlibaray.ui.EmptyPage
import kotlinx.coroutines.Dispatchers

object WindowControlManager {
    private val viewDebugManager = ViewDebugManager()
    @SuppressLint("StaticFieldLeak")
    private lateinit var emptyPage: EmptyPage

    private var modifyListPage: ModifyListPage? = null
    fun init(ctx: Application) {
        emptyPage = EmptyPage()
        viewDebugManager.init(ctx, emptyPage)
        viewDebugManager.addPage(ViewImageShowPage())
        // 监听是否有资源更改

        ViewDebugResourceManager.addResourceChangeListener(object : ViewDebugResourceManager.OnResourceChanged {
            override fun onResourceAdd(id: Int) {
                notify1()
            }

            override fun onResourceRemove(id: Int) {
                notify1()
            }

            fun notify1() {
                launch(Dispatchers.Main) {
                    notifyModifyList()
                }
            }
        })
        notifyModifyList()
    }

    private fun notifyModifyList() {
        // 有资源更改则显示，没有资源更改则不显示页面
        if (ViewDebugResourceManager.getAllChangedResource().isEmpty() && DexLoadManager.getAppliedDexList().isEmpty()) {
            if (modifyListPage != null) {
                viewDebugManager.removePage(modifyListPage!!)
                modifyListPage = null
            }
        } else {
            if (modifyListPage == null) {
                modifyListPage = ModifyListPage()
                viewDebugManager.addPage(modifyListPage!!)
            }
        }
    }

    /**
     * 页面切换到空白页
     */
    fun resetToEmptyPage() {
        viewDebugManager.switchPage(emptyPage)
    }
}