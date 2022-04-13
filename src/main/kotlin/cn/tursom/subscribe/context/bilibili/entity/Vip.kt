package cn.tursom.subscribe.context.bilibili.entity

data class Vip(
  val accessStatus: Int = 0,
  val avatar_subscript: Int = 0,
  val avatar_subscript_url: String = "",
  val dueRemark: String = "",
  val label: Label = Label(),
  val nickname_color: String = "",
  val themeType: Int = 0,
  val vipDueDate: Long = 0,
  val vipStatus: Int = 0,
  val vipStatusWarn: String = "",
  val vipType: Int = 0,
) {
  data class Label(
    val bg_color: String = "",
    val bg_style: Int = 0,
    val border_color: String = "",
    val label_theme: String = "",
    val path: String = "",
    val text: String = "",
    val text_color: String = "",
  )
}
