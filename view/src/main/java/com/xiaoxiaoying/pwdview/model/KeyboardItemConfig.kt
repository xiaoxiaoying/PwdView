package com.xiaoxiaoying.pwdview.model

import android.graphics.Color
import android.graphics.drawable.Drawable

/**
 * @Creator (创建者) xiaoxiaoying
 * @CreateTime （创建时间） 2024/9/14 14:29
 */
class KeyboardItemConfig {
    var itemBackground: Drawable? = null
    var itemTextColor: Int = Color.BLACK
    var itemTextSize: Int = 0
    var deleteColor: Int = itemTextColor
    var deleteDrawable: Drawable? = null
}