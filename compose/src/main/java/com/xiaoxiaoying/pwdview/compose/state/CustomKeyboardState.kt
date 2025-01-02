package com.xiaoxiaoying.pwdview.compose.state

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.xiaoxiaoying.pwdview.compose.R
import com.xiaoxiaoying.pwdview.compose.utils.vibrator
import com.xiaoxiaoying.pwdview.compose.widget.KeyboardConfig
import java.lang.ref.WeakReference
import kotlin.random.Random

/**
 * @Creator (创建者) xiaoxiaoying
 * @CreateTime （创建时间） 2024/12/12 17:01
 * @Description (描述)
 * @ModifyAuthor (最新修改者)
 * @LastChangeTime (最后修改时间)
 */
class CustomKeyboardState(context: Context, private val config: KeyboardConfig = KeyboardConfig()) {

    private val mContext = WeakReference(context)

    private var mediaPlayer: MediaPlayer? = null
    val numberArray = SnapshotStateList<Int>()

    fun getNumberArray() {
        numberArray.clear()
        if (config.sortType == KeyboardConfig.SortType.SORT_TYPE_ORDER) {
            numberArray.addAll((1..9))
            numberArray.add(-1)
            numberArray.add(0)
            numberArray.add(-2)
        } else {
            val arrayList = (0..9).toMutableList()
            repeat(arrayList.size) {
                numberArray.add(arrayList.getNumber())
            }
            numberArray.add(9, -1)
            numberArray.add(-2)
        }
    }

    private fun MutableList<Int>.getNumber(): Int {
        if (isEmpty()) return -1
        val index = Random.nextInt(size)
        return removeAt(index)
    }


    private fun initMediaPlayer() {
        if (mediaPlayer != null) return
        mediaPlayer = MediaPlayer.create(mContext.get(), R.raw.iphone_hint)
    }

    fun onItemClick() {
        if (config.isSoundEffect) {
            initMediaPlayer()
            mediaPlayer?.start()
        }
        if (config.isVibrate) {
            mContext.get()?.vibrator(30L)
        }
    }

    fun onDispose() {
        mediaPlayer?.release()
    }
}