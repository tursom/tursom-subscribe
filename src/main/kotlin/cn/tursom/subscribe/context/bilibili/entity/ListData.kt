package cn.tursom.subscribe.context.bilibili.entity

data class ListData<T>(
  val list: List<T> = emptyList(),
  val re_version: Int = 0,
  val total: Int = 0,
)
