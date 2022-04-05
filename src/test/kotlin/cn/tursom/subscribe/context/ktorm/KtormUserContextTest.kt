package cn.tursom.subscribe.context.ktorm

import cn.tursom.subscribe.context.GlobalContext
import cn.tursom.subscribe.context.ktorm.KtormUserContext
import org.junit.jupiter.api.Test

import org.ktorm.database.Database

internal class KtormUserContextTest {
  private val globalContext = GlobalContext()
  private val ctx = globalContext.userContext

  @Test
  fun getUser() {
    println(ctx.getUser("1837471"))
  }

  @Test
  fun setCookie() {
  }
}