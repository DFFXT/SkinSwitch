package com.example.viewdebug.ui.image

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import org.xmlpull.v1.XmlPullParser
import java.util.LinkedList

/**
 * 将xml转换成spannable
 */
class XmlParser {
    fun getXmlText(ctx: Context, id: Int): Spannable {
        ctx.resources.getXml(id).use {
            val builder = SpannableStringBuilder()

            val breakLine = LinkedList<Boolean>()
            while (true) {
                when (it.eventType) {
                    XmlPullParser.START_DOCUMENT -> {
                    }

                    XmlPullParser.START_TAG -> {
                        if (breakLine.size != 0) {
                            breakLine[breakLine.size - 1] = true
                        }
                        breakLine.add(false)
                        if (builder.isNotEmpty()) {
                            builder.append("\n")
                        }
                        repeat(it.depth - 1) {
                            builder.append("\t")
                        }
                        builder.append("<" + it.name + "")
                        for (i in 0 until it.attributeCount) {
                            val attrName = it.getAttributeName(i)
                            builder.append(" $attrName=")
                            val attrIdValue = it.getAttributeResourceValue(i, -1)
                            var attrValue = ""
                            if (attrIdValue != -1) {
                                attrValue = ctx.resources.getResourceTypeName(attrIdValue) + "/"
                                attrValue += ctx.resources.getResourceEntryName(attrIdValue)
                            } else {
                                attrValue = it.getAttributeValue(i)
                            }

                            builder.append("\"$attrValue\"")
                            builder.setSpan(
                                object : ClickableSpan() {
                                    override fun onClick(widget: View) {
                                        Log.i("XmlParser", "click $attrValue")
                                    }
                                },
                                builder.length - attrValue.length - 1,
                                builder.length,
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE,
                            )
                        }
                        builder.append(">")
                    }

                    XmlPullParser.END_TAG -> {
                        if (breakLine.removeLast()) {
                            builder.append("\n")
                            repeat(it.depth - 1) {
                                builder.append("\t")
                            }
                        }
                        builder.append("</" + it.name + ">")
                    }

                    XmlPullParser.TEXT -> {
                        breakLine[breakLine.size - 1] = true
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
    }
}
