package cn.tursom.subscribe.context

import cn.tursom.core.coroutine.bufferTicker
import cn.tursom.core.coroutine.fastSlowTicker
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.runBlocking

class TickerContextImpl @OptIn(ObsoleteCoroutinesApi::class) constructor(
  override val ticker: ReceiveChannel<Unit> = fastSlowTicker(
    fastTicker = bufferTicker(500, 5),
    slowTicker = ticker(5000),
  ),
) : TickerContext {

  override suspend fun wait() {
    ticker.receive()
  }

  override fun blockingWait() = runBlocking {
    wait()
  }
}
