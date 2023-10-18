package com.example.viewdebug.xml.struct.writer.helper

import android.content.res.TypedArray
import android.util.TypedValue

/**
 * 资源类型
 */
internal annotation class ResourceType {
    companion object {
        // Contains no data.
        val TYPE_NULL: Byte = TypedValue.TYPE_NULL.toByte()

        // The 'data' holds a ResTable_ref a reference to another resource
        // table entry.
        val TYPE_REFERENCE: Byte = TypedValue.TYPE_REFERENCE.toByte()

        // The 'data' holds an attribute resource identifier.
        val TYPE_ATTRIBUTE: Byte = TypedValue.TYPE_ATTRIBUTE.toByte()

        // The 'data' holds an index into the containing resource table's
        // global value string pool.
        val TYPE_STRING: Byte = TypedValue.TYPE_STRING.toByte()

        // The 'data' holds a single-precision floating point number.
        val TYPE_FLOAT: Byte = TypedValue.TYPE_FLOAT.toByte()

        // The 'data' holds a complex number encoding a dimension value
        // such as "100in".
        val TYPE_DIMENSION: Byte = TypedValue.TYPE_DIMENSION.toByte()

        // The 'data' holds a complex number encoding a fraction of a
        // container.
        val TYPE_FRACTION: Byte = TypedValue.TYPE_FRACTION.toByte()

        // Beginning of integer flavors...
        val TYPE_FIRST_INT: Byte = TypedValue.TYPE_FIRST_INT.toByte()

        // The 'data' is a raw integer value of the form n..n.
        val TYPE_INT_DEC: Byte = TypedValue.TYPE_INT_DEC.toByte()

        // The 'data' is a raw integer value of the form 0xn..n.
        val TYPE_INT_HEX: Byte = TypedValue.TYPE_INT_HEX.toByte()

        // The 'data' is either 0 or 1 for input "false" or "true" respectively.
        val TYPE_INT_BOOLEAN: Byte = TypedValue.TYPE_INT_BOOLEAN.toByte()

        // Beginning of color integer flavors...
        val TYPE_FIRST_COLOR_INT: Byte = TypedValue.TYPE_FIRST_COLOR_INT.toByte()

        // The 'data' is a raw integer value of the form #aarrggbb.
        val TYPE_INT_COLOR_ARGB8: Byte = TypedValue.TYPE_INT_COLOR_ARGB8.toByte()

        // The 'data' is a raw integer value of the form #rrggbb.
        val TYPE_INT_COLOR_RGB8: Byte = TypedValue.TYPE_INT_COLOR_RGB8.toByte()

        // The 'data' is a raw integer value of the form #argb.
        val TYPE_INT_COLOR_ARGB4: Byte = TypedValue.TYPE_INT_COLOR_ARGB4.toByte()

        // The 'data' is a raw integer value of the form #rgb.
        val TYPE_INT_COLOR_RGB4: Byte = TypedValue.TYPE_INT_COLOR_RGB4.toByte()

        // ...end of integer flavors.
        val TYPE_LAST_COLOR_INT: Byte = TypedValue.TYPE_LAST_COLOR_INT.toByte()

        // ...end of integer flavors.
        val TYPE_LAST_INT: Byte = TypedValue.TYPE_LAST_INT.toByte()
    }
}
