package cn.tursom.subscribe.context

import cn.tursom.subscribe.entity.User
import cn.tursom.subscribe.entity.UserType

interface UserContext {
  fun listUser(): List<User>
  fun getUser(uid: String): User?

  //fun getCookie(uid: String): String? = getUser(uid)?.cookie
  //fun setCookie(uid: String, cookie: String)
  fun getCookie(type: UserType, uid: String, host: String): String?
  fun setCookie(type: UserType, uid: String, host: String, cookie: String)
  fun setUname(uid: String, uname: String)
}