package com.example.viewdebug.ui.page

import android.content.Context
import android.content.res.XmlResourceParser
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import com.example.viewdebug.R
import org.xmlpull.v1.XmlPullParser
import java.util.LinkedList

/**
 * 将xml转换成spannable
 */
class XmlParser {
    fun getXmlText(ctx: Context, id: Int, click: (String) -> Unit): Spannable {
        ctx.resources.getXml(id).use {
            return getXml(ctx, it, click)
        }
    }

    fun getXml(ctx: Context, xmlResourceParser: XmlResourceParser, click: (String) -> Unit): Spannable {
        val it = xmlResourceParser
        val builder = SpannableStringBuilder()

        val breakLine = LinkedList<Pair<Boolean, Int>>()
        while (true) {
            when (it.eventType) {
                XmlPullParser.START_DOCUMENT -> {
                }

                XmlPullParser.START_TAG -> {
                    if (breakLine.size != 0) {
                        breakLine[breakLine.size - 1] = Pair(true, it.attributeCount)
                    }
                    breakLine.add(Pair(false, it.attributeCount))
                    builder.indent(it.depth - 1)
                    builder.append("<" + it.name + "")
                    for (i in 0 until it.attributeCount) {
                        builder.indent(it.depth)
                        val attrName = it.getAttributeName(i)
                        builder.append("$attrName=")
                        val attrIdValue = it.getAttributeResourceValue(i, -1)
                        var attrValue = ""
                        // 存在@null的使用，导致attrIdValue=0
                        if (attrIdValue > 0) {
                            attrValue = ctx.resources.getResourceTypeName(attrIdValue) + "/"
                            attrValue += ctx.resources.getResourceEntryName(attrIdValue)
                        } else {
                            attrValue = it.getAttributeValue(i)
                        }

                        builder.append("\"$attrValue\"")
                        val start = builder.length - attrValue.length - 1
                        val end = builder.length - 1
                        builder.setSpan(
                            object : ClickableSpan() {
                                override fun updateDrawState(ds: TextPaint) {
                                    ds.color = ctx.getColor(R.color.view_debug_image_detail_title_bar_bg)
                                }
                                override fun onClick(widget: View) {
                                    Log.i("XmlParser", "click $attrValue")
                                    click.invoke(attrValue)
                                }
                            },
                            start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE,
                        )
                    }
                    builder.append(">")
                }

                XmlPullParser.END_TAG -> {
                    val last = breakLine.removeLast()
                    if (last.first || last.second > 0) {
                        builder.indent(it.depth - 1)
                    }
                    builder.append("</" + it.name + ">")
                }

                XmlPullParser.TEXT -> {
                    breakLine[breakLine.size - 1] = Pair(true, 0)
                    repeat(it.depth - 1) {
                        builder.append("\t")
                    }
                    builder.append("\n" + it.text + "\n")
                }

                XmlPullParser.END_DOCUMENT -> {
                    break
                }

                else -> {
                }
            }
            it.next()
        }
        Log.i("XmlParser", builder.toString())
        return builder
    }

    fun getXml(ctx: Context, xmlResourceParser: XmlResourceParser):Spannable {
        return getXml(ctx, xmlResourceParser) {}
    }

    private fun SpannableStringBuilder.indent(tabSize: Int) {
        if (this.lastOrNull() != '\n') {
            append("\n")
        }
        repeat(tabSize) {
            append("\t")
        }
    }
}
