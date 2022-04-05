package cn.tursom.subscribe.context

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.withContext

class CachedSubscribeContext(
  private val userContext: UserContext,
  private val dbSubscribeContext: SubscribeContext.Data,
  private val httpSubscribeContext: SubscribeContext,
  private val kvContext: KVContext,
) : SubscribeContext.Data by dbSubscribeContext {
  companion object : Exception() {
    const val lastUpdateKey = "CachedSubscribeContext_lastUpdateKey"
  }

  override suspend fun lastUpdate(uid: String) = withContext(Dispatchers.IO) {
    kvContext[lastUpdateKey, uid]?.toLong() ?: 0
  }

  override suspend fun updateSubscribe(uid: String, full: Boolean): Int {
    var update = 0
    try {
      var failure = 0
      httpSubscribeContext.subscribeChannel(uid)
        .consumeAsFlow()
        .collect {
          userContext.setUname(it.uid, it.uname ?: "")
          val updateSubscribe = dbSubscribeContext.updateSubscribe(it)
          update += updateSubscribe
          if (!full && updateSubscribe != 1) {
            failure++
            if (failure > 10) {
              throw Companion
            }
          }
        }
    } catch (_: Companion) {
    }
    withContext(Dispatchers.IO) {
      kvContext[lastUpdateKey, uid] = System.currentTimeMillis().toString()
    }
    return update
  }
}