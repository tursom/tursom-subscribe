package cn.tursom.subscribe.context.ktorm

import cn.tursom.subscribe.context.GlobalContext
import cn.tursom.subscribe.context.bilibili.BilibiliSubscribeContext
import cn.tursom.subscribe.context.TickerContextImpl
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.ktorm.database.Database

internal class KtormSubscribeContextTest {
  companion object {
    const val uid = "1837471"
  }

  private val globalContext = GlobalContext()
  private val ctx = KtormSubscribeContext(
    Database.connect(
      "jdbc:sqlite:subscribe.db",
      "org.sqlite.JDBC",
    ),
  )

  @OptIn(ObsoleteCoroutinesApi::class)
  private val httpCtx = BilibiliSubscribeContext(
    globalContext.userContext,
    globalContext.tickerContext,
  )

  @Test
  fun createTable() {
    ctx.createTable()
  }

  @Test
  fun updateSubscribe() {
    val subscribe = runBlocking {
      httpCtx.listSubscribe(uid)
    }
    ctx.updateSubscribe(subscribe)
  }
}