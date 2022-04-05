package cn.tursom.subscribe.context.ktorm

import cn.tursom.subscribe.context.DataContext
import org.ktorm.database.Database
import javax.sql.DataSource

class KtormDataContext(
  database: Database,
) : DataContext {
  constructor(dataSource: DataSource) : this(Database.connect(dataSource))

  override val kvContext = KtormKVContext(database)
  override val userContext = KtormUserContext(database, kvContext)
  override val subscribeContext = KtormSubscribeContext(database)
  override val videoContext = KtormVideoContext(database, kvContext)

  init {
    kvContext.createTable()
    userContext.createTable()
    subscribeContext.createTable()
    videoContext.createTable()
  }
}