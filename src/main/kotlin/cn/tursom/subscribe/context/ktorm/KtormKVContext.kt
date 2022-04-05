package cn.tursom.subscribe.context.ktorm

import cn.tursom.database.ktorm.*
import cn.tursom.database.ktorm.annotations.KtormTableName
import cn.tursom.subscribe.context.KVContext
import org.ktorm.database.Database
import org.ktorm.dsl.where
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

class KtormKVContext(
  private val database: Database,
) : KVContext {
  @KtormTableName("kv")
  data class KV(
    var key: String,
    var value: String?,
  )

  fun createTable() {
    database.useConnection {
      it.createStatement().use { statement ->
        statement.execute(
          "create table if not exists kv (" +
            "key text primary key not null," +
            "value text" +
            ")"
        )
      }
    }
  }

  override fun get(key: String) = database.from<KV>()
    .select()
    .where {
      KV::key eq key
    }
    .getOne<KV>()?.value

  override fun set(key: String, value: String?) {
    if (database.update<KV> {
        set(KV::value, value)
        where {
          KV::key eq key
        }
      } != 0) {
      return
    }
    database.insert(KV(key, value))
  }
}