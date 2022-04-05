package cn.tursom.subscribe.context.ktorm

import cn.tursom.database.ktorm.*
import cn.tursom.subscribe.context.KVContext
import cn.tursom.subscribe.context.UserContext
import cn.tursom.subscribe.entity.Subscribe
import cn.tursom.subscribe.entity.User
import cn.tursom.subscribe.entity.UserType
import org.ktorm.database.Database
import org.ktorm.dsl.where

class KtormUserContext(
  private val database: Database,
  private val kvContext: KVContext,
) : UserContext {
  companion object {
    private const val dbVersionKey = "KtormUserContext_dbVersion"
    private val tableSql = listOf(
      "create table if not exists user (" +
        "uid text primary key not null," +
        "cookie text," +
        "type int" +
        ")",
      "alter table user add uname text",
    )
  }

  init {
    UserType
  }

  fun createTable() {
    database.createTable(
      kvContext[dbVersionKey]?.toIntOrNull() ?: 0,
      tableSql,
    )
    kvContext[dbVersionKey] = tableSql.size.toString()

    database.from<Subscribe>().select().toList<Subscribe>().forEach {
      try {
        database.insert(User(
          uid = it.uid,
          uname = it.uname,
        ))
      } catch (_: Exception) {
      }
    }
  }

  override fun listUser(): List<User> = database.from<User>()
    .select()
    .toList()

  override fun getUser(uid: String) = database.from<User>()
    .select()
    .where {
      User::uid eq uid
    }
    .getOne<User>()

  override fun setCookie(uid: String, cookie: String) {
    database.update<User> {
      set(User::cookie, cookie)
      where {
        User::uid eq uid
      }
    }
  }

  override fun setUname(uid: String, uname: String) {
    if (database.update<User> {
        set(User::uname, uname)
        where {
          User::uid eq uid
        }
      } != 0) {
      return
    }

    database.insert(User(
      uid = uid,
      uname = uname,
    ))
  }
}