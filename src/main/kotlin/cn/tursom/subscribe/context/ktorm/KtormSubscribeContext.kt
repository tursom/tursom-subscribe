package cn.tursom.subscribe.context.ktorm

import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.database.ktorm.*
import cn.tursom.subscribe.context.SubscribeContext
import cn.tursom.subscribe.entity.Subscribe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.limit
import org.ktorm.dsl.where
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

class KtormSubscribeContext(
  private val database: Database,
) : SubscribeContext.Data {
  fun createTable() {
    database.useConnection {
      it.createStatement().use { statement ->
        statement.execute(
          "create table if not exists subscribe (" +
            "id integer PRIMARY KEY autoincrement," +
            "uid text not null," +
            "mid text not null," +
            "uname text," +
            "raw text" +
            ")"
        )
        statement.execute("create unique index if not exists subscriber on subscribe (uid, mid)")
      }
    }
  }

  override suspend fun updateSubscribe(uid: String, full: Boolean) = 0

  override fun updateSubscribe(subscribes: List<Subscribe>): Int {
    var insert = 0
    subscribes.forEach { s ->
      insert += updateSubscribe(s)
    }
    return insert
  }

  override fun updateSubscribe(subscribe: Subscribe): Int {
    return try {
      database.insert(subscribe)
      1
    } catch (e: SQLiteException) {
      when (e.resultCode) {
        SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE, SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY -> {
          database.update<Subscribe> {
            set(Subscribe::uname, subscribe.uname)
            set(Subscribe::raw, subscribe.raw)
            where {
              (Subscribe::uid eq subscribe.uid) and (Subscribe::mid eq subscribe.mid)
            }
          }
        }
        else -> throw e
      }
      0
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun subscribeChannel(uid: String): ReceiveChannel<Subscribe> = GlobalScope.produce {
    listSubscribe(uid).forEach {
      send(it)
    }
  }

  override suspend fun listSubscribe(uid: String) = database.from<Subscribe>()
    .select()
    .where {
      Subscribe::uid eq uid
    }
    .toList<Subscribe>()

  override suspend fun listSubscribe(uid: String, page: Int, pageSize: Int) = database.from<Subscribe>()
    .select()
    .where {
      Subscribe::uid eq uid
    }
    .limit((page - 1) * pageSize, pageSize)
    .toList<Subscribe>()
}