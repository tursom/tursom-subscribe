package cn.tursom.subscribe

import cn.tursom.subscribe.context.GlobalContext
import kotlinx.coroutines.flow.consumeAsFlow

suspend fun main() {
  val globalContext = GlobalContext()
  globalContext.subscribeContext.subscribeChannel("1837471", ).consumeAsFlow()
    .collect {
      if (globalContext.videoContext.lastUpdate(it.mid) > 0) {
        return@collect
      }
      globalContext.videoContext.updateVideos(it.mid, true)
    }
  //println(globalContext.subscribeContext.listSubscribe("1837471").toPrettyJson())
}