package org.iflab.wc.app;

/**
 * Api存放类
 * 
 * @author: Timor(www.LogcatBlog.com)
 * @created: 2014-09-17
 */
public class WecenterApi {
	public static final String BASE = "http://w.hihwei.com/";
	public static final String LOGIN = BASE + "?/api/account/login_process/";
	public static final String REGISTER = BASE + "?/api/account/register_process/";
	public static final String IMAGE_BASE_URL = BASE + "uploads/topic/";
	public static final String POST_PIC = BASE + "?/api/publish/attach_upload/";
	public static final String POST_QUESTION = BASE+ "?/api/publish/publish_question/";
	public static final String USER_IMAGE_BASE = BASE + "uploads/avatar/";
	public static final String ARTICLE_VOTE=BASE+"?/article/ajax/article_vote/";
	public static final String  ARTICLE_COMMNET=BASE+"?/api/article/comment/?id=";
	public static final String  SAVE_COMMNET=BASE+"?/api/publish/save_comment/";
	public static final String  ARTICLE=BASE+"?/api/article/article/?id=";
	public static final String POST_ANSWER=BASE+"?/api/publish/save_answer/";
	public static final String ATTENTIONME=BASE+"api/my_fans_user.php";
	public static final String ATTENTIONUSER=BASE+"api/my_focus_user.php";
	public static final String EXPLORE=BASE+"?/api/explore_ios/";
	public static final String HOME=BASE+"?/api/home/";
	public static final String TOPIC_BEST=BASE+"?/api/topic/topic_best_answer/";
	public static final String TOPIC_DETAIL_F=BASE+"api/focus_topic.php";
	public static final String TOPIC= BASE+"api/topic.php";
	public static final String FOCUS_TOPIC=BASE+"api/my_focus_topic.php";
	public static final String HOT_TOPIC=BASE+"?/api/topic/square/";
	public static final String MY_ACTIVITY=BASE+"api/my_article.php";
	public static final String My_ASK=BASE+"api/my_question.php";
	public static final String PROFILE=BASE+"api/profile.php";
	public static final String AVATAR_UPLOAD=BASE+"?/api/account/avatar_upload/";
	public static final String  PROFILE_SETTING=BASE+"api/profile_setting.php";
	public static final String  USER_INFO=BASE+"?/api/account/get_userinfo/";
	public static final String CHANGE_FOLLOW=BASE+"?/follow/ajax/follow_people/";
	public static final String USER=BASE+"api/user.php?uid=";
	public static final String ANSWER_VOTE=BASE+"?/question/ajax/answer_vote/";
	public static final String  ANSWER_DETAIL=BASE+"?/api/question/answer_detail/?id=";
	public static final String QUESTION_DETAIL=BASE+"?/api/question/question/?id=";
	public static final String  FOLLOW_QUESTION=BASE+"?/question/ajax/focus/?question_id=";
	public static final String MY_ANSWER=BASE+"api/my_answer.php";
}