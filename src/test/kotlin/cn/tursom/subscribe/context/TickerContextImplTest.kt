package cn.tursom.subscribe.context

import cn.tursom.core.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.time.Duration.Companion.seconds

internal class TickerContextImplTest {
  private val ticker = TickerContextImpl()

  @Test
  fun testWait() {
    runBlocking {
      repeat(100) {
        ticker.wait()
        println("$it ${System.currentTimeMillis()}")
        if ((it != 0) && ((it % 10) == 0)) {
          delay(10.seconds().toMillis())
        }
      }
    }
  }
}