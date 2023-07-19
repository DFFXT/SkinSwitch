package com.example.viewdebug.ui.image.attribute

import android.widget.TextView

internal class TextUpdate : Update<TextView> {
    override fun update(view: TextView, vararg args: String) {
        view.text = args[0]
    }

    override fun getValue(view: TextView): String {
        return view.text.toString()
    }

}