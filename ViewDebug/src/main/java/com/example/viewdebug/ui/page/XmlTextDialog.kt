package com.example.viewdebug.ui.page

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.viewdebug.apply.xml.XmlLoadManager
import com.example.viewdebug.databinding.ViewDebugXmlTextContainerBinding
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.copyToClipboard
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.fxf.debugwindowlibaray.ui.UIPage
import com.skin.log.Logger
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.asset.DefaultAssetLoader
import com.skin.skincore.reflex.getSkinTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

/**
 * xml编辑编译弹窗
 */
internal class XmlTextDialog(
    private val ctx: Context,
    private val hostPage: UIPage,
) : BaseDialog(hostPage) {
    private lateinit var binding: ViewDebugXmlTextContainerBinding

    private var loadingDialog: ViewDebugLoadingDialog? = null

    private val watcher by lazy {
        object : RemoteFileReceiver.FileWatcher(resourceType, consume = true) {
            override fun onReceive(fileContainer: FileContainer) {
                val path = fileContainer.fileInfo.firstOrNull()?.path ?: return
                val file = File(path)
                if (file.exists()) {
                    val content = String(file.readBytes())
                    launch(Dispatchers.Main) {
                        binding.tvText.setText(content)
                    }
                }
            }
        }
    }
    private var mode = 0

    private var resourceId: Int = 0
    private lateinit var originText: CharSequence
    private var target: WeakReference<View>? = null
    private var attributeId: Int? = null

    // 资源类型
    private val resourceType by lazy {
        host.ctx.resources.getResourceTypeName(resourceId)
    }

    override fun onCreateDialog(ctx: Context, parent: ViewGroup): View {
        binding = ViewDebugXmlTextContainerBinding.inflate(
            LayoutInflater.from(ctx),
            parent,
            false,
        )
        binding.tvText.movementMethod = LinkMovementMethod.getInstance()
        adjustOrientation(binding.container)
        binding.ivXmlTextOperate.setOnClickListener {
            if (mode == 0) {
                binding.tvText.requestFocus()
                binding.tvText.selectAll()
                onModeChange(1)
            } else if (mode == 1) {
                showLoading()
                launch(Dispatchers.IO) {
                    val compileResult = XmlLoadManager.compileXml(
                        ctx,
                        binding.tvText.text.toString().byteInputStream(),
                        resourceId,
                        resourceType
                    ) && XmlLoadManager.loadApk()

                    withContext(Dispatchers.Main) {
                        closeLoading()
                        if (!compileResult) {
                            "编译失败".shortToast()
                        } else {
                            onModeChange(0)
                            binding.tvText.clearFocus()
                            if (resourceType == "drawable" || resourceType == "color") {
                                // 触发属性更新
                                target?.get()?.let {
                                    AttrApplyManager.apply(
                                        attributeId!!,
                                        intArrayOf(BaseViewApply.EVENT_TYPE_THEME),
                                        it,
                                        resourceId,
                                        resourceType,
                                        SkinManager.getResourceProvider(host.ctx),
                                        it.context.getSkinTheme()
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
        binding.ivClose.setOnClickListener {
            onModeChange(0)
            if (binding.tvText.text.toString() != originText) {
                binding.tvText.setText(originText)
            }
        }
        return binding.root
    }

    private fun onModeChange(mode: Int) {
        this.mode = mode
        binding.ivClose.isVisible = mode == 1
        binding.ivXmlTextOperate.isSelected = mode == 1
    }

    private suspend fun saveChange(): Boolean {
        try {
            val compiler = XmlCompiler(ctx)
            val buffer = compiler.compile(binding.tvText.text.toString().byteInputStream())
            val byteArray = ByteArray(buffer.limit())
            buffer.get(byteArray, 0, buffer.limit())
            // 打包
            val pack = PackAssetsFile(ctx)
            pack.addAXMLFile(byteArray.inputStream(), resourceId.toString())
            pack.pack()
            // 读入
            val assetManager =
                DefaultAssetLoader().createAssetManager(pack.getPackedApkPath(), ctx)
            if (assetManager != null) {
                ViewDebugResourceManager.interceptedAsset = assetManager.second
                ViewDebugResourceManager.addInterceptor(resourceType, resourceId)
            }
            return true
        } catch (e: Exception) {
            Logger.e("XmlCompiler", "compile error")
            e.printStackTrace()
            return false
        }
    }

    private fun showLoading() {
        loadingDialog = loadingDialog ?: ViewDebugLoadingDialog(host)
        loadingDialog?.show()
    }

    private fun closeLoading() {
        loadingDialog?.close()
    }

    fun show(layoutId: Int, xml: CharSequence, attributeId: Int?, target: WeakReference<View>?) {
        show()
        this.target = target
        this.attributeId = attributeId
        val attrValue = ctx.resources.getResourceEntryName(layoutId)
        val title = ctx.resources.getResourceTypeName(layoutId) + "/" + attrValue
        this.resourceId = layoutId
        this.originText = xml
        binding.tvName.text = title
        binding.tvText.setText(xml)
        binding.tvName.setOnClickListener {
            copyToClipboard(ctx, title)
        }
        RemoteFileReceiver.observe(watcher)
    }

    override fun onClose() {
        super.onClose()
        RemoteFileReceiver.remove(watcher)
    }

}
