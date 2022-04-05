package cn.tursom.subscribe.context.bilibili

import cn.tursom.core.Utils.gson
import cn.tursom.core.context.Context
import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.core.fromJsonTyped
import cn.tursom.core.toJson
import cn.tursom.http.client.AsyncHttpRequest
import cn.tursom.subscribe.context.TickerContext
import cn.tursom.subscribe.context.VideoContext
import cn.tursom.subscribe.context.bilibili.entity.Data
import cn.tursom.subscribe.context.bilibili.entity.VideoList
import cn.tursom.subscribe.entity.UserType
import cn.tursom.subscribe.entity.Video
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import okhttp3.OkHttpClient
import java.net.Proxy

class BilibiliVideoContext(
  private val tickerContext: TickerContext,
) : VideoContext {
  companion object {
    private val baseParam = mapOf(
      "tid" to "0",
      "order" to "pubdate",
      "jsonp" to "jsonp",
      "keyword" to "",
    )
  }

  suspend fun listBilibiliVideos(
    uid: String,
    page: Int,
    pageSize: Int = 30,
    context: Context,
  ): Data<VideoList<BVideo>> {
    tickerContext.wait()
    val videos = AsyncHttpRequest.getStr(
      "https://api.bilibili.com/x/space/arc/search",
      param = baseParam + mapOf(
        "mid" to uid,
        "ps" to pageSize.toString(),
        "pn" to page.toString(),
      ) + context[VideoContext.paramsKey],
      client = context[VideoContext.httpClient],
    )
    try {
      val data = gson.fromJsonTyped<Data<VideoList<BVideo>>>(videos)
      context.set(VideoContext.countKey, data.data.page.count)
      return data
    } catch (e: Exception) {
      println(videos)
      throw e
    }
  }

  suspend fun listBilibiliVideos(uid: String, context: Context): List<BVideo> {
    val pageSize = 30
    var page = 0
    var total = Int.MAX_VALUE
    return buildList {
      while (page * pageSize < total) {
        page++
        val videos = listBilibiliVideos(uid, page, pageSize, context).data
        total = videos.page.count
        addAll(videos.list.vlist)
      }
    }
  }

  override suspend fun listVideos(uid: String, page: Int, pageSize: Int, context: Context) =
    listBilibiliVideos(uid, page, pageSize, context).data.list.vlist.map {
      it.toVideo()
    }

  override suspend fun listVideos(uid: String, context: Context) =
    listBilibiliVideos(uid, context).map { it.toVideo() }

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun videoChannel(uid: String, page: Int, pageSize: Int, context: Context) = GlobalScope.produce {
    @Suppress("NAME_SHADOWING") var page = page
    var total = Int.MAX_VALUE
    while ((page - 1) * pageSize < total) {
      val videos = listBilibiliVideos(uid, page, pageSize, context).data
      context.set(VideoContext.pageKey, page)
      page++
      total = videos.page.count
      videos.list.vlist.forEach {
        send(it.toVideo())
      }
    }
  }

  data class BVideo(
    val aid: Int,
    val author: String,
    val bvid: String,
    val comment: Int,
    val copyright: String,
    val created: Int,
    val description: String,
    val hide_click: Boolean,
    val is_live_playback: Int,
    val is_pay: Int,
    val is_steins_gate: Int,
    val is_union_video: Int,
    val length: String,
    val mid: Int,
    val pic: String,
    val play: String,
    val review: Int,
    val subtitle: String,
    val title: String,
    val typeid: Int,
    val video_review: Int,
  ) {
    fun toVideo() = Video(
      uid = mid.toString(),
      vid = bvid,
      createTime = created * 1000L,
      title = title,
      cover = pic,
      aid = aid,
      bvid = bvid,
      raw = toJson(),
      type = UserType.BILIBILI,
    )
  }
}

