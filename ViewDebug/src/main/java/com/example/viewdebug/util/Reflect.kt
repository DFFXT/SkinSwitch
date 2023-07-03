package com.example.viewdebug.util

internal val fragmentViewLifecycleOwnerFragmentFiled by lazy {
    val filed = Class.forName("androidx.fragment.app.FragmentViewLifecycleOwner").getDeclaredField("mFragment")
    filed.isAccessible = true
    filed
}