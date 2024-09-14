package com.xiaoxiaoying.pwdview.adapter

import android.content.Context
import android.util.TypedValue
import com.xiaoxiaoying.pwdview.R
import com.xiaoxiaoying.pwdview.databinding.ItemDeleteBinding
import com.xiaoxiaoying.pwdview.databinding.ItemKeyboardBinding
import com.xiaoxiaoying.pwdview.model.CustomKeyboardModel
import com.xiaoxiaoying.pwdview.model.KeyboardItemConfig

import com.xiaoxiaoying.recyclerarrayadapter.adapter.SimpleAdapter

/**
 * @author xiaoxiaoying
 * @date 2022/3/14
 */
class CustomKeyboardAdapter(
    context: Context,
    val config: KeyboardItemConfig = KeyboardItemConfig()
) : SimpleAdapter<Any>(context) {
    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_DELETE = -2
        const val VIEW_TYPE_EMPTY = -1
    }

    var onDeleteLongCall: () -> Unit = {}

    override fun getItemResourceId(viewType: Int): Int {
        return when (viewType) {
            VIEW_TYPE_ITEM -> R.layout.item_keyboard
            else -> R.layout.item_delete
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is CustomKeyboardModel -> VIEW_TYPE_ITEM
            is Int -> item
            else -> super.getItemViewType(position)
        }
    }

    override fun onBindView(
        holder: ViewHolder<Any>,
        position: Int,
        viewType: Int,
        t: Any?,
        payloads: MutableList<Any>
    ) {
        t ?: return

        holder.itemView.isEnabled = false
        when (viewType) {
            VIEW_TYPE_ITEM -> {
                if (t !is CustomKeyboardModel)
                    return
                val root = ItemKeyboardBinding.bind(holder.itemView)

                root.root.isEnabled = true
                root.content.setTextColor(config.itemTextColor)
                if (config.itemBackground != null) {
                    root.root.background = config.itemBackground
                }
                root.content.text = t.content
                if (config.itemTextSize > 0) {
                    root.content.textSize = config.itemTextSize.toFloat()
                } else {
                    root.content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25F)
                }

            }

            VIEW_TYPE_DELETE -> {
                val root = ItemDeleteBinding.bind(holder.itemView)
                root.root.isEnabled = true
                root.root.setOnLongClickListener {
                    onDeleteLongCall()
                    return@setOnLongClickListener true
                }
                if (config.deleteDrawable != null) {
                    root.icon.setBackgroundDrawable(config.deleteDrawable)
                } else {
                    root.icon.setBackgroundResource(R.drawable.ic_delete)
                    root.icon.setColorFilter(config.deleteColor)
                }

            }
        }

    }

}