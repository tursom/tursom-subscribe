package cn.tursom.subscribe.context.bilibili.entity

data class Data<T>(
  val code: Int,
  val `data`: T,
  val message: String,
  val ttl: Int,
)
