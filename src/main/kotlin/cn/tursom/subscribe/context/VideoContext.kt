package cn.tursom.subscribe.context

import cn.tursom.core.context.ArrayContextEnv
import cn.tursom.core.context.Context
import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.http.client.AsyncHttpRequest
import cn.tursom.subscribe.entity.Video
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import okhttp3.OkHttpClient

interface VideoContext {
  companion object {
    val contextEnv = ArrayContextEnv()
    val paramsKey = contextEnv.newKey<Map<String, String>>().withDefault { emptyMap() }
    val countKey = contextEnv.newKey<Int>().withDefault { 0 }
    val pageKey = contextEnv.newKey<Int>()
    val httpClient = contextEnv.newKey<OkHttpClient>().withDefault { AsyncHttpRequest.defaultClient }
  }

  suspend fun listVideos(
    uid: String,
    page: Int,
    pageSize: Int = 30,
    context: Context = contextEnv.emptyContext(),
  ): List<Video>

  suspend fun listVideos(
    uid: String,
    context: Context = contextEnv.emptyContext(),
  ): List<Video>

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun videoChannel(
    uid: String,
    page: Int = 1,
    pageSize: Int = 30,
    context: Context = contextEnv.emptyContext(),
  ): ReceiveChannel<Video> = GlobalScope.produce {
    listVideos(uid).forEach {
      send(it)
    }
  }

  interface Data : VideoContext {
    suspend fun lastUpdate(uid: String): Long = 0
    fun saveVideo(video: Video): Int
    fun saveVideos(video: List<Video>): Int
    suspend fun updateVideos(
      uid: String,
      full: Boolean = false,
      context: Context = contextEnv.emptyContext(),
    ): Int = 0
  }
}