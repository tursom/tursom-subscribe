package cn.tursom.subscribe.context

import cn.tursom.core.toPrettyJson
import cn.tursom.subscribe.context.bilibili.BilibiliSubscribeContext
import cn.tursom.subscribe.context.ktorm.KtormUserContext
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.ktorm.database.Database

internal class BilibiliSubscribeContextTest {
  private val globalContext = GlobalContext()
  private val ctx = BilibiliSubscribeContext(
    userContext = globalContext.dataContext.userContext,
    tickerContext = globalContext.tickerContext,
  )

  @Test
  fun listSubscribe() {
    runBlocking {
      println(ctx.listSubscribe("1837471", 1, 20))
    }
  }

  @Test
  fun followings() {
    runBlocking {
      println(ctx.followings("1837471").toPrettyJson())
    }
  }

  @Test
  fun subscribeChannel() {
    runBlocking {
      println(ctx.subscribeChannel("1837471").consumeAsFlow()
        .collect {
          println(it.uname)
        })
    }
  }
}