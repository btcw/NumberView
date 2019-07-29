package top.iwill.numberview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseCoroutineActivity() {

    override fun getLayoutId() = R.layout.activity_main

    override suspend fun initData() {
        number_view.setCurrValue("0")
        add.setOnClickListener {
            launch {
                repeat(9) {
                    number_view.setNextValue("${it + 1}")
                    delay(2000)
                }
            }
        }
    }
}
