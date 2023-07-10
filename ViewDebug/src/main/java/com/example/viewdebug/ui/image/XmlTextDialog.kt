package com.example.viewdebug.ui.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.viewdebug.databinding.ViewDebugXmlTextContainerBinding
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.skin.ViewDebugMergeResource
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.launch
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.ChunkFileWriter
import com.skin.skincore.asset.DefaultResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class XmlTextDialog(
    private val ctx: Context,
    private val hostPage: UIPage,
) {
    private val binding = ViewDebugXmlTextContainerBinding.inflate(
        LayoutInflater.from(ctx),
        hostPage.tabView.parent as ViewGroup,
        false,
    )
    private var mode = 0

    private var layoutId: Int = 0
    private lateinit var originText: CharSequence

    init {
        binding.tvText.movementMethod = LinkMovementMethod.getInstance()
        adjustOrientation(binding.container)
        binding.ivXmlTextOperate.setOnClickListener {
            Toast.makeText(it.context, "未实现该功能", Toast.LENGTH_LONG).show()
            if (mode == 0) {
                binding.tvText.requestFocus()
                binding.tvText.selectAll()
                onModeChange(1)
            } else if (mode == 1) {
                showLoading()
                launch(Dispatchers.IO) {
                    saveChange()
                    withContext(Dispatchers.Main) {
                        onModeChange(0)
                        binding.tvText.clearFocus()
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
    }

    private fun onModeChange(mode: Int) {
        this.mode = mode
        binding.ivClose.isVisible = mode == 1
        binding.ivXmlTextOperate.isSelected = mode == 1
    }

    private suspend fun saveChange() {
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
    }
    private fun showLoading() {

    }

    fun show(layoutId: Int, xml: CharSequence) {
        val attrValue = ctx.resources.getResourceEntryName(layoutId)
        val title = ctx.resources.getResourceTypeName(layoutId) + "/" + attrValue
        this.layoutId = layoutId
        this.originText = xml
        binding.tvName.text = title
        binding.tvText.setText(xml)
        binding.tvName.setOnClickListener {
            copyToClipboard(ctx, title)
        }
        hostPage.showDialog(binding.root)
    }

    private fun copyToClipboard(ctx: Context, text: String) {
        val clipboardManager = ctx.getSystemService(ClipboardManager::class.java)
        clipboardManager.setPrimaryClip(ClipData.newPlainText("UI调试", text))
    }

    fun close() {
        hostPage.closeDialog(binding.root)
    }
}
