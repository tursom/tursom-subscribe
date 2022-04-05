package cn.tursom.subscribe.context.bilibili

import cn.tursom.core.Utils.gson
import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.core.fromJsonTyped
import cn.tursom.core.toJson
import cn.tursom.http.client.AsyncHttpRequest
import cn.tursom.subscribe.context.SubscribeContext
import cn.tursom.subscribe.context.TickerContext
import cn.tursom.subscribe.context.UserContext
import cn.tursom.subscribe.context.bilibili.entity.Data
import cn.tursom.subscribe.context.bilibili.entity.ListData
import cn.tursom.subscribe.entity.Subscribe
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
    params: Map<String, String> = emptyMap(),
  ): Data<ListData<Follower>> {
    val cookie = userContext.getCookie(uid) ?: throw UnauthorizedUserException("unauthorized user $uid")
    tickerContext.wait()
    var subscribes = AsyncHttpRequest.getStr(
      "https://api.bilibili.com/x/relation/followings",
      param = baseParam + mapOf(
        "vmid" to uid,
        "pn" to page.toString(),
        "ps" to pageSize.toString(),
      ) + params,
      headers = mapOf(
        "cookie" to cookie,
        "referer" to "https://space.bilibili.com/1837471/fans/follow",
      )
    )
    subscribes = subscribes.substring(6, subscribes.length - 1)
    return gson.fromJsonTyped(subscribes)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun followingChannel(
    uid: String,
    pageSize: Int = 20,
    params: Map<String, String> = emptyMap(),
  ): ReceiveChannel<Follower> = GlobalScope.produce {
    var page = 0
    var total = Int.MAX_VALUE
    while (page * pageSize < total) {
      page++
      val followings = followings(uid, page, pageSize, params).data
      total = followings.total
      followings.list.forEach {
        send(it)
      }
    }
  }

  suspend fun followings(uid: String, pageSize: Int = 20, params: Map<String, String> = emptyMap()): List<Follower> {
    var page = 0
    var total = Int.MAX_VALUE
    return buildList {
      while (page * pageSize < total) {
        page++
        val followings = followings(uid, page, pageSize, params).data
        total = followings.total
        addAll(followings.list)
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun subscribeChannel(uid: String): ReceiveChannel<Subscribe> = GlobalScope.produce {
    val channel = followingChannel(uid)
    while (true) {
      send(channel.receive().toSubscribe(uid))
    }
  }

  override suspend fun listSubscribe(uid: String) = followings(uid).map { it.toSubscribe(uid) }

  override suspend fun listSubscribe(uid: String, page: Int, pageSize: Int) =
    followings(uid, page, pageSize).data.list.map { it.toSubscribe(uid) }

  data class Follower(
    val attribute: Int,
    val contract_info: ContractInfo,
    val face: String,
    val face_nft: Int,
    val mid: Int,
    val mtime: Int,
    val official_verify: OfficialVerify,
    val sign: String,
    val special: Int,
    val tag: Any,
    val uname: String,
    val vip: Vip,
  ) {
    fun toSubscribe(uid: String) = Subscribe(
      uid = uid,
      mid = mid.toString(),
      uname = uname,
      raw = toJson(),
    )

    data class OfficialVerify(
      val desc: String,
      val type: Int,
    )

    data class ContractInfo(
      val is_contract: Boolean,
      val is_contractor: Boolean,
      val ts: Int,
      val user_attr: Int,
    )

    data class Vip(
      val accessStatus: Int,
      val avatar_subscript: Int,
      val avatar_subscript_url: String,
      val dueRemark: String,
      val label: Label,
      val nickname_color: String,
      val themeType: Int,
      val vipDueDate: Long,
      val vipStatus: Int,
      val vipStatusWarn: String,
      val vipType: Int,
    ) {
      data class Label(
        val bg_color: String,
        val bg_style: Int,
        val border_color: String,
        val label_theme: String,
        val path: String,
        val text: String,
        val text_color: String,
      )
    }
  }
}

