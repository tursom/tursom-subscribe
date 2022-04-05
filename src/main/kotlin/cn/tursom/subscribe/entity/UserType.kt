package cn.tursom.subscribe.entity

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.TypeAdapterFactory
import cn.tursom.database.ktorm.simpTableField
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

enum class UserType(
  val data: Int,
) {
  BILIBILI(1);

  companion object : TypeAdapter<UserType>, SqlType<UserType>(Types.TINYINT, "tinyint") {
    init {
      TypeAdapterFactory.registerAdapter(this)
    }

    fun valueOf(data: Int) = when (data) {
      1 -> BILIBILI
      else -> null
    }

    override val level: Int get() = 1

    override fun register(table: BaseTable<Any>, field: KProperty1<Any, UserType>): Column<UserType>? =
      if (field.returnType.jvmErasure != UserType::class) {
        null
      } else {
        table.registerColumn(field.simpTableField, this)
      }

    override fun doGetResult(rs: ResultSet, index: Int): UserType? = valueOf(rs.getInt(index))

    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: UserType) {
      ps.setInt(index, parameter.data)
    }
  }
}