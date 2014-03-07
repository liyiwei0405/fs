namespace java com.funshion.gamma.atdd.tacticService.thrift
struct MediaIdList {
    1: i32 retCode,            	// 状态码（200 | 400）
    2: string retMsg,        	// 响应信息，用于错误调试
    3: list<i32> mediaIds,     	// 媒体id集
}

struct MediaTactic {
	1: i32 mediaid,
	2: bool hasTactic //有策略时为true，否则false
}

struct MediaTacticList {
    1: i32 retCode,            				// 状态码（200 | 400）
    2: string retMsg,        				// 响应信息，用于错误调试
    3: list<MediaTactic> mediaTactic,   	// 媒体id的策略标注结果集
}

service TacticService {
	/*    根据策略过滤掉不可播放的媒体id，返回可播放的媒体id集
        mediaIds        需要进行策略过滤的媒体id集，当媒体id集全都被过滤时，返回一个空的list
        ucs    			策略字段
    */
	MediaIdList getAvilableMedia(1: list<i32> mediaIds, 2: string ucs),
	
	/*    根据策略参数标注媒体的策略状态，返回媒体id的策略标注结果集
        mediaIds        需要进行策略过滤的媒体id集
        ucs    			策略字段
    */
	MediaTacticList getMediaTactic(1: list<i32> mediaIds, 2: string ucs)
}
