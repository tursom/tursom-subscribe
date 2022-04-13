package cn.tursom.subscribe.context.bilibili.entity

import cn.tursom.core.toJson
import cn.tursom.subscribe.entity.UserType
import cn.tursom.subscribe.entity.Video

data class BVideo(
  val aid: Int,
  val author: String,
  val bvid: String,
  val comment: Int,
  val copyright: String,
  val created: Int,
  val description: String,
  val hide_click: Boolean,
  val is_live_playback: Int,
  val is_pay: Int,
  val is_steins_gate: Int,
  val is_union_video: Int,
  val length: String,
  val mid: Int,
  val pic: String,
  val play: String,
  val review: Int,
  val subtitle: String,
  val title: String,
  val typeid: Int,
  val video_review: Int,
) {
  fun toVideo() = Video(
    uid = mid.toString(),
    vid = bvid,
    createTime = created * 1000L,
    title = title,
    cover = pic,
    aid = aid,
    bvid = bvid,
    raw = toJson(),
    type = UserType.BILIBILI,
  )
}