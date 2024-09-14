package com.xiaoxiaoying.pwdview.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * create time 2022/11/16
 * @author xiaoxiaoying
 */

fun Context.getVibrator(): Vibrator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}

/**
 * @param duration 振幅时常
 * @param amplitude 震感强度
 */

fun Context.vibrator(duration: Long, amplitude: Int = -1) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrationEffect = VibrationEffect.createOneShot(duration, amplitude)
        getVibrator()?.vibrate(vibrationEffect)
    } else {
        getVibrator()?.vibrate(duration)
    }

}


fun Context.cancelVibrator() {
    getVibrator()?.cancel()
}
