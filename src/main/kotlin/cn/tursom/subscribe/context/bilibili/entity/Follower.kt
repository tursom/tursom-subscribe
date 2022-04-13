package cn.tursom.subscribe.context.bilibili.entity

import cn.tursom.core.toJson
import cn.tursom.subscribe.entity.Subscribe

data class Follower(
  val attribute: Int = 0,
  val contract_info: ContractInfo = ContractInfo(),
  val face: String = "",
  val face_nft: Int = 0,
  val mid: Int = 0,
  val mtime: Int = 0,
  val official_verify: OfficialVerify = OfficialVerify(),
  val sign: String = "",
  val special: Int = 0,
  val tag: String = "",
  val uname: String = "",
  val vip: Vip = Vip(),
) {
  fun toSubscribe(uid: String) = Subscribe(
    uid = uid,
    mid = mid.toString(),
    uname = uname,
    raw = toJson(),
  )

  data class ContractInfo(
    val is_contract: Boolean = false,
    val is_contractor: Boolean = false,
    val ts: Int = 0,
    val user_attr: Int = 0,
  )
}
