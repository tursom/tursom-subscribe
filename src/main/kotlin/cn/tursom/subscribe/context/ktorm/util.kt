package cn.tursom.subscribe.context.ktorm

import org.ktorm.database.Database

internal fun Database.createTable(dbVersion: Int, tableSql: List<String>) = useConnection {
  it.createStatement().use { statement ->
    tableSql.forEachIndexed { index, sql ->
      if (dbVersion <= index) {
        statement.execute(sql)
      }
    }
  }
}
