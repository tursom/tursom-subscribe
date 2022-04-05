package cn.tursom.subscribe.context

import cn.tursom.subscribe.entity.Subscribe
import kotlinx.coroutines.channels.ReceiveChannel

interface SubscribeContext {
  suspend fun subscribeChannel(uid: String): ReceiveChannel<Subscribe>
  suspend fun listSubscribe(uid: String): List<Subscribe>
  suspend fun listSubscribe(uid: String, page: Int, pageSize: Int): List<Subscribe>
  interface Data : SubscribeContext {
    suspend fun lastUpdate(uid: String): Long = 0
    suspend fun updateSubscribe(uid: String, full: Boolean = false) = updateSubscribe(listSubscribe(uid))
    fun updateSubscribe(subscribes: List<Subscribe>): Int
    fun updateSubscribe(subscribe: Subscribe): Int
  }
}