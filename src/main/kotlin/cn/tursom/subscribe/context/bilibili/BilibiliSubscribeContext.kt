package cn.tursom.subscribe.context.bilibili

import cn.tursom.core.Utils.gson
import cn.tursom.core.context.Context
import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.core.fromJsonTyped
import cn.tursom.http.client.getStr
import cn.tursom.subscribe.context.SubscribeContext
import cn.tursom.subscribe.context.TickerContext
import cn.tursom.subscribe.context.UserContext
import cn.tursom.subscribe.context.bilibili.entity.Data
import cn.tursom.subscribe.context.bilibili.entity.Follower
import cn.tursom.subscribe.context.bilibili.entity.ListData
import cn.tursom.subscribe.entity.Subscribe
import cn.tursom.subscribe.entity.UserType
import cn.tursom.subscribe.exception.UnauthorizedUserException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

class BilibiliSubscribeContext(
  private val userContext: UserContext,
  private val tickerContext: TickerContext,
) : SubscribeContext {
  companion object {
    private val baseParam = mapOf(
      "order" to "desc",
      //"order_type" to "attention",
      "order_type" to "",
      "jsonp" to "jsonp",
      "callback" to "__jp5"
    )
  }

  suspend fun followings(
    uid: String,
    page: Int,
    pageSize: Int,
    ctx: Context = SubscribeContext.contextEnv.emptyContext(),
  ): Data<ListData<Follower>> {
    val cookie = userContext.getCookie(UserType.BILIBILI, uid, "api.bilibili.com")
      ?: throw UnauthorizedUserException("unauthorized user $uid")
    tickerContext.wait()
    var subscribes = ctx[SubscribeContext.httpClient].getStr(
      "https://api.bilibili.com/x/relation/followings",
      param = baseParam + mapOf(
        "vmid" to uid,
        "pn" to page.toString(),
        "ps" to pageSize.toString(),
      ) + ctx[SubscribeContext.paramsKey],
      headers = mapOf(
        "cookie" to cookie,
        "referer" to "https://space.bilibili.com/1837471/fans/follow",
      )
    )
    subscribes = subscribes.substring(6, subscribes.length - 1)
    val data = gson.fromJsonTyped<Data<ListData<Follower>>>(subscribes)
    ctx[SubscribeContext.countKey] = data.data.total
    ctx[SubscribeContext.pageKey] = page
    return data
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun followingChannel(
    uid: String,
    pageSize: Int = 20,
    ctx: Context = SubscribeContext.contextEnv.emptyContext(),
  ): ReceiveChannel<Follower> = GlobalScope.produce {
    var page = 0
    var total = Int.MAX_VALUE
    while (page * pageSize < total) {
      page++
      val followings = followings(uid, page, pageSize, ctx).data
      total = followings.total
      followings.list.forEach {
        send(it)
      }
    }
  }

  suspend fun followings(
    uid: String,
    pageSize: Int = 20,
    ctx: Context = SubscribeContext.contextEnv.emptyContext(),
  ): List<Follower> {
    var page = 0
    var total = Int.MAX_VALUE
    return buildList {
      while (page * pageSize < total) {
        page++
        val followings = followings(uid, page, pageSize, ctx).data
        total = followings.total
        addAll(followings.list)
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun subscribeChannel(uid: String, ctx: Context): ReceiveChannel<Subscribe> = GlobalScope.produce {
    val channel = followingChannel(uid, ctx = ctx)
    while (true) {
      send(channel.receive().toSubscribe(uid))
    }
  }

  override suspend fun listSubscribe(uid: String, ctx: Context) = followings(uid, ctx = ctx).map { it.toSubscribe(uid) }

  override suspend fun listSubscribe(uid: String, page: Int, pageSize: Int, ctx: Context) =
    followings(uid, page, pageSize, ctx).data.list.map { it.toSubscribe(uid) }
}

