package top.iwill.numberview

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.*

/**
 * @description:
 * @author: btcw
 * @date: 2019/7/18
 */
abstract class BaseCoroutineActivity : AppCompatActivity(),
    CoroutineScope by CoroutineScope(Dispatchers.Main  + SupervisorJob()) {

    protected val TAG: String
        get() = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        launch { initListener() }
        launch { initData() }
    }

    open suspend fun initListener() {}

    open suspend fun initData() {}

    abstract fun getLayoutId(): Int

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}