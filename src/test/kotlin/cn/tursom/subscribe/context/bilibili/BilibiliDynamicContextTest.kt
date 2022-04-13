package cn.tursom.subscribe.context.bilibili

import cn.tursom.core.toPrettyJson
import cn.tursom.subscribe.context.GlobalContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class BilibiliDynamicContextTest {
  private val globalContext = GlobalContext()
  private val ctx = BilibiliDynamicContext(
    globalContext.tickerContext,
    globalContext.userContext,
  )

  @Test
  fun new() {
    val dynamicNewList = runBlocking {
      ctx.new("1837471")
    }.data

    println(dynamicNewList.toPrettyJson())
  }

  @Test
  fun history() {
    val dynamicNewList = runBlocking {
      ctx.history("1837471", 647159238379962375)
    }.data

    println(dynamicNewList.toPrettyJson())
  }
}