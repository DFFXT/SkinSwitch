package com.example.viewdebug.ui

import android.annotation.SuppressLint
import android.app.Application
import com.example.viewdebug.apply.dex.DexLoadManager
import com.example.viewdebug.ui.page.ViewImageShowPage
import com.example.viewdebug.ui.page.info.ModifyListPage
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.launch
import com.fxf.debugwindowlibaray.ViewDebugManager
import com.fxf.debugwindowlibaray.ui.EmptyPage
import com.fxf.debugwindowlibaray.ui.UIPage
import kotlinx.coroutines.Dispatchers

/**
 * 调试窗口管理
 */
object WindowControlManager {
    private val viewDebugManager = ViewDebugManager()
    @SuppressLint("StaticFieldLeak")
    private lateinit var emptyPage: EmptyPage

    @SuppressLint("StaticFieldLeak")
    private var modifyListPage: ModifyListPage? = null
    fun init(ctx: Application) {
        emptyPage = EmptyPage()
        viewDebugManager.init(ctx, defaultPage = emptyPage)
        viewDebugManager.addPage(ViewImageShowPage())
        // 监听是否有资源更改

        if (ViewDebugResourceManager.getAllChangedResource().isNotEmpty() || ViewDebugResourceManager.getAllValueChangedItem().isNotEmpty()) {
            launch(Dispatchers.Main) {
                notifyModifyList()
            }
        }

        /*ViewDebugResourceManager.addResourceChangeListener(object : ViewDebugResourceManager.OnResourceChanged {
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
        })*/
        notifyModifyList()
    }

    /**
     * 刷新变更列表页面数据
     */
    fun refreshModifyListPage() {
        modifyListPage?.refresh()
    }

    /**
     * 触发更新列表刷新
     */
    fun notifyModifyList() {
        // 有资源更改则显示，没有资源更改则不显示页面
        if (ViewDebugResourceManager.getAllChangedResource().isEmpty() && DexLoadManager.getAllDexList().isEmpty() && ViewDebugResourceManager.getAllValueChangedItem().isEmpty()) {
            if (modifyListPage != null) {
                viewDebugManager.removePage(modifyListPage!!)
                modifyListPage = null
            }
        } else {
            if (modifyListPage == null) {
                modifyListPage = ModifyListPage()
            }
            viewDebugManager.addPage(modifyListPage!!)
        }
    }

    fun removePage(uiPage: UIPage) {
        viewDebugManager.removePage(uiPage)
    }

    /**
     * 页面切换到空白页
     */
    fun resetToEmptyPage() {
        viewDebugManager.switchPage(emptyPage)
    }
}