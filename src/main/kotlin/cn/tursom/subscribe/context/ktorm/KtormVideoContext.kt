package cn.tursom.subscribe.context.ktorm

import cn.tursom.core.context.Context
import cn.tursom.database.ktorm.*
import cn.tursom.subscribe.context.KVContext
import cn.tursom.subscribe.context.VideoContext
import cn.tursom.subscribe.entity.Video
import org.ktorm.database.Database
import org.ktorm.dsl.limit
import org.ktorm.dsl.where
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

class KtormVideoContext(
  private val database: Database,
  private val kvContext: KVContext,
) : VideoContext.Data {
  companion object {
    private const val dbVersionKey = "KtormVideoContext_dbVersion"
    private val tableSql = listOf(
      "create table if not exists video (" +
        "id integer PRIMARY KEY autoincrement," +
        "uid text not null," +
        "vid text not null," +
        "create_time integer not null," +
        "title text not null," +
        "cover text," +
        "aid integer," +
        "bvid text," +
        "raw text" +
        ")",
      "create index time_index on video (create_time)",
      "alter table video add type tinyint not null default 1",
      "create index if not exists uid on video (type, uid)",
      "create unique index if not exists vid on video (type, vid)",
    )
  }

  fun createTable() {
    database.createTable(
      kvContext[dbVersionKey]?.toInt() ?: 0,
      tableSql,
    )
    kvContext[dbVersionKey] = tableSql.size.toString()
  }

  override fun saveVideo(video: Video): Int = try {
    database.insert(video)
  } catch (e: SQLiteException) {
    when (e.resultCode) {
      SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE, SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY -> {}
      else -> throw e
    }
    0
  }

  override fun saveVideos(video: List<Video>) = video.sumOf(::saveVideo)

  override suspend fun listVideos(uid: String, page: Int, pageSize: Int, context: Context) =
    database.from<Video>()
      .select()
      .where {
        Video::uid eq uid
      }
      .limit((page - 1) * pageSize, pageSize)
      .toList<Video>()

  override suspend fun listVideos(uid: String, context: Context) =
    database.from<Video>()
      .select()
      .where {
        Video::uid eq uid
      }
      .toList<Video>()
}