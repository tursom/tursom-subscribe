package cn.tursom.subscribe.context.bilibili.entity

data class Card(
  val activity_infos: ActivityInfos = ActivityInfos(),
  val card: String = "",
  val desc: CardDesc = CardDesc(),
  val display: Display = Display(),
  val extend_json: String = "",
) {
  data class ActivityInfos(
    val details: List<Detail> = emptyList(),
  ) {
    data class Detail(
      val detail: String = "",
      val type: Int = 0,
    )
  }

  data class CardDesc(
    val acl: Int = 0,
    val bvid: String = "",
    val dynamic_id: Long = 0,
    val dynamic_id_str: String = "",
    val inner_id: Int = 0,
    val is_liked: Int = 0,
    val like: Int = 0,
    val orig_dy_id: Long = 0,
    val orig_dy_id_str: String = "",
    val orig_type: Int = 0,
    val origin: Origin = Origin(),
    val pre_dy_id: Int = 0,
    val pre_dy_id_str: String = "",
    val r_type: Int = 0,
    val repost: Int = 0,
    val rid: Int = 0,
    val rid_str: String = "",
    val status: Int = 0,
    val stype: Int = 0,
    val timestamp: Int = 0,
    val type: Int = 0,
    val uid: Int = 0,
    val uid_type: Int = 0,
    val user_profile: UserProfile = UserProfile(),
    val view: Int = 0,
  ) {
    data class Origin(
      val acl: Int = 0,
      val bvid: String = "",
      val dynamic_id: Long = 0,
      val dynamic_id_str: String = "",
      val inner_id: Int = 0,
      val like: Int = 0,
      val orig_dy_id: Int = 0,
      val orig_dy_id_str: String = "",
      val pre_dy_id: Int = 0,
      val pre_dy_id_str: String = "",
      val r_type: Int = 0,
      val repost: Int = 0,
      val rid: Int = 0,
      val rid_str: String = "",
      val status: Int = 0,
      val stype: Int = 0,
      val timestamp: Int = 0,
      val type: Int = 0,
      val uid: Int = 0,
      val uid_type: Int = 0,
      val view: Int = 0,
    )

    data class UserProfile(
      val card: Card = Card(),
      val decorate_card: DecorateCard = DecorateCard(),
      val info: Info = Info(),
      val level_info: LevelInfo = LevelInfo(),
      val pendant: Pendant = Pendant(),
      val rank: String = "",
      val sign: String = "",
      val vip: Vip = Vip(),
    ) {
      data class Card(
        val official_verify: OfficialVerify = OfficialVerify(),
      )

      data class DecorateCard(
        val big_card_url: String = "",
        val card_type: Int = 0,
        val card_type_name: String = "",
        val card_url: String = "",
        val expire_time: Int = 0,
        val fan: Fan = Fan(),
        val id: Int = 0,
        val image_enhance: String = "",
        val item_id: Int = 0,
        val item_type: Int = 0,
        val jump_url: String = "",
        val mid: Int = 0,
        val name: String = "",
        val uid: Int = 0,
      ) {
        data class Fan(
          val color: String = "",
          val is_fan: Int = 0,
          val num_desc: String = "",
          val number: Int = 0,
        )
      }

      data class Info(
        val face: String = "",
        val face_nft: Int = 0,
        val uid: Int = 0,
        val uname: String = "",
      )

      data class LevelInfo(
        val current_level: Int = 0,
      )

      data class Pendant(
        val expire: Int = 0,
        val image: String = "",
        val image_enhance: String = "",
        val image_enhance_frame: String = "",
        val name: String = "",
        val pid: Int = 0,
      )
    }
  }
}