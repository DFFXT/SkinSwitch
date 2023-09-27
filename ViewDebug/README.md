# SkinSwitch
Android 视图调试
使用方式
<pre><code>
  // 视情况添加，如果有guava依赖冲突则添加
  implementation 'com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava'
  // 换肤框架
  implementation 'com.github.DFFXT.SkinSwitch:SkinCore:0.20.1'
  // 视情况添加，视图调试框架，支持xml修改实时生效，和kotlin但文件修冷启动生效，用于节省编译时间，具体使用方式需搭配android studio插件使用[插件下载](https://github.com/DFFXT/ViewDebug-Trans)
  debugImplementation 'com.github.DFFXT.SkinSwitch:ViewDebug:0.20.1'
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




视图调试会自动通过start-up插件初始化。自动初始化有个缺点，就是无法持久应用热更新（第二次重启后热更新失效），也无法应用多个热更新
如果想使热更新持久应用和支持多个文件的热更新，需按照以下的方式使用

更方便的使用：
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
ViewDebugStarter实现，IBuildIdentification接口返回构建时间，则支持在同一个buildId中，远程推送的dex文件可重复加载，
```kotlin
@Keep
class ViewDebugStarter : ViewDebugInitializer() {
    override fun getBuildIdentification(): IBuildIdentification? {
        return object : IBuildIdentification {
            override fun getBuildId(): String {
                // 返回构建时间，buildTime字段可在gradle中添加：buildConfigField("long", "buildTime", "${System.currentTimeMillis()}")
                return BuildConfig.buildTime.toString()
            }
        }
    }
}
```



