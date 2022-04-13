package cn.tursom.subscribe.context.ktorm

import cn.tursom.database.ktorm.*
import cn.tursom.database.ktorm.ext.DirectSqlExpression.Companion.sql
import cn.tursom.subscribe.context.KVContext
import cn.tursom.subscribe.context.UserContext
import cn.tursom.subscribe.entity.Subscribe
import cn.tursom.subscribe.entity.User
import cn.tursom.subscribe.entity.UserType
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.where
import org.ktorm.expression.ColumnExpression
import org.ktorm.schema.IntSqlType

class KtormUserContext(
  private val database: Database,
  private val kvContext: KVContext,
) : UserContext {
  companion object {
    private const val dbVersionKey = "KtormUserContext_dbVersion"
    private const val userCookieKey = "KtormUserContext_userCookie"
    private val tableSql = listOf(
      "create table if not exists user (" +
        "uid text  not null," +
        "type int not null," +
        "PRIMARY KEY (uid, type)" +
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

    database.from<Subscribe>().select().where {
      IntSqlType sql "(select count(*) from user u where u.uid = subscribe.mid)" eq 0
      ColumnExpression(
        null,
        "(select count(*) from user u where u.uid = subscribe.mid)",
        IntSqlType,
        isLeafNode = true
      ) eq 0
    }.toList<Subscribe>().forEach {
      try {
        database.insert(User(
          uid = it.mid,
          uname = it.uname,
          type = UserType.BILIBILI,
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

  override fun getCookie(type: UserType, uid: String, host: String): String? =
    kvContext[userCookieKey, type.data.toString(), uid, host]

  override fun setCookie(type: UserType, uid: String, host: String, cookie: String) {
    kvContext[userCookieKey, type.data.toString(), uid, host] = cookie
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