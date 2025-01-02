package com.xiaoxiaoying.pwdview.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xiaoxiaoying.pwdview.R
import com.xiaoxiaoying.pwdview.databinding.ItemDeleteBinding
import com.xiaoxiaoying.pwdview.databinding.ItemKeyboardBinding
import com.xiaoxiaoying.pwdview.model.CustomKeyboardModel
import com.xiaoxiaoying.pwdview.model.KeyboardItemConfig


/**
 * @author xiaoxiaoying
 * @date 2022/3/14
 */
class CustomKeyboardAdapter(
    private val context: Context,
    val config: KeyboardItemConfig = KeyboardItemConfig()
) : RecyclerView.Adapter<CustomKeyboardAdapter.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_DELETE = -2
        const val VIEW_TYPE_EMPTY = -1
    }

    var onDeleteLongCall: () -> Unit = {}

    var onItemClick: (Any) -> Unit = { _ -> }

    private val array: MutableList<Any> = mutableListOf()

    fun add(item: Any) {
        synchronized(this) {
            array.add(item)
        }
        notifyItemInserted(array.size - 1)
    }

    fun clear() {
        synchronized(this) {
            array.clear()
        }
        notifyDataSetChanged()
    }

    private fun getItemResourceId(viewType: Int): Int {
        return when (viewType) {
            VIEW_TYPE_ITEM -> R.layout.item_keyboard
            else -> R.layout.item_delete
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(getItemResourceId(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)

        onBindView(holder, position, getItemViewType(position), getItem(position), payloads)
    }

    private fun getItem(position: Int): Any? {
        if (position >= array.size) {
            return null
        }
        return array[position]
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is CustomKeyboardModel -> VIEW_TYPE_ITEM
            is Int -> item
            else -> super.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int = array.size

    private fun onBindView(
        holder: ViewHolder,
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

                root.root.setOnClickListener {
                    onItemClick(t)
                }

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
                root.root.setOnClickListener {
                    onItemClick(t)
                }
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


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}