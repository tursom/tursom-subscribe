package cn.tursom.subscribe.context.bilibili

import cn.tursom.core.Utils.gson
import cn.tursom.core.context.Context
import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.core.fromJsonTyped
import cn.tursom.http.client.getStr
import cn.tursom.subscribe.context.TickerContext
import cn.tursom.subscribe.context.VideoContext
import cn.tursom.subscribe.context.bilibili.entity.BVideo
import cn.tursom.subscribe.context.bilibili.entity.Data
import cn.tursom.subscribe.context.bilibili.entity.VideoList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce

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
    val videos = context[VideoContext.httpClient].getStr(
      "https://api.bilibili.com/x/space/arc/search",
      param = baseParam + mapOf(
        "mid" to uid,
        "ps" to pageSize.toString(),
        "pn" to page.toString(),
      ) + context[VideoContext.paramsKey],
    )
    try {
      val data = gson.fromJsonTyped<Data<VideoList<BVideo>>>(videos)
      context[VideoContext.countKey] = data.data.page.count
      context[VideoContext.pageKey] = page
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
      page++
      total = videos.page.count
      videos.list.vlist.forEach {
        send(it.toVideo())
      }
    }
  }
}

