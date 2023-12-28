# SkinSwitch
Android 视图调试
使用方式
<pre><code>
  // 视情况添加，如果有guava依赖冲突则添加
  implementation 'com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava'
  // 换肤框架
  implementation 'com.github.DFFXT.SkinSwitch:SkinCore:0.21.5'
  // 视情况添加，视图调试框架，支持xml修改实时生效，和kotlin但文件修冷启动生效，用于节省编译时间，具体使用方式需搭配android studio插件使用[插件下载](https://github.com/DFFXT/ViewDebug-Trans)
  debugImplementation 'com.github.DFFXT.SkinSwitch:ViewDebug:0.21.5'
</code></pre>


<pre><code>
  // 设置日志接口实现
  Logger.setLoggerImpl(....)
  // 初始化视图拦截功能
  com.skin.skincore.SkinManager.init(context.application!!,0,object : DefaultProviderFactory() {
      // 设置皮肤包目录
      override fun getSkinFolder(): String = ""
      // 设置目录下皮肤包名称
      override fun getSkinName(theme: Int): String = "never load external skin"
  },
  // 设置黑夜模式
  true,
  false,
  )
</code></pre>




视图调试会自动通过start-up插件初始化。如果想关闭自动初始化可以移除对应的provider

取消自动初始化：
在AndroidManifest.xml中加入以下代码，代码功能为：禁止com.example.viewdebug.ViewDebugInitializer自动初始化，用ViewDebugStarter（需要自己实现）替代
（调试工具一般不打入release版本，所以需要创建的是src/debug/AndroidManifest.xml文件，只在debug版本编译代码）
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="com.example.viewdebug.ViewDebugInitializer"
        android:value="androidx.startup"
        tools:node="remove"/>
    <meta-data
        android:name="com.skin.skinswitch.ViewDebugStarter"
        android:value="androidx.startup"
        tools:node="merge"/>
</provider>
```




