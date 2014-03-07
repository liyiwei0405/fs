//////////////////////////////////////////////////////////////////////////////////////////
// Author: ����
// Copyright 2005-, Funshion Online Technologies Ltd. All Rights Reserved
// ��Ȩ 2005-�������������߼������޹�˾ ���а�Ȩ����
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
// the contents of this file may not be disclosed to third parties, copied or
// duplicated in any form, in whole or in part, without the prior written
// permission of Funshion Online Technologies Ltd.
// ���Ǳ����������߼������޹�˾δ������˽��Դ���롣���ļ����������δ���������߼�����
// �޹�˾��������ͬ�⣬���������κε�����͸¶��й�ܲ��ֻ�ȫ��; Ҳ�������κ���ʽ��˽�Ա���.
//////////////////////////////////////////////////////////////////////////////////////////

namespace java com.funshion.gamma.atdd.thrift

include "./common.thrift"

/**
 * retCode˵��:����״̬��Ļ����Ͽ���չҵ����ص�˽��״̬���i32ֵ 
 * ����retCodeֵ����˵�������ڱ�����Ӧ�ӿڶ��崦��ʱ����
 */

/**
 * ���ķ�����ص����ݽṹ
 */
struct MediaSubscription {
    1: i32 mediaId,    // ����ý��ID
    2: i32 subscribeTime, //����ʱ��
    3: i32 updateTime,   //ý�����ʱ��
    4: i32 sortTime,    //max(����ʱ��,ý�����ʱ��)
    5: string lastClientType, // �ն�����
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
 * ����������ص����ݽṹ
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
	1:CommentType type, // Ŀ������
	2:i32 targetId,     // Ŀ��ID
}

//���ݹ��˵�Ӱ�����ض������ݽṹ
struct ContentFilterAffectFactor {
	1:i32 whetherEnableFilter, //�Ƿ���Ҫ���ù���
	2:i32 auditSwitchStatus,   //��˿��ص�״̬
}

struct Comment {
	1:	i32 commentId,	// ����ID����redis��������������
	2:	i32 userId, 	// �¼��������û�ID
	3:	string content, // �¼�����
	4:	i32 score, 		// ���۷�����2, 4, 6, 8, 10��
	5:	i32 createdate, // �¼�����ʱ��
	6:	string clientType, // �ͻ�������

	7:	CommentType type, // ��������
	8:	i32 targetId,      // ����Ŀ��ID

	9:	common.QualityType quality, // ��������
	10:	i32 privacy, //��˽����
	11:	i32 isCheck, //���״̬

	12: i32 viewnum,    //�����
	13: i32 replynum,   //�ظ���
	14: i32 forwardnum, //ת����
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
    /*============================================�� �� �� �� �� �� �� ��====================================*/
    /*======================================================================================================*/

       /**
        * �û����ĵ���ý��
        *
        * @param i32 userId �û�ID
        * @param i32 mediaId      ���Ķ���ID
        * @param string clientType   �ն�����
        *
        * @return ResultStatus
        *             �ɹ�ʱ:{retCode:200}
        *             ʧ��ʱ:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
        *                        ERROR_CODE: 400: ��������
        *                                    500: ����������
        *                                    ......
        */
       common.ResultStatus subscribeMedia(1: i32 userId, 2: i32 mediaId, 3: string clientType),
      
       /**
        * �û�ȡ�����ĵ���ý��
        *
        * @param i32 userId �û�ID
        * @param i32 mediaId      ���Ķ���ID
        *
        * @return ResultStatus
        *             �ɹ�ʱ:{retCode:200}
        *             ʧ��ʱ:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
        *                        ERROR_CODE: 400: ��������
        *                                    404: �����Ѷ��ĵĶ���
        *                                    500: ����������
        *                                    ......
        */
       common.ResultStatus cancelSubscribeMedia(1: i32 userId, 2: i32 mediaId),

       /**
        * �û�����ȡ������ý��
        *
        * @param i32 userId �û�ID
        * @param list<i32> mediaList ���Ķ���ID�б�
        *
        * @return ResultStatus
        *             �ɹ�ʱ:{retCode:200}
        *             ʧ��ʱ:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
        *                        ERROR_CODE: 400: ��������
        *                                    500: ����������
        *                                    ......
        */
       common.ResultStatus cancelSubscribeMedias(1: i32 userId, 2: list<i32> mediaList),
      
       /**
        * �û��Ƿ��Ѷ���ĳ��ý��
        *
        * @param i32 userId �û�ID
        * @param i32 mediaId  ����ID
        *
        * @return ResultStatus 
        */
       common.ResultStatus isMediaSubscribed(1: i32 userId, 2: i32 mediaId),

       /**
        * ��������
        * @note ����ͻ���ʱ���ں���Χ�ڣ���ʹ�ÿͻ��˴�����ʱ�䣬����ʹ�÷�������ʱ��
        *
        * @param i32 userId �û�ID
        * @param list<MediaToSubscribe> MediaListToSubscribe      ����ý��Id�б�
        * @param string clientType   �ն�����
        *
        *@return ResultStatus
        */
       common.ResultStatus subscribeMedias(1: i32 userId, 2: list<MediaToSubscribe> MediaListToSubscribe, 3: string clientType),
	   
       /**
        * ��������(ͬ����ʽ)
        * @note ����ͻ���ʱ���ں���Χ�ڣ���ʹ�ÿͻ��˴�����ʱ�䣬����ʹ�÷�������ʱ��
        *
        * @param i32 userId �û�ID
        * @param list<MediaToSubscribe> MediaListToSubscribe      ����ý��Id�б�
        * @param string clientType   �ն�����
        *
        *@return ResultStatus
        */
       common.ResultStatus subscribeMediasSync(1: i32 userId, 2: list<MediaToSubscribe> MediaListToSubscribe, 3: string clientType),
       /**
        * ��sortTime�����ȡָ���û��Ķ����б�(sortTime = max(����ʱ��, ý����ָ���ն˵ĸ���ʱ��))
        *
        * @param i32 userId �û�ID
		* @param string clientType   �ն�����
        * @param PageParam pageParam ��ҳ����
        *
        * @return ResultMediaSubscriptionList
        */
       ResultMediaSubscriptionList getMediaSubscriptionsOrderBySortTime(1: i32 userId, 2: string clientType , 3:common.PageParam pageParam),

       /**
        * ��ȡָ���û����и��£��û�δ���ʵģ��Ķ��Ķ����б�(������ý��ĸ���ʱ������)
        *
        * @param i32 userId �û�ID
        * @param string clientType   �ն�����
        *
        * @return ResultListI32 mediaId�б�
        */
       common.ResultListI32 getUpdatedSubscribeMedias(1: i32 userId, 2: string clientType),
      
       /**
        * ��ȡ�û����и��µĶ��Ķ����б������Щ����Ϊ�Ѷ�
        *
        * @param i32 userId �û�ID
        * @param string clientType   �ն�����
        *
        * @return ResultListI32 mediaId�б�
        */
       common.ResultListI32 getAndMarkReadUpdatedUserSubscribeMedias(1: i32 userId, 2: string clientType),
       
        /**
        * TODO:������ʱ�������ȡָ���û��Ķ����б�(��վ���󣬿��Ȳ��ṩ)
        *
        * @param i32 userId �û�ID
        * @param PageParam pageParam ��ҳ����
        *
        * @return ResultMediaSubscriptionList
        */
       ResultMediaSubscriptionList getMediaSubscriptionsOrderBySubscribeTime(1: i32 userId, 2: common.PageParam pageParam),
      
       /**
        * TODO:������ý��ĸ���ʱ�������ȡָ���û��Ķ����б�(��վ���󣬿��Ȳ��ṩ)
        *
        * @param i32 userId �û�ID
        * @param PageParam pageParam ��ҳ����
        *
        * @return ResultMediaSubscriptionList
        */
       ResultMediaSubscriptionList getMediaSubscriptionsOrderByMediaUpdateTime(1: i32 userId, 2: common.PageParam pageParam ),
       
       /**
        * TODO:������ʱ�������ȡĳý��Ķ����û��б�
        *
        * @param i32 mediaId     ���Ķ���ID
        *
        * @return �û�ID�б�
        */   
       common.ResultListI32 getSubscribedUsersByMediaIdOrderBySubscribeTime(1: i32 mediaId, 2: common.PageParam pageParam),
       /**
        * ��ָ���û����ĵ�ý����ĳ�ն˵ĸ��±��Ϊ�Ѷ�
        *
        * @param i32 userId �û�ID
        * @param i32 mediaId ý��ID
        * @param string clientType   �ն�����
        *
        * @return ResultStatus
        */
       common.ResultStatus markReadUpdatedUserSubscribeMedia(1: i32 userId, 2: i32 mediaId, 3: string clientType),
       
       /**
        * TODO:��ȡ�û���������
        * @param i32 userid �û�ID
        *
        * @return ResultI32
        */
       common.ResultI32 getSubscribeCountByUserid(1: i32 userId),

       /**
        * ��ȡý�嶩����
        * @param i32 mediaId ý��ID
        * @return ResultI32 �������
        */
       common.ResultI32 getSubscribeCountByMediaId(1: i32 mediaId),
    /*======================================================================================================*/
    /*============================================���� �� �� �� �� �� ��====================================*/
    /*======================================================================================================*/
       /**
	   	 * �û���Ŀ����
	   	 *
	   	 * @param AddActionInfo actionInfo ����ͨ����Ϣ
	   	 * @param CommentTarget target     ���Ŀ��
	   	 * @param i32 score                ���� 0��2��4��6��8��10
	   	 *
	   	 * @return ResultI32 
	   	 *					RetCode: 200:�ɹ����     400���������Ϸ�    500���������ݷ���������
	   	 */
	   	common.ResultI32 addScore(1: common.AddActionInfo actionInfo, 2: CommentTarget target, 3: i32 score),
	
	   	/** 
	   	 * �õ��û���ĳ���������
	   	 * 
	   	 * @param i32 userId �û�ID
	   	 * @param CommentTarget target ����Ŀ��
	   	 *
	   	 * @return ResultI32 result����ֵΪ0��2��4��6��8��10��0��ʾδ���
	   	 *                   RetCode: 200:�ɹ���ȡ����     400���������Ϸ�    500���������ݷ���������   404��δ��ѯ����Ӧ��ּ�¼
	   	 */
	   	common.ResultI32 getScore(1: i32 userId, 2: CommentTarget target),

	/*======================================================================================================*/
    /*============================================�� �� �� �� �� �� �� ��====================================*/
    /*======================================================================================================*/
	   	/**
		 * ��ӵ���
		 * �ڲ�ʵ�ֲ����첽����ʽ
		 * 
		 *
		 * @param AddActionInfo actionInfo  ����ͨ����Ϣ
		 * @param CommentTarget target 		����Ŀ��
		 * @param string content 			�������ݣ��������,@�������ѱ�����Ϊ���ӡ�ͼƬ
		 * @param i32 score 				ý������
		 * @param ContentFilterAffectFactor filterAffectFactor �������ݹ��˵�Ӱ�����ض���
		 *
		 * @return ResultComment ����ӵĵ�����Ϣ (OK:200,duplicate comment:400) //����400��������������ظ������������ͣ����ǲ�������һ�����������ж����ʻ������ظ�����
		 */
		ResultComment addComment(1:common.AddActionInfo actionInfo, 2: CommentTarget target, 3:string content, 4:i32 score, 5:ContentFilterAffectFactor filterAffectFactor),
		
		
		/**
		 * ��ids�õ���������
		 *
		 * @param list<i32> ����Id�б�
		 * @param list<string> fields
		 * @return ���������б�
		 */	
		ResultCommentList getCommentByIds(1: list<i32> commentIds, 2: list<string> fields),
		
		//����--΢��Ƶ��UGC��ý��

		/**
		 * ����û��Ե���������������б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param i32 userId �û�id
		 * @param PageParam pageParam ��ҳ����
		 *
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getCommentsOfTargetByUserId(1: CommentTarget target,2: i32 userId, 3:common.PageParam pageParam),

		/**
		 * ��õ����������е����б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param PageParam pageParam ��ҳ����
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),
		
		/**
		 * ��õ����������е����б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param PageParam pageParam ��ҳ����
		 * @param i32 userId
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getCommentsOfTargetAuditOrSelf(1: CommentTarget target, 2:common.PageParam pageParam, 3:i32 userId),
		
		/**
		 * ���ĳ�û����е����б�
		 * 
		 * @param i32 userId �û�id
		 * @param PageParam pageParam ��ҳ����
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getCommentsByUserId(1: i32 userId, 2:common.PageParam pageParam),
	
		/**
		 * ��õ�����������б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param PageParam pageParam ��ҳ����
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getGoodCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),
	
		/**
		 * ��õ�����������б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param PageParam pageParam ��ҳ����
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getBadCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),
	
		/**
		 * ��õ������󾫻������б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param PageParam pageParam ��ҳ����
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getWonderfulCommentsOfTarget(1: CommentTarget target, 2:common.PageParam pageParam),

		/**
		 * ��õ��������ö������б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getTopCommentsOfTarget(1: CommentTarget target),

		/**
		 * ��õ�������ı༭�Ƽ������б�
		 * 
		 * @param CommentTarget target ��������Ŀ�� 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getRecommendCommentsOfTarget(1: CommentTarget target),
		
		/**
		 * ��õ��������ҵĹ�ע�ߵ����б�
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param i32 userId �û�id
		 * @param PageParam pageParam ��ҳ����
		 * 
		 * @return ResultCommentList �����б�
		 */
		ResultCommentList getFollowComment(1: CommentTarget target, 2:i32 userId, 3:common.PageParam pageParam),
		
		/**
		 * ��ȡ����ͳ����
		 * 
		 * @param CommentTarget target ��������Ŀ��
		 * @param i32  counterFlag ������������
		 *             �����������Ͳ��ö�����λ����ʾ����ʱֻ������������λ,������ʾ��ȫ�����������������������͡�ȫ�������;���������
		 *                                  ��λΪ1��ʾ��ȫ����������λΪ1��ʾ�Ǿ����������ߵ�λȫΪ1��ʾȫ�������;�������
		 *                                  ����ʮ������--1:ȫ������ ��2������������ 3��ȫ�������;�������
		 * 
		 * @return ResultCommentList 
		 */
		 
		ResultCommentCountList getCommentCount(1: CommentTarget target, 2:i32 counterFlag),
		
		/**
		 * ���õ�������
		 * 
		 * @param i32 commentId ����Id
		 * @param common.QualityType qualityType ������������
		 * 
		 * @return common.ResultStatus
		 *            �ɹ�ʱ:{retCode:200}
                 *             ʧ��ʱ:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
                 *                        ERROR_CODE: 400: ��������
                 *                                    500: ����������
		 */
		common.ResultStatus setCommentQuality(1:i32 commentId, 2: common.QualityType qualityType),

		/**
		 * ��ȡý������ͳ����Ϣ
		 *
		 * @param list<i32> mediaIds
		 *
		 * @return ResultMediaScoreStat
		 *		�ɹ�ʱ:{retCode:200, retMsg:'OK', result:map<mediaId, map<field, value>>}
		 *			field:='avg_score'|'score_count'
		 *		ʧ��ʱ:{retCode:ERROR_CODE, retMsg:ERROR_MESSAGE}
		 *			ERROR_CODE:
		 *				400: ��������
		 *				500: ����������
		 */
		ResultMediaScoreStat getMediaScoreStat(1: list<i32> mediaIds),

		/**
         * ��ȡ��¼�û�������������δ�������
         *
         * @param CommentTarget target ��������Ŀ��
         * @param userId ������������
         *
         * @return common.ResultI32
         *            �ɹ�ʱ:{retCode:200}
                 *             ʧ��ʱ:{retCode:ERROR_CODE,retMsg:ERROR_MESSAGE}
                 *                        ERROR_CODE: 400: ��������

         */
        common.ResultI32 getCommentCountWithUserUnpublished(1: CommentTarget target, 2: i32 userId),
        /**
         * ��ȡ����ͳ����V2
         *
         * @param CommentTarget target ��������Ŀ��
         * @param i32  counterFlag ������������
         *             �����������Ͳ���ʮ������--1:ȫ������ ��2����������

         * @return common.ResultI32
         */

        common.ResultI32 getCommentNum(1: CommentTarget target, 2:i32 counterFlag),
}
