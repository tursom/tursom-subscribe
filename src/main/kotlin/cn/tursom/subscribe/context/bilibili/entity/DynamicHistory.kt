package cn.tursom.subscribe.context.bilibili.entity

data class DynamicHistory(
  val _gt_: Int,
  val cards: List<Card>,
  val fold_mgr: List<FoldMgr>,
  val has_more: Int,
  val next_offset: Long,
  val open_rcmd: Int,
) {
  data class FoldMgr(
    val fold_type: Int,
    val folds: List<Fold>,
  ) {
    data class Fold(
      val dynamic_ids: List<Long>,
    )
  }
}