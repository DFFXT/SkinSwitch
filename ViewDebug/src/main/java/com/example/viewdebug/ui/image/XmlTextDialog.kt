package com.example.viewdebug.ui.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.viewdebug.databinding.ViewDebugXmlTextContainerBinding
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.skin.ViewDebugMergeResource
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.skin.log.Logger
import com.skin.skincore.asset.DefaultResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class XmlTextDialog(
    private val ctx: Context,
    private val hostPage: UIPage,
) : BaseDialog(hostPage) {
    private val binding = ViewDebugXmlTextContainerBinding.inflate(
        LayoutInflater.from(ctx),
        hostPage.tabView.parent as ViewGroup,
        false,
    )

    private var loadingDialog: ViewDebugLoadingDialog? = null
    private var mode = 0

    private var layoutId: Int = 0
    private lateinit var originText: CharSequence

    override fun onCreateDialog(ctx: Context): View {
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
                    val compileResult = saveChange()
                    withContext(Dispatchers.Main) {
                        closeLoading()
                        if (!compileResult) {
                            "编译失败".shortToast()
                        } else {
                            onModeChange(0)
                            binding.tvText.clearFocus()
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
            pack.addLayoutFile(byteArray.inputStream(), layoutId.toString())
            pack.pack()
            // 读入
            val assetManager = DefaultResourceLoader().createAssetManager(pack.getPackedApkPath(), ctx)
            if (assetManager != null) {
                ViewDebugMergeResource.interceptedAsset = assetManager.second
                ViewDebugMergeResource.layoutInterceptorMapper.add(layoutId)
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

    fun show(layoutId: Int, xml: CharSequence) {
        show()
        val attrValue = ctx.resources.getResourceEntryName(layoutId)
        val title = ctx.resources.getResourceTypeName(layoutId) + "/" + attrValue
        this.layoutId = layoutId
        this.originText = xml
        binding.tvName.text = title
        binding.tvText.setText(xml)
        binding.tvName.setOnClickListener {
            copyToClipboard(ctx, title)
        }

    }

    private fun copyToClipboard(ctx: Context, text: String) {
        val clipboardManager = ctx.getSystemService(ClipboardManager::class.java)
        clipboardManager.setPrimaryClip(ClipData.newPlainText("UI调试", text))
    }
}
