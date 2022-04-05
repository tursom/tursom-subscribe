package cn.tursom.subscribe.context.bilibili.entity

data class ListData<T>(
  val list: List<T>,
  val re_version: Int,
  val total: Int,
)
