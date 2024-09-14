package com.xiaoxiaoying.pwdview.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xiaoxiaoying.pwdview.R
import com.xiaoxiaoying.pwdview.adapter.CustomKeyboardAdapter
import com.xiaoxiaoying.pwdview.model.CustomKeyboardModel
import com.xiaoxiaoying.pwdview.model.KeyboardItemConfig
import com.xiaoxiaoying.pwdview.utils.dipToPix
import com.xiaoxiaoying.pwdview.utils.vibrator
import com.xiaoxiaoying.recyclerarrayadapter.listener.OnItemClickListener
import kotlin.random.Random

/**
 * @author xiaoxiaoying
 * @date 2022/3/14
 */
class CustomKeyboardView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attr, defStyleAttr, defStyleRes) {
    private var adapter: CustomKeyboardAdapter? = null
    private var numberArray = arrayListOf<Int>()
    private var hint: AppCompatTextView? = null
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.iphone_hint)
    }

    /**
     * 按键是否有声音效果 默认有
     */
    var isSoundEffect: Boolean = true

    /**
     * 是否震动 默认震动
     */
    var isVibrate: Boolean = true

    /**
     * 数字排序类型 默认的为 [SORT_TYPE_INSANITY]
     * * [SORT_TYPE_INSANITY] 错乱排序
     * * [SORT_TYPE_ORDER] 常规键盘排序
     *
     */
    var sortType: Int = SORT_TYPE_INSANITY
        set(value) {
            if (value == sortType)
                return
            field = value
            changeNumber()
        }


    /**
     * 数字点击回调
     */
    var onNumberClickCall: ((number: Int) -> Unit)? = null

    /**
     * 数字删除回调
     */
    var onNumberDeleteCall: ((isLongClick: Boolean) -> Unit)? = null

    /**
     * 底部提示文案
     */
    var hintText: CharSequence? = ""
        set(value) {
            field = value
            hint?.text = value
        }

    var hintTextColor: Int = Color.parseColor("#3F434A")
        set(value) {
            field = value
            hint?.setTextColor(value)
        }
    var hintTextSize: Int = context.dipToPix(14)
        set(value) {
            field = value
            hint?.setTextSize(TypedValue.COMPLEX_UNIT_PX, value.toFloat())
        }
    private var viewBackgroundColor: Int = Color.parseColor("#D2D5DB")

    var itemBackground: Drawable? = null
        set(value) {
            if (field == value)
                return
            field = value
            adapter?.config?.itemBackground = value
            val itemCount = adapter?.itemCount ?: 0
            if (itemCount > 0) {
                adapter?.notifyItemRangeChanged(0, itemCount - 1)
            }
        }
    var itemTextColor: Int = Color.BLACK
        set(value) {
            if (field == value)
                return
            field = value
            adapter?.config?.itemTextColor = value
            val itemCount = adapter?.itemCount ?: 0
            if (itemCount > 0) {
                adapter?.notifyItemRangeChanged(0, itemCount - 1)
            }
        }
    var itemTextSize: Int = context.dipToPix(25)
        set(value) {
            if (field == value)
                return
            field = value
            adapter?.config?.itemTextSize = value
            val itemCount = adapter?.itemCount ?: 0
            if (itemCount > 0) {
                adapter?.notifyItemRangeChanged(0, itemCount - 1)
            }
        }

    var deleteColor: Int = itemTextColor
        set(value) {
            if (field == value)
                return
            field = value
            adapter?.config?.deleteColor = deleteColor
            val itemCount = adapter?.itemCount ?: 0
            if (itemCount > 0) {
                adapter?.notifyItemChanged(itemCount - 1)
            }
        }
    var deleteDrawable: Drawable? = null
        set(value) {
            if (field == value)
                return
            field = value
            adapter?.config?.deleteDrawable = deleteDrawable
            val itemCount = adapter?.itemCount ?: 0
            if (itemCount > 0) {
                adapter?.notifyItemChanged(itemCount - 1)
            }
        }

    init {
        val array = context.obtainStyledAttributes(
            attr,
            R.styleable.CustomKeyboardView,
            defStyleAttr,
            defStyleRes
        )
        hintText = array.getString(R.styleable.CustomKeyboardView_hintText)
        isSoundEffect = array.getBoolean(R.styleable.CustomKeyboardView_isSoundEffect, true)
        isVibrate = array.getBoolean(R.styleable.CustomKeyboardView_isVibrate, true)
        sortType = array.getInt(R.styleable.CustomKeyboardView_sortType, SORT_TYPE_INSANITY)
        hintTextColor = array.getColor(R.styleable.CustomKeyboardView_hintTextColor, Color.BLACK)
        hintTextSize = array.getDimensionPixelSize(R.styleable.CustomKeyboardView_hintTextSize, 0)

        viewBackgroundColor =
            array.getColor(R.styleable.CustomKeyboardView_backgroundColor, Color.BLACK)
        itemBackground = array.getDrawable(R.styleable.CustomKeyboardView_itemBackground)

        itemTextColor = array.getColor(R.styleable.CustomKeyboardView_itemTextColor, Color.BLACK)
        itemTextSize = array.getDimensionPixelSize(R.styleable.CustomKeyboardView_itemTextSize, 0)

        deleteColor = array.getColor(R.styleable.CustomKeyboardView_deleteColor, Color.BLACK)
        deleteDrawable = array.getDrawable(R.styleable.CustomKeyboardView_deleteDrawable)
        array.recycle()
    }


    private fun sort() {
        val tempList = arrayListOf<Int>()
        while (numberArray.size > 0) {
            tempList.add(numberArray.getNumber())
        }
        numberArray = tempList
    }

    private fun ArrayList<Int>.getNumber(): Int {
        val index = Random.nextInt(size)
        val value = get(index)
        val last = get(size - 1)
        set(index, last)
        remove(last)
        return value
    }

    private fun onItemClick(t: Any?, view: View) {
        t ?: return
        try {
            if (isSoundEffect) {
                mediaPlayer.start()
            }
            if (isVibrate) {
                context?.vibrator(30L)
            }
        } catch (_: Exception) {

        }

        when (t) {
            is CustomKeyboardModel -> {
                try {
                    onNumberClickCall?.invoke(t.content.toString().toInt())
                } catch (_: Exception) {

                }
            }

            else -> {
                onNumberDeleteCall?.invoke(false)
            }
        }

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val keyboardView = inflate(context, R.layout.customer_keyboard, this)
        keyboardView?.setBackgroundColor(viewBackgroundColor)
        val keyboard = keyboardView?.findViewById<RecyclerView>(R.id.keyboardRecycler)
        hint = keyboardView?.findViewById(R.id.hintKey)

        adapter = CustomKeyboardAdapter(context, KeyboardItemConfig().apply {
            this.itemBackground = this@CustomKeyboardView.itemBackground
            this.deleteColor = this@CustomKeyboardView.deleteColor
            this.itemTextSize = this@CustomKeyboardView.itemTextSize
            this.itemTextColor = this@CustomKeyboardView.itemTextColor
            this.deleteDrawable = this@CustomKeyboardView.deleteDrawable
        })
        keyboard?.adapter = adapter
        changeNumber()
        val div = context.dipToPix(3)
        keyboard?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.top = div
                outRect.bottom = div
                val params = view.layoutParams as GridLayoutManager.LayoutParams
                val index = params.spanIndex
                outRect.left = if (index == 0)
                    div * 2 else div
                outRect.right = if (index == 2)
                    div * 2 else div
            }
        })
        adapter?.onItemClickListener = object : OnItemClickListener<Any> {
            override fun onItemClick(t: Any?, view: View) {
                this@CustomKeyboardView.onItemClick(t, view)
            }
        }
        adapter?.onDeleteLongCall = {
            onNumberDeleteCall?.invoke(true)
        }
    }

    private fun changeNumber() {
        adapter?.clean()
        numberArray.clear()
        if (sortType == SORT_TYPE_ORDER) {
            repeat(9) {
                numberArray.add(it + 1)
            }
            numberArray.add(0)
        } else {
            repeat(10) {
                numberArray.add(it)
            }
            sort()
        }

        numberArray.forEachIndexed { index, i ->
            adapter?.add(CustomKeyboardModel().apply {
                content = i.toString()
            })
            if (index == 8) {
                adapter?.add(CustomKeyboardAdapter.VIEW_TYPE_EMPTY)
            }
        }
        adapter?.add(CustomKeyboardAdapter.VIEW_TYPE_DELETE)
    }

    override fun onDetachedFromWindow() {
        mediaPlayer.release()
        super.onDetachedFromWindow()
    }

    companion object {
        /**
         * 常规键盘排序
         */
        const val SORT_TYPE_ORDER = 1

        /**
         * 错乱排序
         */
        const val SORT_TYPE_INSANITY = 0
    }
}