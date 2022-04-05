package cn.tursom.subscribe.util

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.sql.DataSource

val DataSource.singleton
  get() = SingletonDataSource(this)

fun DataSource.locked(lock: Lock = ReentrantLock()) = LockedDataSource(this, lock)
