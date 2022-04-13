package cn.tursom.subscribe.context.bilibili.entity

data class Display(
  val biz_info: BizInfo = BizInfo(),
  val comment_info: CommentInfo = CommentInfo(),
  val cover_play_icon_url: String = "",
  val emoji_info: EmojiInfo = EmojiInfo(),
  val live_info: LiveInfo = LiveInfo(),
  val relation: Relation = Relation(),
  val show_tip: ShowTip = ShowTip(),
  val tags: List<Tag> = emptyList(),
  val topic_info: TopicInfo = TopicInfo(),
  val usr_action_txt: String = "",
) {
  data class BizInfo(
    val archive: Archive = Archive(),
  ) {
    data class Archive(
      val season_info: SeasonInfo = SeasonInfo(),
    )

    data class SeasonInfo(
      val color: String = "",
      val font: String = "",
      val season_id: Int = 0,
      val text: String = "",
    )
  }

  data class CommentInfo(
    val comment_ids: String = "",
    val comments: List<Comment> = emptyList(),
    val emojis: List<Emoji> = emptyList(),
  ) {
    data class Comment(
      val content: String = "",
      val name: String = "",
      val uid: Int = 0,
    )

    data class Emoji(
      val emoji_name: String = "",
      val meta: Meta = Meta(),
      val url: String = "",
    ) {
      data class Meta(
        val size: Int = 0,
      )
    }
  }

  data class EmojiInfo(
    val emoji_details: List<EmojiDetail> = emptyList(),
  ) {
    data class EmojiDetail(
      val attr: Int = 0,
      val emoji_name: String = "",
      val id: Int = 0,
      val meta: MetaX = MetaX(),
      val mtime: Int = 0,
      val package_id: Int = 0,
      val state: Int = 0,
      val text: String = "",
      val type: Int = 0,
      val url: String = "",
    ) {
      data class MetaX(
        val size: Int = 0,
      )
    }
  }

  data class LiveInfo(
    val live_status: Int = 0,
    val live_url: String = "",
  )

  data class Relation(
    val is_follow: Int = 0,
    val is_followed: Int = 0,
    val status: Int = 0,
  )

  data class ShowTip(
    val del_tip: String = "",
  )

  data class Tag(
    val icon: String = "",
    val link: String = "",
    val sub_module: String = "",
    val sub_type: Int = 0,
    val tag_type: Int = 0,
    val text: String = "",
  )

  data class TopicInfo(
    val new_topic: NewTopic = NewTopic(),
    val topic_details: List<TopicDetail> = emptyList(),
  ) {
    data class NewTopic(
      val id: Int = 0,
      val link: String = "",
      val name: String = "",
    )

    data class TopicDetail(
      val is_activity: Int = 0,
      val topic_id: Int = 0,
      val topic_link: String = "",
      val topic_name: String = "",
    )
  }
}
