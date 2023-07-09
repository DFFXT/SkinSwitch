package com.example.viewdebug.xml.struct.writer.helper

object ExternalFunction {
    init {
        System.loadLibrary("ViewDebug")
    }
    external fun stringToFloat(string: String): Long
}