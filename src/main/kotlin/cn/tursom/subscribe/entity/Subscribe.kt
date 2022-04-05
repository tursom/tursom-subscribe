package cn.tursom.subscribe.entity

data class Subscribe(
  var id: Int? = null,
  var uid: String,
  var mid: String,
  var uname: String? = null,
  var raw: String? = null,
)