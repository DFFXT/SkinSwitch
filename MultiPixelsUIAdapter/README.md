# 分辨率适配
如果产品或者UI要求适配不同分辨率的设备，而且UI给了对应分辨率的标注，那么就可以使用这个插件。

比如，UI给了4个分辨率1920x720、1920x1080、1080x1920、1624x1150的标注
普通方式进行分辨率适配一般要写4个布局，很难维护，一改就是改4个layout的xml，很容易遗漏。
## 使用这个插件后的分辨率适配方式
通常情况下4个标准分辨率只需要一个布局，不同分辨率之间的差异使用尺寸资源来控制。
示例：假设不同分辨率下TextView的宽高不一样
实现如下：
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="@android:color/holo_blue_light">

    <ImageView
        android:id="@+id/textView"
        android:layout_width="@dimen/m_app_entrances_size"
        android:layout_height="@dimen/m_app_entrances_size"
        android:textSize="18sp"
        android:textColor="@android:color/black" />
</LinearLayout>
```
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="m_app_entrances_size">20dp</dimen>
    <dimen name="m_app_entrances_size_vertical">200dp</dimen>
    <dimen name="m_app_entrances_size_square">150dp</dimen>
    <dimen name="m_app_entrances_size_large">400dp</dimen>
</resources>
```
// readme.md中插入链接

通过这种方式，插件会自动计算设备的分辨率和哪个UI标准更接近，然后使用对应的UI资源（内部使用[AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize)插件来进行缩放），并加载对应的尺寸资源（当然其它资源也可以），这样就是实现了更简单的分辨率适配。

## 插件使用方式
```kotlin
implementation "com.github.DFFXT.SkinSwitch:MultiPixelsUIAdapter:0.22.7"
```
### 初始化
```kotlin
    // application.onCreate中添加以下代码
    // 1280x720分辨率下的UI，默认无后缀
    MultiPixelsAdjustManager.addMultiPixelsSuffix(MultiPixelsAdjust.UIConfig(1920, 720), "")
    // 1624x1150分辨率下的UI，后缀为_square
    MultiPixelsAdjustManager.addMultiPixelsSuffix(MultiPixelsAdjust.UIConfig(1624, 1150), "_square")
    // 1920x1080分辨率下的UI，后缀为_large
    MultiPixelsAdjustManager.addMultiPixelsSuffix(MultiPixelsAdjust.UIConfig(1920, 1080), "_large")
    // 1080x1920分辨率下的UI，后缀为_vertical
    MultiPixelsAdjustManager.addMultiPixelsSuffix(MultiPixelsAdjust.UIConfig(1080, 1920), "_vertical")
    // 计算使用哪个标准
    MultiPixelsAdjustManager.recalculate()
```
