package cn.tursom.subscribe.context

import kotlinx.coroutines.channels.ReceiveChannel

interface TickerContext {
  val ticker: ReceiveChannel<Unit>
  suspend fun wait()
  fun blockingWait()
}