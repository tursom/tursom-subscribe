package cn.tursom.subscribe.context.bilibili

import cn.tursom.core.Utils.gson
import cn.tursom.core.fromJsonTyped
import cn.tursom.subscribe.context.TickerContext
import cn.tursom.subscribe.context.UserContext
import cn.tursom.subscribe.context.bilibili.entity.Data
import cn.tursom.subscribe.context.bilibili.entity.DynamicHistory
import cn.tursom.subscribe.context.bilibili.entity.DynamicNewList
import cn.tursom.subscribe.entity.UserType
import cn.tursom.subscribe.exception.UnauthorizedUserException
import cn.tursom.web.client.HttpClient
import cn.tursom.web.client.okhttp.OkhttpHttpClient

class BilibiliDynamicContext(
  private val tickerContext: TickerContext,
  private val userContext: UserContext,
) {
  companion object {
    data class Context(
      val client: HttpClient<*> = OkhttpHttpClient.default,
    )

    private const val host = "api.vc.bilibili.com"

    //uid: 1837471
    //offset_dynamic_id: 645924525475627026
    //type: 8
    //from:
    //platform: web
    private const val history = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_history"
    private val defaultHistoryParams = mapOf(
      "type" to "8",
      "from" to "",
      "platform" to "web",
    )

    //uid: 1837471
    //type_list: 8
    //from:
    //platform: web
    private const val new = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new"
    private val defaultNewParams = mapOf(
      "type_list" to "8",
      "from" to "",
      "platform" to "web",
    )
  }

  suspend fun new(uid: String, ctx: Context = Context()): Data<DynamicNewList> {
    val cookie = userContext.getCookie(UserType.BILIBILI, uid, host)
      ?: throw UnauthorizedUserException("unauthorized user $uid")
    tickerContext.wait()
    val newStr = ctx.client.request(
      "GET",
      new,
    ).addParams(
      defaultNewParams + mapOf(
        "uid" to uid
      )).addHeaders(mapOf(
      "Cookie" to cookie,
      "Origin" to "https://t.bilibili.com",
      "Referer" to "https://t.bilibili.com/",
      "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36",
    )).send().body.string()
    return gson.fromJsonTyped(newStr)
  }

  suspend fun history(uid: String, offsetDynamicId: Long, ctx: Context = Context()): Data<DynamicHistory> {
    val cookie = userContext.getCookie(UserType.BILIBILI, uid, host)
      ?: throw UnauthorizedUserException("unauthorized user $uid")
    tickerContext.wait()
    val newStr = ctx.client.request(
      "GET",
      history,
    ).addParams(defaultHistoryParams + mapOf(
      "uid" to uid,
      "offset_dynamic_id" to offsetDynamicId.toString(),
    )).addHeaders(mapOf(
      "Cookie" to cookie,
      "Origin" to "https://t.bilibili.com",
      "Referer" to "https://t.bilibili.com/",
      "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36",
    )).send().body.string()
    return gson.fromJsonTyped(newStr)
  }
}
