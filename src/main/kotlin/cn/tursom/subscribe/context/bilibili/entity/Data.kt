package cn.tursom.subscribe.context.bilibili.entity

data class Data<T>(
  val code: Int = 0,
  @Suppress("UNCHECKED_CAST")
  val data: T = null as T,
  val message: String = "",
  val ttl: Int = 0,
  val msg: String = "",
)
