package cn.tursom.subscribe.context.bilibili.entity

data class DynamicNewList(
  val _gt_: Int = 0,
  val attentions: Attentions = Attentions(),
  val cards: List<Card> = emptyList(),
  val exist_gap: Int = 0,
  val history_offset: Long = 0,
  val max_dynamic_id: Long = 0,
  val new_num: Int = 0,
  val open_rcmd: Int = 0,
  val update_num: Int = 0,
) {
  data class Attentions(
    val bangumis: List<Bangumi> = emptyList(),
    val uids: List<Int> = emptyList(),
  ) {
    data class Bangumi(
      val season_id: Int = 0,
      val type: Int = 0,
    )
  }
}


