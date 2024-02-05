package com.skin.skincore.asset

import android.content.Context
import android.util.DisplayMetrics
import com.skin.skincore.provider.ISkinPathProvider
import java.util.WeakHashMap

/**
 * 资源加载管理，提供缓存
 * 如果想重写资源加载，可以设置[IAssetFactory]，根据context，和ISkinPathProvider加载不同的皮肤资源
 */
object AssetLoaderManager {
    private var assetFactory: IAssetFactory = DefaultAssetFactory()

    // private val map = HashMap<AssetKey?, IAsset?>()
    // 通过WeakHashMap来自动释放IAsset对象，需要主动调用SkinManager.releaseTheme方法
    private val map = WeakHashMap<IAsset, Unit>()

    fun getAsset(context: Context, provider: ISkinPathProvider): IAsset? {
        val key = assetFactory.getAssetKey(context, provider)
        var asset = map.keys.find { it.assetKey == key }
        if (asset == null) {
            asset = createResource(context, provider)
            asset.assetKey = key
            map[asset] = Unit
        }
        return asset
    }

    private fun createResource(context: Context, provider: ISkinPathProvider): IAsset {
        return assetFactory.createAsset(context, provider)
    }

    fun getAll(): WeakHashMap<IAsset, Unit> = map

    /**
     * 设置资源加载器
     */
    fun setAssetFactory(factory: IAssetFactory) {
        this.assetFactory = factory
    }

    /**
     * 资源的key
     * 路径不能完全作为资源的唯一性
     * 如果存在辅助屏幕，而且辅助屏幕的分辨率和主屏不一致，那么需要构造对应分辨率下的Resource对象，否则会导致Presentation自动销毁
     * 错误信息：Presentation is being dismissed because the display metrics have changed since it was created.
     * @param context 包名区分
     * @param path 皮肤资源包区分
     * @param displayMetrics 分辨率区分
     * @param customTag 自定义区分
     */
    class AssetKey(context: Context, path: String, displayMetrics: DisplayMetrics, val customTag: String = "") {
        private val code = "@${context.packageName}@$path@${displayMetrics.hashCode()}@$customTag"
        override fun equals(other: Any?): Boolean {
            if (other is AssetKey) {
                return code == other.code
            }
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return code.hashCode()
        }
    }
}
