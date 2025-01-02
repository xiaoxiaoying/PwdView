package com.xiaoxiaoying.pwdview.compose.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoxiaoying.pwdview.compose.R
import com.xiaoxiaoying.pwdview.compose.state.CustomKeyboardState
import com.xiaoxiaoying.pwdview.compose.utils.hexToColor

/**
 * @Creator (创建者) xiaoxiaoying
 * @CreateTime （创建时间） 2024/12/12 15:28
 * @Description (描述)
 * @ModifyAuthor (最新修改者)
 * @LastChangeTime (最后修改时间)
 */
class KeyboardConfig {

    /**
     * 数字排序类型 默认的为 [SortType.SORT_TYPE_INSANITY]
     * * [SortType.SORT_TYPE_INSANITY] 错乱排序
     * * [SortType.SORT_TYPE_ORDER] 常规键盘排序
     *
     */
    var sortType: Int = SortType.SORT_TYPE_INSANITY


    /**
     * 内边距
     */
    var contentPadding: Dp = 6.dp

    /**
     * 键盘垂直间距
     */
    var verticalSpace: Dp = 2.dp

    /**
     * 键盘水平间距
     */
    var horizontalSpace: Dp = 6.dp

    /**
     * 圆角，默认4dp
     * 如果是圆形，则设置为-1
     */
    var shape: Dp = 4.dp

    /**
     * 键盘字体样式
     */
    var textStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 14.sp,
        fontWeight = FontWeight.W600
    )

    var hintContent: String = "xiaoxiaoying"

    var hintColor: Color = "#3F434A".hexToColor()

    var buttonColor: Color = Color.White

    var backgroundColor: Color = "#D2D5DB".hexToColor()

    var isVibrate: Boolean = true
    var isSoundEffect: Boolean = true

    @DrawableRes
    var deleteRes: Int = R.drawable.ic_delete

    var deleteColor: Color = textStyle.color

    object SortType {
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

@Composable
fun CustomKeyboardView(
    modifier: Modifier = Modifier,
    config: KeyboardConfig = KeyboardConfig(),
    onKeyClick: (key: String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current

    val model by remember {
        mutableStateOf(CustomKeyboardState(context, config))
    }
    val numberArray = model.numberArray

    LaunchedEffect(config.sortType) {
        model.getNumberArray()
    }

    DisposableEffect(model) {
        onDispose {
            model.onDispose()
        }
    }

    Surface(modifier) {
        // 实现键盘布局和按键逻辑
        // 例如，使用 Grid 或其他布局来创建键盘的网格
        // 然后，为每个按键设置点击事件
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(config.contentPadding),
            verticalArrangement = Arrangement.spacedBy(config.verticalSpace),
            horizontalArrangement = Arrangement.spacedBy(config.horizontalSpace),
            modifier = Modifier
                .fillMaxWidth()
                .background(config.backgroundColor)
        ) {
            items(numberArray, key = { it }) { key ->
                when (key) {
                    -1 -> {
                        Spacer(Modifier.fillMaxWidth())
                    }

                    -2 -> {
                        // 删除按钮
                        DeleteKeyboard(onDeleteClick = {
                            model.onItemClick()
                            onDeleteClick()
                        }, painterResource(config.deleteRes), ColorFilter.tint(config.deleteColor))
                    }

                    else -> {
                        NumberKeyboard(key, config) {
                            model.onItemClick()
                            onKeyClick(it)
                        }
                    }
                }

            }

            item(span = { GridItemSpan(3) }) {
                Text(
                    config.hintContent,
                    color = config.hintColor,
                    fontSize = config.textStyle.fontSize,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = config.verticalSpace)
                )
            }
        }
    }
}

@Composable
private fun DeleteKeyboard(
    onDeleteClick: () -> Unit,
    deletePainter: Painter = painterResource(R.drawable.ic_delete),
    colorFilter: ColorFilter = ColorFilter.tint(Color.Black)
) {
    // 删除按钮


    IconButton(
        onClick = onDeleteClick,
    ) {
        Image(
            painter = deletePainter,
            contentDescription = "delete",
            modifier = Modifier
                .height(36.dp)
                .width(36.dp)
                .padding(8.dp),
            colorFilter = colorFilter
        )
    }
}

@Composable
private fun NumberKeyboard(item: Int, config: KeyboardConfig, onKeyClick: (key: String) -> Unit) {
    Button(
        onClick = {
            onKeyClick(item.toString())
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = config.buttonColor
        ),
        shape = if (config.shape == (-1).dp) ButtonDefaults.shape else RoundedCornerShape(config.shape)
    ) {
        Text(text = item.toString(), style = config.textStyle)
    }
}


@Preview
@Composable
fun CustomKeyboardViewPreview() {
    CustomKeyboardView(onKeyClick = {}, onDeleteClick = {})
}