package cn.tursom.subscribe.context

import cn.tursom.core.context.ArrayContextEnv
import cn.tursom.core.context.Context
import cn.tursom.http.client.Okhttp
import cn.tursom.subscribe.entity.Subscribe
import kotlinx.coroutines.channels.ReceiveChannel
import okhttp3.OkHttpClient

interface SubscribeContext {
  companion object {
    val contextEnv = ArrayContextEnv()
    val paramsKey = contextEnv.newKey<Map<String, String>>().withDefault { emptyMap() }
    val countKey = contextEnv.newKey<Int>()
    val pageKey = contextEnv.newKey<Int>()
    val httpClient = contextEnv.newKey<OkHttpClient>().withDefault { Okhttp.default }
  }

  suspend fun subscribeChannel(uid: String, ctx: Context = contextEnv.emptyContext()): ReceiveChannel<Subscribe>
  suspend fun listSubscribe(uid: String, ctx: Context = contextEnv.emptyContext()): List<Subscribe>
  suspend fun listSubscribe(
    uid: String,
    page: Int,
    pageSize: Int,
    ctx: Context = contextEnv.emptyContext(),
  ): List<Subscribe>

  interface Data : SubscribeContext {
    suspend fun lastUpdate(uid: String): Long = 0
    suspend fun updateSubscribe(uid: String, full: Boolean = false, ctx: Context = contextEnv.emptyContext()) =
      updateSubscribe(listSubscribe(uid, ctx))

    fun updateSubscribe(subscribes: List<Subscribe>): Int
    fun updateSubscribe(subscribe: Subscribe): Int
  }
}