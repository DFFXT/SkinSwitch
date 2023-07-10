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
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.ChunkFileWriter
import com.skin.skincore.asset.DefaultResourceLoader
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
                onModeChange(0)
                binding.tvText.clearFocus()
                saveChange()
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

    private fun saveChange() {
        val compiler = XmlCompiler(ctx)
        val buffer = compiler.compile(binding.tvText.text.toString().byteInputStream())
        val byteArray = ByteArray(buffer.limit())
        buffer.get(byteArray, 0, buffer.limit())
        // 打包
        val pack = PackAssetsFile()
        pack.addLayoutFile(byteArray.inputStream(), layoutId.toString())
        pack.pack()
        // 读入
        val assetManager = DefaultResourceLoader().createAssetManager(pack.getPackedApkPath(), ctx)
        if (assetManager != null) {
            ViewDebugMergeResource.interceptedAsset = assetManager.second
            ViewDebugMergeResource.layoutInterceptorMapper.add(layoutId)
        }
    }

    fun show(layoutId: Int, xml: CharSequence) {
        val layoutId = ctx.resources.getIdentifier("test_type", "layout", ctx.packageName)
        val attrValue = ctx.resources.getResourceEntryName(layoutId)
        val title = ctx.resources.getResourceTypeName(layoutId) + "/" + attrValue
        this.layoutId = layoutId
        this.originText = xml
        binding.tvName.text = title
        val t = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<androidx.constraintlayout.widget.ConstraintLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "    xmlns:tools=\"http://schemas.android.com/tools\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    tools:context=\"com.skin.skinswitch.MainActivity\">\n" +
                "\n" +
                "    <androidx.fragment.app.FragmentContainerView\n" +
                "        android:id=\"@+id/container\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"match_parent\"\n" +
                "        tools:name=\"com.skin.skinswitch.module.HomeFragment\" />\n" +
                "\n" +
                "    <TextView\n" +
                "        android:id=\"@+id/view\"\n" +
                "        android:layout_width=\"@dimen/test_d\"\n" +
                "        android:layout_height=\"40dp\"\n" +
                "        android:text=\"1\"\n" +
                "        android:background=\"@drawable/theme_drawable\"\n" +
                "        app:layout_constraintStart_toStartOf=\"parent\"\n" +
                "        app:layout_constraintTop_toTopOf=\"parent\" />\n" +
                "\n" +
                "    <RadioGroup\n" +
                "        android:id=\"@+id/group_skin\"\n" +
                "        android:layout_width=\"wrap_content\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:layout_marginTop=\"200dp\"\n" +
                "        app:layout_constraintTop_toTopOf=\"parent\"\n" +
                "        app:layout_constraintStart_toStartOf=\"parent\"\n" +
                "        app:layout_constraintEnd_toStartOf=\"@id/group_mode\">\n" +
                "        <RadioButton\n" +
                "            android:id=\"@+id/radio_defaultSkin\"\n" +
                "            android:layout_width=\"wrap_content\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            android:textColor=\"@color/test_text_color\"\n" +
                "            android:text=\"默认资源\"/>\n" +
                "        <RadioButton\n" +
                "            android:id=\"@+id/radio_customSkin\"\n" +
                "            android:layout_width=\"wrap_content\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            android:textColor=\"@color/test_text_color\"\n" +
                "            android:text=\"皮肤包资源\"/>\n" +
                "    </RadioGroup>\n" +
                "    <RadioGroup\n" +
                "        android:id=\"@+id/group_mode\"\n" +
                "        android:layout_width=\"wrap_content\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        app:layout_constraintEnd_toEndOf=\"parent\"\n" +
                "        app:layout_constraintStart_toEndOf=\"@id/group_skin\"\n" +
                "        app:layout_constraintTop_toTopOf=\"@id/group_skin\"\n" +
                "        >\n" +
                "        <RadioButton\n" +
                "            android:id=\"@+id/radio_dayMode\"\n" +
                "            android:layout_width=\"wrap_content\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            android:textColor=\"@color/test_text_color\"\n" +
                "            android:text=\"白天模式\"/>\n" +
                "        <RadioButton\n" +
                "            android:id=\"@+id/radio_nightMode\"\n" +
                "            android:layout_width=\"wrap_content\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            android:textColor=\"@color/test_text_color\"\n" +
                "            android:text=\"夜间模式\"/>\n" +
                "    </RadioGroup>\n" +
                "\n" +
                "\n" +
                "</androidx.constraintlayout.widget.ConstraintLayout>"
        binding.tvText.setText(t)
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
