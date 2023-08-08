# SkinSwitch
Android 换肤框架
使用方式
<pre><code>
  // 视情况添加，如果有guava依赖冲突则添加
  implementation 'com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava'
  // 换肤框架
  implementation 'com.github.DFFXT.SkinSwitch:SkinCore:0.18.1.8'
  // 视情况添加，视图调试框架，支持xml修改实时生效，和kotlin但文件修冷启动生效，用于节省编译时间，具体使用方式需搭配android studio插件使用[插件下载](https://github.com/DFFXT/ViewDebug-Trans)
  debugImplementation 'com.github.DFFXT.SkinSwitch:ViewDebug:0.18.1.8'
</code></pre>


<pre><code>
  // 设置日志接口实现
  Logger.setLoggerImpl(....)
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
切换白天黑夜
<pre><code>
  // 设置黑夜模式
  SkinManager.applyThemeNight(true)
  // 切换皮肤主题，不同主题会加载不同的皮肤包，加载失败则使用默认皮肤包
  SkinManager.switchTheme(....)
  // 监听切换
  SkinManager.addSkinChangeListener(...)
  // 新增支持的换肤属性
  SkinManger.addAttributeCollection(....)

  // 当某个View换肤时，会触发此回调，根据返回状态确定十分需要换肤
  AttrApplyManager.onApplyInterceptor = {....}
</code></pre>

其它
如果需要某个视图下的View都不换肤，或者指定某个模块不换肤或者换肤，可用以下属性
<pre><code>
  // 指定某个view不换肤，但各种xml属性仍然被记录，未设置则默认换肤
  app:skin="false"
  // skin属性是否具有传播性，
  // 如果为true，则所有子节点和叶子节点都将继承该view的app:skin属性
  // 如果为false，则不具备传播性
  // 未设置则默认不具备传播性
  app:skin_forDescendants="true"
</code></pre>
示例：该布局下的所有View都将失去换肤能力，包括inflate的视图（inflate时传递了parent参数），除非某个子View设置了app:skin_forDescendants="false"属性，打断这个节点上的传播性
<pre><code>
  app:skin="false"
  app:skin_forDescendants="true"
</code></pre>

示例：指定某个布局具有换肤能力，其它视图不影响（比如指定了某个ViewGroup，那么该ViewGroup下的所有fragment等视图都有换肤能力），如果项目是以aar的方式被其它项目使用，而且需要具有换肤能力，但同时不能影响到外部，则可以使用这种方式。
<pre><code>
  // 如果没有设置app:skin属性，则不进行换肤
  SkinManger.setSkinAttrStrategy(ParseOutValue.SKIN_ATTR_UNDEFINE, false)

  // 指定视图可以换肤，而且具有传播性，从而使只有该ViewGroup下的视图才具有换肤能力
  app:skin="true"
  app:skin_forDescendants="true"
</code></pre>


