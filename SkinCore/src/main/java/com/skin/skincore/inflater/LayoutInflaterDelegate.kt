package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import android.view.ViewGroup
import com.skin.log.Logger
import com.skin.skincore.reflex.factory2Filed
import com.skin.skincore.reflex.factoryFiled
import com.skin.skincore.reflex.privateFactoryFiled
import com.skin.skincore.tag.TAG_CREATE_VIEW

/**
 * 通过反射，添加自己的Factory（仅仅是一层包装）, 自己的factory内部会调用对应的真实factory
 * 拦截通过xml创建view对象
 */
class LayoutInflaterDelegate(
    original: LayoutInflater, newContext: Context,
    internal var iOnViewCreated: IOnViewCreated?
) :
    LayoutInflater(original, newContext), IOnViewCreated {
    private val TAG = "SkinLayoutInflater"

    companion object {

        /**
         * 将original的Factory复制到target，并用FactoryDelegate代理
         * @param original 原始对象
         * @param target 新对象，也可以是原始对象
         * @param onViewCreatedListener 监听创建
         */
        fun delegate(
            original: LayoutInflater,
            target: LayoutInflater,
            onViewCreatedListener: IOnViewCreated
        ) {
            // 反射field并设置新的值
            factoryFiled.set(target, FactoryDelegate(onViewCreatedListener, original.factory))
            factory2Filed.set(target, FactoryDelegate(onViewCreatedListener, original.factory2))
            privateFactoryFiled.set(
                target,
                PrivateFactoryDelegate(target, onViewCreatedListener, original.privateFactory())
            )
        }
    }
    /*    *//**
     * 优先级：
     * factory2->factory->privateFactory
     *//*
    private lateinit var factory: FactoryDelegate
    private lateinit var factory2: FactoryDelegate*/

    init {
        /**
         * 代理origin的Factory
         */
        delegate(original, this, this)
    }

    override fun onInflateFinish(root: View) {
        this.iOnViewCreated?.onInflateFinish(root)
    }

    override fun inflate(resource: Int, root: ViewGroup?, attachToRoot: Boolean): View {
        val rootView = super.inflate(resource, root, attachToRoot)
        onInflateFinish(rootView)
        return rootView
    }

    override fun setFactory(factory: Factory) {
        // 不调用supper
        // todo 这里也应该限制只调用一次
        // super.setFactory(factory)
        // this.factory.addFactory(factory)
        (getFactory() as FactoryDelegate).addFactory(factory)
    }

    override fun setFactory2(factory: Factory2) {
        // super.setFactory2(factory)
        // this.factory2.addFactory(factory2)
        (getFactory2() as FactoryDelegate).addFactory(factory)
    }

    override fun onCreateView(
        viewContext: Context,
        parent: View?,
        name: String,
        attrs: AttributeSet?
    ): View? {
        val v = super.onCreateView(viewContext, parent, name, attrs)
        Logger.v(TAG_CREATE_VIEW, "3 onCreateView $name")
        if (v != null && attrs != null) {
            this.iOnViewCreated?.onViewCreated(parent, v, name, attrs)
        }
        return v
    }

    override fun cloneInContext(newContext: Context): LayoutInflater {
        return LayoutInflaterDelegate(this, newContext, iOnViewCreated)
    }

    override fun onViewCreated(parent: View?, view: View, name: String, attributeSet: AttributeSet) {
        this.iOnViewCreated?.onViewCreated(parent, view, name, attributeSet)
    }
}

/**
 * 反射获取privateFactory
 */
fun LayoutInflater.privateFactory(): Factory2? {
    return privateFactoryFiled.get(this) as? Factory2
}
