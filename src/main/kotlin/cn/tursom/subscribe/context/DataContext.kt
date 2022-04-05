package cn.tursom.subscribe.context

interface DataContext {
  val userContext: UserContext
  val subscribeContext: SubscribeContext.Data
  val kvContext: KVContext
  val videoContext: VideoContext
}
