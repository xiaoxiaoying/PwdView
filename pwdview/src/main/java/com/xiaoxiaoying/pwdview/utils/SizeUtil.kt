package com.xiaoxiaoying.pwdview.utils

import android.content.Context
import android.util.TypedValue


fun Context.dipToPix(dip: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip.toFloat(),
        this.resources.displayMetrics
    )
        .toInt()
}
