package com.xiaoxiaoying.pwdview.compose.utils

import androidx.compose.ui.graphics.Color

/**
 * @Creator (创建者) xiaoxiaoying
 * @CreateTime （创建时间） 2024/12/12 18:06
 * @Description (描述)
 * @ModifyAuthor (最新修改者)
 * @LastChangeTime (最后修改时间)
 */

fun String.hexToColor(a: Float = 1f): Color {
    if ((this.length != 7 && this.length != 9) || this[0] != '#') {
        return Color.Transparent
    }
    val hex = this.replace("#", "")

    if (length == 9) {
        val alpha = Integer.valueOf(hex.substring(0, 2), 16)
        val blue = Integer.valueOf(hex.substring(6, 8), 16)
        val red = Integer.valueOf(hex.substring(2, 4), 16)
        val green = Integer.valueOf(hex.substring(4, 6), 16)
        return Color(red, green, blue, alpha)
    }

    val red = Integer.valueOf(hex.substring(0, 2), 16)
    val green = Integer.valueOf(hex.substring(2, 4), 16)
    val blue = Integer.valueOf(hex.substring(4, 6), 16)

    return Color(red, green, blue, (a * 255).toInt())
}