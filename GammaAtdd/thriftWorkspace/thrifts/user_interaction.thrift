//////////////////////////////////////////////////////////////////////////////////////////
// Author: 胡文
// Copyright 2005-, Funshion Online Technologies Ltd. All Rights Reserved
// 版权 2005-，北京风行在线技术有限公司 所有版权保护
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
// the contents of this file may not be disclosed to third parties, copied or
// duplicated in any form, in whole or in part, without the prior written
// permission of Funshion Online Technologies Ltd.
// 这是北京风行在线技术有限公司未公开的私有源代码。本文件及相关内容未经风行在线技术有
// 限公司事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份.
//////////////////////////////////////////////////////////////////////////////////////////

namespace java com.funshion.gamma.atdd.thrift

include "./common.thrift"

/**
 * retCode说明:公有状态码的基础上可扩展业务相关的私有状态码的i32值 
 * 具体retCode值及其说明可以在本文相应接口定义处及时更新
 */

/**
 * 订阅服务相关的数据结构
 */
struct MediaSubscription {
    1: i32 mediaId,    // 订阅媒体ID
    2: i32 subscribeTime, //订阅时间
    3: i32 updateTime,   //媒体更新时间
    4: i32 sortTime,    //max(订阅时间,媒体更新时间)
    5: string lastClientType, // 终端类型
}

struct ResultMediaSubscriptionList {
       1:i32 retCode,
       2:string retMsg,
       3:list<MediaSubscription> result,
}

struct MediaToSubscribe {
       1:i32 mediaId,
       2:i32 subscribeTime,
}

/**
 * 点评服务相关的数据结构
 */
enum CommentType {
	MEDIA_COMMENT = 1,
	STAFF_COMMENT = 2,
	VIDEO_COMMENT = 4,
	RANK_COMMENT  = 8,
	PIC_COMMENT   = 16,
	UGC_COMMENT   = 32,
}

struct CommentTarget {
	1:CommentType type, // 目标类型
	2:i32 targetId,     // 目标ID
}

//内容过滤的影响因素对象数据结构
struct ContentFilterAffectFactor {
	1:i32 whetherEnableFilter, //是否需要启用过滤
	2:i32 auditSwitchStatus,   //审核开关的状态
}

struct Comment {
	1:	i32 commentId,	// 点评ID，由redis计数器自增生成
	2:	i32 userId, 	// 事件发起者用户ID
	3:	string content, // 事件内容
	4:	i32 score, 		// 评价分数（2, 4, 6, 8, 10）
	5:	i32 createdate, // 事件创建时间
	6:	string clientType, // 客户端类型

	7:	CommentType type, // 点评类型
	8:	i32 targetId,      // 点评目标ID

	9:	common.QualityType quality, // 点评质量
	10:	i32 privacy, //隐私设置
	11:	i32 isCheck, //审核状态

	12: i32 viewnum,    //浏览数
	13: i32 replynum,   //回复数
	14: i32 forwardnum, //转发数
}

struct ResultCommentCountList {
	1:i32 retCode,
	2:string retMsg,
	3:map<string, i32> result,
}

struct ResultComment {
	1:i32 retCode,
	2:string retMsg,
	3:Comment result,
}

struct ResultCommentList {
	1:i32 retCode,
	2:string retMsg,
	3:list<Comment> result,
}

struct ResultMediaScoreStat {
	1: i32 retCode,
	2: string retMsg,
	3: map<i32, map<string, string>> result,	// map<mediaId, map<field, value>>
}

service UserInteraction {
	
    /*======================================================================================================*/
    /*============================================订 阅 服 务 接 口 定 义====================================*/
    /*======================================================================================================*/

       /**
        * 用户订阅单个媒体
        *
        * @param i32 userId 用户ID
        * @param i32 mediaId      订阅对象ID
        * @param string clientType   终端类型
        *
        * @return ResultStatus
        *             成功时:{retCode:200}
        *             失败时:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
        *                        ERROR_CODE: 400: 参数错误
        *                                    500: 服务器错误
        *                                    ......
        */
       common.ResultStatus subscribeMedia(1: i32 userId, 2: i32 mediaId, 3: string clientType),
      
       /**
        * 用户取消订阅单个媒体
        *
        * @param i32 userId 用户ID
        * @param i32 mediaId      订阅对象ID
        *
        * @return ResultStatus
        *             成功时:{retCode:200}
        *             失败时:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
        *                        ERROR_CODE: 400: 参数错误
        *                                    404: 不是已订阅的对象
        *                                    500: 服务器错误
        *                                    ......
        */
       common.ResultStatus cancelSubscribeMedia(1: i32 userId, 2: i32 mediaId),

       /**
        * 用户批量取消订阅媒体
        *
        * @param i32 userId 用户ID
        * @param list<i32> mediaList 订阅对象ID列表
        *
        * @return ResultStatus
        *             成功时:{retCode:200}
        *             失败时:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
        *                        ERROR_CODE: 400: 参数错误
        *                                    500: 服务器错误
        *                                    ......
        */
       common.ResultStatus cancelSubscribeMedias(1: i32 userId, 2: list<i32> mediaList),
      
       /**
        * 用户是否已订阅某个媒体
        *
        * @param i32 userId 用户ID
        * @param i32 mediaId  对象ID
        *
        * @return ResultStatus 
        */
       common.ResultStatus isMediaSubscribed(1: i32 userId, 2: i32 mediaId),

       /**
        * 批量订阅
        * @note 如果客户端时间在合理范围内，则使用客户端传来的时间，否则使用服务器端时间
        *
        * @param i32 userId 用户ID
        * @param list<MediaToSubscribe> MediaListToSubscribe      订阅媒体Id列表
        * @param string clientType   终端类型
        *
        *@return ResultStatus
        */
       common.ResultStatus subscribeMedias(1: i32 userId, 2: list<MediaToSubscribe> MediaListToSubscribe, 3: string clientType),
	   
       /**
        * 批量订阅(同步方式)
        * @note 如果客户端时间在合理范围内，则使用客户端传来的时间，否则使用服务器端时间
        *
        * @param i32 userId 用户ID
        * @param list<MediaToSubscribe> MediaListToSubscribe      订阅媒体Id列表
        * @param string clientType   终端类型
        *
        *@return ResultStatus
        */
       common.ResultStatus subscribeMediasSync(1: i32 userId, 2: list<MediaToSubscribe> MediaListToSubscribe, 3: string clientType),
       /**
        * 按sortTime逆序获取指定用户的订阅列表(sortTime = max(订阅时间, 媒体在指定终端的更新时间))
        *
        * @param i32 userId 用户ID
		* @param string clientType   终端类型
        * @param PageParam pageParam 分页参数
        *
        * @return ResultMediaSubscriptionList
        */
       ResultMediaSubscriptionList getMediaSubscriptionsOrderBySortTime(1: i32 userId, 2: string clientType , 3:common.PageParam pageParam),

       /**
        * 获取指定用户的有更新（用户未访问的）的订阅对象列表(按订阅媒体的更新时间逆序)
        *
        * @param i32 userId 用户ID
        * @param string clientType   终端类型
        *
        * @return ResultListI32 mediaId列表
        */
       common.ResultListI32 getUpdatedSubscribeMedias(1: i32 userId, 2: string clientType),
      
       /**
        * 获取用户的有更新的订阅对象列表并标记这些更新为已读
        *
        * @param i32 userId 用户ID
        * @param string clientType   终端类型
        *
        * @return ResultListI32 mediaId列表
        */
       common.ResultListI32 getAndMarkReadUpdatedUserSubscribeMedias(1: i32 userId, 2: string clientType),
       
        /**
        * TODO:按订阅时间逆序获取指定用户的订阅列表(网站需求，可先不提供)
        *
        * @param i32 userId 用户ID
        * @param PageParam pageParam 分页参数
        *
        * @return ResultMediaSubscriptionList
        */
       ResultMediaSubscriptionList getMediaSubscriptionsOrderBySubscribeTime(1: i32 userId, 2: common.PageParam pageParam),
      
       /**
        * TODO:按订阅媒体的更新时间逆序获取指定用户的订阅列表(网站需求，可先不提供)
        *
        * @param i32 userId 用户ID
        * @param PageParam pageParam 分页参数
        *
        * @return ResultMediaSubscriptionList
        */
       ResultMediaSubscriptionList getMediaSubscriptionsOrderByMediaUpdateTime(1: i32 userId, 2: common.PageParam pageParam ),
       
       /**
        * TODO:按订阅时间正序获取某媒体的订阅用户列表
        *
        * @param i32 mediaId     订阅对象ID
        *
        * @return 用户ID列表
        */   
       common.ResultListI32 getSubscribedUsersByMediaIdOrderBySubscribeTime(1: i32 mediaId, 2: common.PageParam pageParam),
       /**
        * 将指定用户订阅的媒体在某终端的更新标记为已读
        *
        * @param i32 userId 用户ID
        * @param i32 mediaId 媒体ID
        * @param string clientType   终端类型
        *
        * @return ResultStatus
        */
       common.ResultStatus markReadUpdatedUserSubscribeMedia(1: i32 userId, 2: i32 mediaId, 3: string clientType),
       
       /**
        * TODO:获取用户订阅总数
        * @param i32 userid 用户ID
        *
        * @return ResultI32
        */
       common.ResultI32 getSubscribeCountByUserid(1: i32 userId),

       /**
        * 获取媒体订阅数
        * @param i32 mediaId 媒体ID
        * @return ResultI32 计数结果
        */
       common.ResultI32 getSubscribeCountByMediaId(1: i32 mediaId),
    /*======================================================================================================*/
    /*============================================评分 服 务 接 口 定 义====================================*/
    /*======================================================================================================*/
       /**
	   	 * 用户对目标打分
	   	 *
	   	 * @param AddActionInfo actionInfo 发布通用信息
	   	 * @param CommentTarget target     打分目标
	   	 * @param i32 score                分数 0、2、4、6、8、10
	   	 *
	   	 * @return ResultI32 
	   	 *					RetCode: 200:成功打分     400：参数不合法    500：访问数据服务器错误
	   	 */
	   	common.ResultI32 addScore(1: common.AddActionInfo actionInfo, 2: CommentTarget target, 3: i32 score),
	
	   	/** 
	   	 * 得到用户对某对象的评分
	   	 * 
	   	 * @param i32 userId 用户ID
	   	 * @param CommentTarget target 点评目标
	   	 *
	   	 * @return ResultI32 result可能值为0、2、4、6、8、10。0表示未打分
	   	 *                   RetCode: 200:成功获取评分     400：参数不合法    500：访问数据服务器错误   404：未查询到对应打分记录
	   	 */
	   	common.ResultI32 getScore(1: i32 userId, 2: CommentTarget target),

	/*======================================================================================================*/
    /*============================================点 评 服 务 接 口 定 义====================================*/
    /*======================================================================================================*/
	   	/**
		 * 添加点评
		 * 内部实现采用异步处理方式
		 * 
		 *
		 * @param AddActionInfo actionInfo  发布通用信息
		 * @param CommentTarget target 		点评目标
		 * @param string content 			点评内容（处理过）,@、表情已被解析为连接、图片
		 * @param i32 score 				媒体评分
		 * @param ContentFilterAffectFactor filterAffectFactor 点评内容过滤的影响因素对象
		 *
		 * @return ResultComment 被添加的点评信息 (OK:200,duplicate comment:400) //提醒400包含参数错误和重复点评两种类型，但是参数错误一般程序本身可以判定，故还包含重复点评
		 */
		ResultComment addComment(1:common.AddActionInfo actionInfo, 2: CommentTarget target, 3:string content, 4:i32 score, 5:ContentFilterAffectFactor filterAffectFactor),
		
		
		/**
		 * 按ids得到点评数据
		 *
		 * @param list<i32> 点评Id列表
		 * @param list<string> fields
		 * @return 点评数据列表
		 */	
		ResultCommentList getCommentByIds(1: list<i32> commentIds, 2: list<string> fields),
		
		//点评--微视频、UGC和媒体

		/**
		 * 获得用户对单个点评对象点评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param i32 userId 用户id
		 * @param PageParam pageParam 分页参数
		 *
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getCommentsOfTargetByUserId(1: CommentTarget target,2: i32 userId, 3:common.PageParam pageParam),

		/**
		 * 获得点评对象所有点评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param PageParam pageParam 分页参数
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),
		
		/**
		 * 获得点评对象所有点评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param PageParam pageParam 分页参数
		 * @param i32 userId
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getCommentsOfTargetAuditOrSelf(1: CommentTarget target, 2:common.PageParam pageParam, 3:i32 userId),
		
		/**
		 * 获得某用户所有点评列表
		 * 
		 * @param i32 userId 用户id
		 * @param PageParam pageParam 分页参数
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getCommentsByUserId(1: i32 userId, 2:common.PageParam pageParam),
	
		/**
		 * 获得点评对象好评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param PageParam pageParam 分页参数
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getGoodCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),
	
		/**
		 * 获得点评对象差评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param PageParam pageParam 分页参数
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getBadCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),
	
		/**
		 * 获得点评对象精华点评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param PageParam pageParam 分页参数
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getWonderfulCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),

		/**
		 * 获得点评对象置顶点评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getTopCommentsOfTarget(1: CommentTarget target),

		/**
		 * 获得点评对象的编辑推荐点评列表
		 * 
		 * @param CommentTarget target 被点评的目标 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getRecommendCommentsOfTarget(1: CommentTarget target),
		
		/**
		 * 获得点评对象我的关注者点评列表
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param i32 userId 用户id
		 * @param PageParam pageParam 分页参数
		 * 
		 * @return ResultCommentList 点评列表
		 */
		ResultCommentList getFollowComment(1: CommentTarget target, 2:i32 userId, 3:common.PageParam pageParam),
		
		/**
		 * 获取点评统计量
		 * 
		 * @param CommentTarget target 被点评的目标
		 * @param i32  counterFlag 点评质量类型
		 *             点评质量类型采用二进制位来表示：暂时只有两个二进制位,用来表示“全部点评”，“精华点评”和“全部点评和精华点评”
		 *                                  低位为1表示是全部点评，高位为1表示是精华点评，高低位全为1表示全部点评和精华点评
		 *                                  即：十进制下--1:全部点评 ，2：精华点评， 3：全部点评和精华点评
		 * 
		 * @return ResultCommentList 
		 */
		 
		ResultCommentCountList getCommentCount(1: CommentTarget target, 2:i32 counterFlag),
		
		/**
		 * 设置点评质量
		 * 
		 * @param i32 commentId 点评Id
		 * @param common.QualityType qualityType 点评质量类型
		 * 
		 * @return common.ResultStatus
		 *            成功时:{retCode:200}
                 *             失败时:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
                 *                        ERROR_CODE: 400: 参数错误
                 *                                    500: 服务器错误
		 */
		common.ResultStatus setCommentQuality(1:i32 commentId, 2: common.QualityType qualityType),

		/**
		 * 获取媒体评分统计信息
		 *
		 * @param list<i32> mediaIds
		 *
		 * @return ResultMediaScoreStat
		 *		成功时:{retCode:200, retMsg:'OK', result:map<mediaId, map<field, value>>}
		 *			field:='avg_score'|'score_count'
		 *		失败时:{retCode:ERROR_CODE, retMsg:ERROR_MESSAGE}
		 *			ERROR_CODE:
		 *				400: 参数错误
		 *				500: 服务器错误
		 */
		ResultMediaScoreStat getMediaScoreStat(1: list<i32> mediaIds),

		/**
         * 获取登录用户点评对象所有未审核数量
         *
         * @param CommentTarget target 被点评的目标
         * @param userId 点评质量类型
         *
         * @return common.ResultI32
         *            成功时:{retCode:200}
                 *             失败时:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
                 *                        ERROR_CODE: 400: 参数错误

         */
        common.ResultI32 getCommentCountWithUserUnpublished(1: CommentTarget target, 2: i32 userId),
        /**
         * 获取点评统计量V2
         *
         * @param CommentTarget target 被点评的目标
         * @param i32  counterFlag 点评质量类型
         *             点评质量类型采用十进制下--1:全部点评 ，2：精华点评

         * @return common.ResultI32
         */

        common.ResultI32 getCommentNum(1: CommentTarget target, 2:i32 counterFlag),
}
