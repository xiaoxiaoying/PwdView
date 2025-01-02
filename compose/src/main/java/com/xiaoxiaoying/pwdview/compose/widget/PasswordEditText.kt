package com.xiaoxiaoying.pwdview.compose.widget

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoxiaoying.pwdview.compose.utils.hexToColor
import kotlin.math.abs


class PasswordEditConfig {
    /**
     * 长度
     */
    var length = 6

    /**
     * 边框宽度
     */
    var borderWidth = 1f

    /**
     * 边框圆角
     */
    var borderCorner: Dp = 4.dp

    /**
     * 边框颜色
     */
    var borderColor = "#d1d2d6".hexToColor()

    /**
     * 模式
     * * [Mode.MODE_DIALOG] 弹窗模式 默认
     * * [Mode.MODE_ACTIVITY] 线在底部
     */
    var mode: Int = Mode.MODE_DIALOG

    /**
     * 分割线间隔
     */
    var space: Dp = 4.dp

    /**
     * 分割线大小
     */
    var divisionLineSize: Float = 1F

    var passwordColor: Color = Color.Black

    var cursorColor: Color = passwordColor

    /**
     * 输入类型
     * * [InputType.INPUT_TYPE_PASS] 密码
     * * [InputType.INPUT_TYPE_NUMBER] 数字
     */
    var inputType: Int = InputType.INPUT_TYPE_PASS

    /**
     * 密码类型
     * * [Type.PWD_TYPE_ORIGIN] 圆点
     * * [Type.PWD_TYPE_STAR] 星号
     */
    var pwdType: Int = Type.PWD_TYPE_ORIGIN

    /**
     * 键盘类型
     * * [KeyboardType.KEYBOARD_TYPE_CUSTOM] 自定义
     * * [KeyboardType.KEYBOARD_TYPE_SYSTEM] 系统
     */
    var keyboardType: Int = KeyboardType.KEYBOARD_TYPE_SYSTEM

    var passwordRadius: Dp = 4.dp

    var itemBackgroundColor: Color = Color.Transparent

    object Mode {
        const val MODE_DIALOG = 0
        const val MODE_ACTIVITY = 1
    }

    object InputType {
        const val INPUT_TYPE_PASS = 0
        const val INPUT_TYPE_NUMBER = 1
    }

    object Type {
        const val PWD_TYPE_ORIGIN = 0
        const val PWD_TYPE_STAR = 1
    }

    object KeyboardType {

        const val KEYBOARD_TYPE_CUSTOM = 0
        const val KEYBOARD_TYPE_SYSTEM = 1
    }
}


/**
 * @Creator (创建者) xiaoxiaoying
 * @CreateTime （创建时间） 2024/12/12 19:47
 * @Description (描述)
 * @ModifyAuthor (最新修改者)
 * @LastChangeTime (最后修改时间)
 */
@Composable
fun PasswordEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    config: PasswordEditConfig = PasswordEditConfig()
) {

    BasicTextField(
        value,
        onValueChange,
        modifier,
        singleLine = true,
        enabled = enabled,
        readOnly = readOnly || config.keyboardType == PasswordEditConfig.KeyboardType.KEYBOARD_TYPE_CUSTOM,
        textStyle = textStyle,
        maxLines = config.length,
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                config.keyboardType == PasswordEditConfig.KeyboardType.KEYBOARD_TYPE_CUSTOM -> KeyboardType.Unspecified
                config.inputType == PasswordEditConfig.InputType.INPUT_TYPE_PASS -> KeyboardType.Password
                config.inputType == PasswordEditConfig.InputType.INPUT_TYPE_NUMBER -> KeyboardType.Number
                else -> KeyboardType.Number
            }
        ),
        decorationBox = @Composable {
            EditContent(value, textStyle, config)
        }
    )
}

@Composable
fun PasswordEditText(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    config: PasswordEditConfig = PasswordEditConfig()
) {

    BasicTextField(
        value,
        onValueChange,
        modifier,
        singleLine = true,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        maxLines = config.length,
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                config.inputType == PasswordEditConfig.InputType.INPUT_TYPE_PASS -> KeyboardType.Password
                config.inputType == PasswordEditConfig.InputType.INPUT_TYPE_NUMBER -> KeyboardType.Number
                config.keyboardType == PasswordEditConfig.KeyboardType.KEYBOARD_TYPE_CUSTOM -> KeyboardType.Unspecified
                else -> KeyboardType.Number
            }
        ),
        decorationBox = @Composable {
            EditContent(value.text, textStyle, config)
        }
    )
}

@Composable
private fun EditContent(
    value: String = "",
    textStyle: TextStyle = LocalTextStyle.current,
    config: PasswordEditConfig = PasswordEditConfig()
) {

    var length = value.length

    if (length > config.length) {
        length = config.length
    }

    val textMeasurer = rememberTextMeasurer(cacheSize = 0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // 设置透明背景
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val boxWidth = size.width
            var boxHeight = size.height
            val lineWidth =
                (boxWidth - (config.length - 1) * config.space.value) / config.length.toFloat()
            if (boxHeight <= 0) {
                boxHeight = lineWidth
            }

            when {
                config.mode == PasswordEditConfig.Mode.MODE_ACTIVITY -> {
                    repeat(config.length) { index ->
                        val startY = boxHeight - config.borderWidth
                        val startX = index * (lineWidth + config.space.value)
                        val endX = startX + lineWidth
                        drawLine(
                            color = if (length == index) config.cursorColor else config.borderColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, startY),
                            strokeWidth = config.borderWidth
                        )
                    }
                }

                config.space.value > 0 -> {
                    val endY = boxHeight - config.borderWidth
                    repeat(config.length) { index ->
                        val startX = index * (lineWidth + config.space.value)
                        val endX = startX + lineWidth
                        val rect =
                            RectF(
                                startX + config.borderWidth,
                                config.borderWidth,
                                endX - config.borderWidth,
                                endY
                            )

                        val topLeft =
                            Offset(
                                startX + config.borderWidth,
                                config.borderWidth
                            )

                        val size = Size(rect.width(), rect.height())

                        drawRoundRect(
                            config.itemBackgroundColor,
                            topLeft,
                            size,
                            cornerRadius = CornerRadius(
                                x = config.borderCorner.value,
                                y = config.borderCorner.value
                            )
                        )

                        drawRoundRect(
                            if (length == index) config.cursorColor else config.borderColor,
                            topLeft,
                            size,
                            style = Stroke(config.borderWidth),
                            cornerRadius = CornerRadius(
                                x = config.borderCorner.value,
                                y = config.borderCorner.value
                            )
                        )
                    }
                }


                else -> {
                    val rect = RectF(
                        config.borderWidth,
                        config.borderWidth,
                        boxWidth - config.borderWidth,
                        boxHeight - config.borderWidth
                    )

                    val topLeft =
                        Offset(
                            config.borderWidth,
                            config.borderWidth
                        )

                    val size = Size(rect.width(), rect.height())
                    drawRoundRect(
                        config.itemBackgroundColor,
                        topLeft,
                        size,
                        cornerRadius = CornerRadius(
                            x = config.borderCorner.value,
                            y = config.borderCorner.value
                        )
                    )
                    drawRoundRect(
                        config.borderColor,
                        topLeft,
                        size,
                        style = Stroke(config.borderWidth),
                        cornerRadius = CornerRadius(
                            x = config.borderCorner.value,
                            y = config.borderCorner.value
                        )
                    )

                    repeat(config.length - 1) {
                        val mPasswordItemWidth =
                            (boxWidth - config.borderWidth * 2 - (config.length - 1) * config.divisionLineSize) / config.length
                        val startX =
                            config.borderWidth + (it + 1) * mPasswordItemWidth + it * config.divisionLineSize
                        val startY = config.borderWidth
                        val endY = boxHeight - config.borderWidth * 2
                        drawLine(
                            color = config.borderColor,
                            start = Offset(startX, startY),
                            end = Offset(startX, endY),
                            strokeWidth = config.divisionLineSize
                        )
                    }


                }

            }


            // 绘制密码
            repeat(length) {

                val cx = abs(lineWidth * it + it * config.space.value + lineWidth / 2F)
                val cy = boxHeight / 2F
                when (config.inputType) {
                    PasswordEditConfig.InputType.INPUT_TYPE_PASS -> {
                        when (config.pwdType) {
                            PasswordEditConfig.Type.PWD_TYPE_STAR -> {
                                val textLayoutResult = textMeasurer.measure(
                                    text = "*",
                                    style = textStyle,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val textSize = textLayoutResult.size
                                drawText(
                                    textLayoutResult,
                                    config.passwordColor,
                                    Offset(
                                        cx - textSize.width / 2F,
                                        cy - textSize.height / 2F
                                    )
                                )

                            }

                            else -> {

                                drawCircle(
                                    config.passwordColor,
                                    config.passwordRadius.value,
                                    Offset(cx, cy),
                                )
                            }
                        }

                    }

                    PasswordEditConfig.InputType.INPUT_TYPE_NUMBER -> {
                        val content = value[it].toString()
                        val textLayoutResult = textMeasurer.measure(
                            text = content,
                            style = textStyle,
                            overflow = TextOverflow.Ellipsis
                        )
                        val textSize = textLayoutResult.size
                        drawText(
                            textLayoutResult,
                            config.passwordColor,
                            Offset(cx - textSize.width / 2F, cy - textSize.height / 2F)
                        )
                    }
                }
            }
        }

    }
}

@Preview
@Composable
fun PasswordEditTextPreview() {
    PasswordEditText(
        Modifier
            .fillMaxWidth()
            .background(Color.White),
        value = TextFieldValue("123"),
        onValueChange = {},
        config = PasswordEditConfig().apply {
            space = 6.dp
            mode = PasswordEditConfig.Mode.MODE_DIALOG
            passwordRadius = 10.dp
            pwdType = PasswordEditConfig.Type.PWD_TYPE_STAR
            inputType = PasswordEditConfig.InputType.INPUT_TYPE_NUMBER
        })
}