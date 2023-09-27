// 包名不能变
package com.example.viewdebug.apply.dex

import kotlin.String

/**
 * 疑惑点：
 * 通过this::class.java.classLoader.findLoadedClass 均返回了class对象
 * 但是通过反射，修改parent后，this::class.java.classLoader.findLoadedClass返回了想要的值
 */
object ClassLoadObserve : ClassLoader(){
    private val map = HashSet<String>()
    private val method by lazy {
        val m = ClassLoader::class.java.getDeclaredMethod("findLoadedClass", String::class.java)
        m.isAccessible = true
        m
    }
    init {
        map.add("")
        val f = ClassLoader::class.java.getDeclaredField("parent")
        f.isAccessible = true
        val parent = f.get(this::class.java.classLoader)
        f.set(this, parent)
        f.set(this::class.java.classLoader, this)
    }

    override fun findClass(name: String): Class<*> {
        // map.add(name)
        return super.findClass(name)
    }


    /**
     * 判断类是否加载
     */
    fun isLoaded(name: String): Boolean {
        // method.invoke(this::class.java.classLoader, name) != null
        return  method.invoke(this::class.java.classLoader, name) != null
    }


}