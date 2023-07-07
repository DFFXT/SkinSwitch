package com.example.viewdebug.xml.struct.writer.helper

/**
 * 资源类型
 */
internal object ResourceType {

    // Contains no data.
    val TYPE_NULL: Byte = 0x00

    // The 'data' holds a ResTable_ref a reference to another resource
    // table entry.
    val TYPE_REFERENCE: Byte = 0x01

    // The 'data' holds an attribute resource identifier.
    val TYPE_ATTRIBUTE: Byte = 0x02

    // The 'data' holds an index into the containing resource table's
    // global value string pool.
    val TYPE_STRING: Byte = 0x03

    // The 'data' holds a single-precision floating point number.
    val TYPE_FLOAT: Byte = 0x04

    // The 'data' holds a complex number encoding a dimension value
    // such as "100in".
    val TYPE_DIMENSION: Byte = 0x05

    // The 'data' holds a complex number encoding a fraction of a
    // container.
    val TYPE_FRACTION: Byte = 0x06

    // Beginning of integer flavors...
    val TYPE_FIRST_INT: Byte = 0x10

    // The 'data' is a raw integer value of the form n..n.
    val TYPE_INT_DEC: Byte = 0x10

    // The 'data' is a raw integer value of the form 0xn..n.
    val TYPE_INT_HEX: Byte = 0x11

    // The 'data' is either 0 or 1 for input "false" or "true" respectively.
    val TYPE_INT_BOOLEAN: Byte = 0x12

    // Beginning of color integer flavors...
    val TYPE_FIRST_COLOR_INT: Byte = 0x1c

    // The 'data' is a raw integer value of the form #aarrggbb.
    val TYPE_INT_COLOR_ARGB8: Byte = 0x1c

    // The 'data' is a raw integer value of the form #rrggbb.
    val TYPE_INT_COLOR_RGB8: Byte = 0x1d

    // The 'data' is a raw integer value of the form #argb.
    val TYPE_INT_COLOR_ARGB4: Byte = 0x1e

    // The 'data' is a raw integer value of the form #rgb.
    val TYPE_INT_COLOR_RGB4: Byte = 0x1f

    // ...end of integer flavors.
    val TYPE_LAST_COLOR_INT: Byte = 0x1f

    // ...end of integer flavors.
    val TYPE_LAST_INT: Byte = 0x1f
}
