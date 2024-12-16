package com.xiaoxiaoying

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoxiaoying.pwdview.compose.utils.hexToColor
import com.xiaoxiaoying.pwdview.compose.widget.CustomKeyboardView
import com.xiaoxiaoying.pwdview.compose.widget.KeyboardConfig
import com.xiaoxiaoying.pwdview.compose.widget.PasswordEditConfig
import com.xiaoxiaoying.pwdview.compose.widget.PasswordEditText
import com.xiaoxiaoying.pwdview.ui.theme.PwdViewTheme

/**
 * @Creator (创建者) xiaoxiaoying
 * @CreateTime （创建时间） 2024/12/13 18:51
 * @Description (描述)
 * @ModifyAuthor (最新修改者)
 * @LastChangeTime (最后修改时间)
 */
class MainComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PwdViewTheme {
                MainCompose {
                    finish()
                }
            }
        }
    }
}

@Composable
private fun MainCompose(onBackClick: () -> Unit) {


    var inputText by remember {
        mutableStateOf("")
    }

    Scaffold(Modifier.fillMaxSize(), topBar = @Composable {
        IconButton(onClick = onBackClick) {
            Text("返回")
        }
    }, content = { paddingValues ->
        val modifier = Modifier.padding(paddingValues)

        Column(modifier = modifier) {

            PasswordEditText(
                value = inputText, onValueChange = {
                    inputText = it
                }, modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                config = PasswordEditConfig().apply {
                    space = 4.dp
                    borderColor = "#FF0000".hexToColor()
                    borderWidth = 2F
                    itemBackgroundColor = "#CC000000".hexToColor()
                    borderCorner = 6.dp
                    cursorColor = "#FF0000".hexToColor()
                    passwordColor = Color.White
                    passwordRadius = 12.dp
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600
                )
            )

            PasswordEditText(
                value = inputText, onValueChange = {
                    inputText = it
                }, modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                config = PasswordEditConfig().apply {
                    space = 0.dp
                    borderColor = "#FF0000".hexToColor()
                    borderWidth = 2F
                    borderCorner = 6.dp
                    cursorColor = "#FF0000".hexToColor()
                    divisionLineSize = 3F
                    pwdType = PasswordEditConfig.Type.PWD_TYPE_STAR
                }
            )

            PasswordEditText(
                value = inputText, onValueChange = {
                    inputText = it
                }, modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                config = PasswordEditConfig().apply {
                    space = 6.dp
                    borderWidth = 2F
                    borderCorner = 6.dp
                    cursorColor = "#FF0000".hexToColor()
                    mode = PasswordEditConfig.Mode.MODE_ACTIVITY
                    inputType = PasswordEditConfig.InputType.INPUT_TYPE_NUMBER
                    keyboardType = PasswordEditConfig.KeyboardType.KEYBOARD_TYPE_CUSTOM
                }
            )

            Spacer(Modifier.weight(1f))

            CustomKeyboardView(Modifier.fillMaxWidth(), KeyboardConfig().apply {

            }, onKeyClick = {
                inputText += it
            }) {
                inputText = if (inputText.isNotEmpty() && inputText.length > 1) {
                    inputText.substring(0, inputText.length - 1)
                } else {
                    ""
                }

            }

        }

    })
}

@Preview
@Composable
fun MainComposePreview() {
    MainCompose {}
}