package cn.tursom.subscribe.entity

data class Video(
  var id: Int? = null,
  var type: UserType,
  var uid: String,
  var vid: String,
  var createTime: Long,
  var title: String,
  var cover: String? = null,
  var aid: Int? = null,
  var bvid: String? = null,
  var raw: String? = null,
)