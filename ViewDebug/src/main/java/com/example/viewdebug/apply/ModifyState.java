package com.example.viewdebug.apply;

/**
 * 资源更改状态
 */
public enum ModifyState {
    // 已经应用了
    APPLIED,
    // 重启更新,
    REBOOT_UPDATABLE,
    // 无法应用
    INVALID_APPLY,
    // 未知状态
    UNKNOWN
}
