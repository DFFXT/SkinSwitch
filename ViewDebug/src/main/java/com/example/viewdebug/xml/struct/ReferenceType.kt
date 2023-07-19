package com.example.viewdebug.xml.struct

annotation class ReferenceType {
    companion object {
        const val TYPE_ID = "id"
        const val TYPE_DRAWABLE = "drawable"
        const val TYPE_COLOR = "color"
        const val TYPE_DIMEN = "dimen"
        const val TYPE_STRING = "string"
        const val TYPE_INT = "integer"
        const val TYPE_FLOAT = "float"
        const val TYPE_FRACTION = "fraction"
    }
}

annotation class FormatType {
    companion object {
        const val TYPE_ID = "id"
        const val TYPE_DRAWABLE = "drawable"
        const val TYPE_COLOR = "color"
        const val TYPE_DIMEN = "dimen"
        const val TYPE_STRING = "string"
        const val TYPE_INT = "integer"
        const val TYPE_FLOAT = "float"
        const val TYPE_FRACTION = "fraction"
        const val TYPE_REFERENCE = "reference"
        const val TYPE_BOOLEAN = "boolean"
    }
}