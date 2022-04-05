package cn.tursom.subscribe.entity

data class User(
  var uid: String,
  var uname: String? = null,
  var cookie: String? = null,
  var type: UserType? = null,
)
