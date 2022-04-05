package cn.tursom.subscribe.context.bilibili.entity

data class VideoList<T>(
  val episodic_button: EpisodicButton,
  val list: VList<T>,
  val page: Page,
) {
  data class VList<T>(
    val tlist: Map<String, Type>,
    val vlist: List<T>,
  )

  data class Page(
    val count: Int,
    val pn: Int,
    val ps: Int,
  )

  data class EpisodicButton(
    val text: String,
    val uri: String,
  )

  data class Type(
    val count: Int,
    val name: String,
    val tid: Int,
  )
}