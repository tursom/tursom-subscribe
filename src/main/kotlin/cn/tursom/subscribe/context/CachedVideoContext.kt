package cn.tursom.subscribe.context

import cn.tursom.core.context.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.withContext

class CachedVideoContext(
  private val dbVideoContext: VideoContext.Data,
  private val httpVideoContext: VideoContext,
  private val kvContext: KVContext,
) : VideoContext.Data by dbVideoContext {
  companion object : Exception() {
    const val lastUpdateKey = "CachedVideoContext_lastUpdateKey"
    const val updatePageKey = "CachedVideoContext_updatePageKey"
  }

  override suspend fun lastUpdate(uid: String) = withContext(Dispatchers.IO) {
    kvContext[lastUpdateKey, uid]?.toLong() ?: 0
  }

  override suspend fun updateVideos(uid: String, full: Boolean, context: Context): Int {
    val noCache = kvContext[updatePageKey, uid] == null
    var update = 0
    if (full) {
      update += fullUpdateVideos(uid)
    }
    if (!noCache) {
      try {
        var failure = 0
        httpVideoContext.videoChannel(uid, context = context)
          .consumeAsFlow()
          .collect {
            val updateSubscribe = dbVideoContext.saveVideo(it)
            update += updateSubscribe
            if (updateSubscribe != 0) {
              return@collect
            }
            failure++
            if (failure > 10) {
              throw Companion
            }
          }
      } catch (_: Companion) {
      }
    }
    withContext(Dispatchers.IO) {
      kvContext[lastUpdateKey, uid] = System.currentTimeMillis().toString()
    }
    return update
  }

  private suspend fun fullUpdateVideos(uid: String): Int {
    var update = 0
    var page = kvContext[updatePageKey, uid]?.toInt() ?: 1
    val context = VideoContext.contextEnv.newContext()
    httpVideoContext.videoChannel(uid, page, 30, context)
      .consumeAsFlow()
      .collect {
        page = context[VideoContext.pageKey] ?: page
        kvContext[updatePageKey, uid] = page.toString()
        update += dbVideoContext.saveVideo(it)
      }
    return update
  }
}