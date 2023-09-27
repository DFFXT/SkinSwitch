package com.example.viewdebug.ui.page.info

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.apply.ModifyState

open class ModifyItem(val name: String, val id: Int, val type: String, val state: ModifyState) {

}

class ModifyItemParent(var isExpand: Boolean, name: String, id: Int, type: String, state: ModifyState) :
    ModifyItem(name, id, type, state) {
    val children: ArrayList<ModifyItemChild> = ArrayList<ModifyItemChild>()
}

class ModifyItemChild(val parent: ModifyItemParent, name: String, id: Int, type: String, state: ModifyState) :
    ModifyItem(name, id, type, state)


fun ModifyItem.getStateText(): String {
    return when (state) {
        ModifyState.APPLIED -> {
            ViewDebugInitializer.ctx.getString(R.string.view_debug_dex_applied)
        }

        ModifyState.REBOOT_UPDATABLE -> {
            ViewDebugInitializer.ctx.getString(R.string.view_debug_dex_reboot_update)
        }

        ModifyState.INVALID_APPLY -> {
            ViewDebugInitializer.ctx.getString(R.string.view_debug_dex_invalid_apply)
        }

        ModifyState.UNKNOWN -> {
            ViewDebugInitializer.ctx.getString(R.string.view_debug_dex_unknown)
        }
    }
}