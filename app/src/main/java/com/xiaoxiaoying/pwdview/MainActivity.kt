package com.xiaoxiaoying.pwdview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xiaoxiaoying.pwdview.simple.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = ActivityMainBinding.inflate(layoutInflater)
        setContentView(root.root)
        root.passwordEditText.requestFocus()
        root.keyboard.onNumberClickCall = {
            root.passwordEditText.addPassword(it.toString())

        }

        root.keyboard.onNumberDeleteCall = {
            if (it) {
                root.passwordEditText.clearPassword()
            } else {
                root.passwordEditText.deletePassword()
            }

        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    PwdViewTheme {
//        Greeting("Android")
//    }
//}