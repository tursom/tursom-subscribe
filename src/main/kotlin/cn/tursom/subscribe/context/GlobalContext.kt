package cn.tursom.subscribe.context

import cn.tursom.subscribe.context.bilibili.BilibiliSubscribeContext
import cn.tursom.subscribe.context.bilibili.BilibiliVideoContext
import cn.tursom.subscribe.context.ktorm.KtormDataContext
import cn.tursom.subscribe.util.locked
import cn.tursom.subscribe.util.singleton
import org.sqlite.SQLiteDataSource

class GlobalContext(
  val tickerContext: TickerContext,
  val dataContext: DataContext,
  val kvContext: KVContext,
  val userContext: UserContext,
  val subscribeContext: SubscribeContext.Data,
  val videoContext: VideoContext.Data,
) {
  companion object {
    operator fun invoke(): GlobalContext {
      val tickerContext = TickerContextImpl()
      val dataContext = KtormDataContext(
        SQLiteDataSource().let {
          it.url = "jdbc:sqlite:subscribe.db"
          it.singleton.locked()
        },
      )
      val subscribeContext = CachedSubscribeContext(
        userContext = dataContext.userContext,
        dbSubscribeContext = dataContext.subscribeContext,
        httpSubscribeContext = BilibiliSubscribeContext(dataContext.userContext, tickerContext),
        kvContext = dataContext.kvContext,
      )
      val videoContext = CachedVideoContext(
        dbVideoContext = dataContext.videoContext,
        httpVideoContext = BilibiliVideoContext(tickerContext),
        kvContext = dataContext.kvContext,
      )
      return GlobalContext(
        tickerContext = tickerContext,
        dataContext = dataContext,
        kvContext = dataContext.kvContext,
        userContext = dataContext.userContext,
        subscribeContext = subscribeContext,
        videoContext = videoContext,
      )
    }
  }
}