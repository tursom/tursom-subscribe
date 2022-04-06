package cn.tursom.subscribe.util

import java.sql.Connection
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.sql.DataSource

class LockedDataSource(
  private val dataSource: DataSource,
  private val lock: Lock = ReentrantLock(),
) : DataSource by dataSource {
  private class LockedConnection(
    private val connection: Connection,
    private val lock: Lock,
  ) : Connection by connection {
    override fun close() {
      lock.unlock()
    }
  }

  override fun getConnection(): Connection {
    lock.lock()
    return LockedConnection(dataSource.connection, lock)
  }

  override fun getConnection(username: String?, password: String?): Connection {
    lock.lock()
    return LockedConnection(dataSource.getConnection(username, password), lock)
  }
}