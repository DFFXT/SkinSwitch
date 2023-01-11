package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.tag.TAG_CREATE_VIEW

/**
 * 通过反射，添加自己的Factory（仅仅是一层包装）, 自己的factory内部会调用对应的真实factory
 * 拦截通过xml创建view对象
 */
class SkinLayoutInflater(original: LayoutInflater?, newContext: Context?) :
    LayoutInflater(original, newContext), IOnViewCreated {
    private val TAG = "SkinLayoutInflater"

    private val factoryFiled = LayoutInflater::class.java.getDeclaredField("mFactory").apply {
        isAccessible = true
    }
    private val factory2Filed = LayoutInflater::class.java.getDeclaredField("mFactory2").apply {
        isAccessible = true
    }
    private val privateFactoryFiled =
        LayoutInflater::class.java.getDeclaredField("mPrivateFactory").apply { isAccessible = true }
    private val createViewMethod = LayoutInflater::class.java.getDeclaredMethod(
        "createViewFromTag",
        View::class.java,
        String::class.java,
        Context::class.java,
        AttributeSet::class.java,
        Boolean::class.java
    )

    /**
     * 当view被创建时回调
     */
    var onViewCreatedListener: IOnViewCreated? = null

    /**
     * 优先级：
     * factory2->factory->privateFactory
     */
    private lateinit var factory: SkinInflaterFactory
    private lateinit var factory2: SkinInflaterFactory2

    // 内部默认inflater
    private lateinit var privateFactory: PrivateInflaterFactory

    init {
        /**
         * 这一步是将真实的inflater复制到SkinLayoutInflater，使其等价
         */
        if (getFactory() != null) {
            addFactory(getFactory())
        }
        if (getFactory2() != null) {
            addFactory2(getFactory2())
        }
        if (privateFactoryFiled.get(this) != null) {
            addPrivateFactory2(privateFactoryFiled.get(this) as Factory2)
        }
    }

    private fun addFactory(factory: Factory) {
        if (!this::factory.isInitialized) {
            this.factory = SkinInflaterFactory(this, factory)
            factoryFiled.set(this, this.factory)
        } else {
            this.factory.addFactory(factory)
        }
    }

    private fun addFactory2(factory2: Factory2) {
        if (!this::factory2.isInitialized) {
            this.factory2 = SkinInflaterFactory2(this, factory2)
            factory2Filed.set(this, this.factory2)
        } else {
            this.factory2.addFactory(factory2)
        }
    }

    private fun addPrivateFactory2(factory2: Factory2) {
        if (!this::privateFactory.isInitialized) {
            this.privateFactory = PrivateInflaterFactory(this, factory2)
            privateFactoryFiled.set(this, this.privateFactory)
        } else {
            this.privateFactory.addFactory(factory2)
        }
    }

    override fun setFactory(factory: Factory) {
        // 不调用supper
        // todo 这里也应该限制只调用一次
        // super.setFactory(factory)
        addFactory(factory)
    }

    override fun setFactory2(factory: Factory2) {
        // super.setFactory2(factory)
        addFactory2(factory)
    }

    override fun onCreateView(name: String, attrs: AttributeSet): View? {
        val v = super.onCreateView(name, attrs)
        if (v != null) {
            onViewCreated(v, name, attrs)
        }
        Logger.i(TAG_CREATE_VIEW, "1 onCreateView $name $v")
        return v
    }

    override fun onCreateView(parent: View?, name: String, attrs: AttributeSet): View? {
        Logger.i(TAG_CREATE_VIEW, "2 onCreateView $name")
        return super.onCreateView(parent, name, attrs).apply {
            if (this != null) {
                onViewCreated(this, name, attrs)
            }
        }
    }

    override fun onCreateView(
        viewContext: Context,
        parent: View?,
        name: String,
        attrs: AttributeSet?
    ): View? {
        val v = super.onCreateView(viewContext, parent, name, attrs)
        Logger.i(TAG_CREATE_VIEW, "3 onCreateView $name")
        if (v != null && attrs != null) {
            onViewCreatedListener?.onViewCreated(v, name, attrs)
        }
        return v
    }

    override fun cloneInContext(newContext: Context?): LayoutInflater {
        return SkinLayoutInflater(this, newContext)
    }

    override fun onViewCreated(view: View, name: String, attributeSet: AttributeSet) {
        onViewCreatedListener?.onViewCreated(view, name, attributeSet)
    }
}
