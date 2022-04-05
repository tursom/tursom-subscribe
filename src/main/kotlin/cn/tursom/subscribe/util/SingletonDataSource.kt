package cn.tursom.subscribe.util

import java.sql.Connection
import javax.sql.DataSource

class SingletonDataSource(
  private val dataSource: DataSource,
) : DataSource by dataSource {
  private class SingletonConnection(
    private val connection: Connection,
  ) : Connection by connection {
    override fun close() {}
  }

  private var connection: Connection? = null

  override fun getConnection() = connection ?: synchronized(this) {
    if (connection != null) {
      connection!!
    } else {
      connection = SingletonConnection(dataSource.connection)
      connection!!
    }
  }

  override fun getConnection(username: String?, password: String?) = connection ?: synchronized(this) {
    if (connection != null) {
      connection!!
    } else {
      connection = SingletonConnection(dataSource.getConnection(username, password))
      connection!!
    }
  }
}