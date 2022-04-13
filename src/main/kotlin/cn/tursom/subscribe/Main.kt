package cn.tursom.subscribe

import cn.tursom.core.hours
import cn.tursom.http.client.Okhttp
import cn.tursom.subscribe.context.GlobalContext
import cn.tursom.subscribe.context.VideoContext
import kotlinx.coroutines.flow.consumeAsFlow
import okhttp3.OkHttp

suspend fun main() {
  val globalContext = GlobalContext()
  val t = System.currentTimeMillis() - 12.hours().toMillis()
  globalContext.subscribeContext.subscribeChannel("1837471").consumeAsFlow()
    .collect {
      if (globalContext.videoContext.lastUpdate(it.mid) > t) {
        return@collect
      }
      globalContext.videoContext.updateVideos(
        it.mid, false,
        VideoContext.contextEnv.newContext().set(VideoContext.httpClient, Okhttp.proxy("127.0.0.1", 2080))
      )
    }
  //println(globalContext.subscribeContext.listSubscribe("1837471").toPrettyJson())
}