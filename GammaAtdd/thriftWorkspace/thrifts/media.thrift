namespace java com.funshion.gamma.atdd.thrift

struct MediaV2 {
    1: i32 mediaId,
    2: string nameCN,
    3: string nameEN,
    4: string nameOT,
    5: string displayType,
    6: string language,
    7: string mediaLength,
    8: string country,
    9: string releaseDate,
    10: string releaseInfo,
    11: string createDate,
    12: string modifyDate,
	13: string website,
	14: string pinyinCN,
	15: string karma,
	16: i32 cutPic,
	17: string fspStatus,
	18: string clarity,
	19: string adWord,
	20: i32 coverPic,
	21: i16 programType,
	22: string webClarity,
	23: bool isplay,
	24: list<i32> relateVideos,
	25: list<i32> mediaClasses,
	26: string mediaTactics,
	27: bool deleted,
	28: string tag4editor,
	29: i32 voteNumber,
	30: i64 playAfterNumber,
	31: string issue,
	32: i16 copyright,
	33: string TVstation,
	34: string fspLangStatus,
	35: string fspOriginalStatus
	36: string fspInfo
}

//字段组。目前只定义了简单的几种。后续会根据客户需求再做调整
enum FieldGroupType {
	base,	//媒体基础字段(mediaId, nameCN, nameEN, nameOT, displayType, language, mediaLength, country, releaseDate, releaseInfo, createDate, modifyDate, website, pinyinCN)
	name,	//媒体id和媒体名（nameCN、nameEN、nameOT）
	nameTime,	//媒体id和媒体名（nameCN、nameEN、nameOT）和各种时间（releaseDate、createDate、modifyDate）
	all			//全部媒体字段
}

struct MediaListV2 {
    1: i32 retCode,            // 状态码（200 | 400）
    2: string retMsg,        // 响应信息，用于错误调试
    3: i32 sum,            // 结果集总数
    4: list<MediaV2> pageRet,     // 分页结果集数据
}

struct MediaWithClient {
	1: i32 mediaId,
	2: string clientType,
	3: i32 mediaLastNum,
	4: string mediaLastReadableNum,
	5: string mediaLastUpdateTime
}

struct MediaWithClientList {
    1: i32 retCode,            // 状态码（200 | 400）
    2: string retMsg,        // 响应信息，用于错误调试
    3: list<MediaWithClient> ret,     // 结果集数据
}

service MediaServiceV2 {
	/*    根据媒体id数组获取单个媒体数据
        mediaId    媒体id
        fieldGroupType	期望获取的媒体字段组
    */
	MediaV2 getMediaById(1: i32 mediaId, 2: FieldGroupType type),
	
	/*    根据媒体id数组获取批量媒体数据
        mediaIds    媒体id数组
        fieldGroupType	期望获取的媒体字段组
    */
	list<MediaV2> getMediaListByIds(1: list<i32> mediaIds, 2: FieldGroupType type),
	
	/*    根据媒体类型获取批量媒体数据
        displayType    媒体类型
        fieldGroupType	期望获取的媒体字段组
        pageSize    分页数量（由调用方指定，当为非法参数时，该参数设定为10;该参数有最大值限制，最大值可配置）
        pageIndex    页码（由调用方指定，当为非法参数时,该参数设定为1）
    */
	MediaListV2 getMediaListByType(1: string displayType, 2: FieldGroupType type, 3: i32 pageSize, 4: i32 pageIndex),
	
	/*    根据媒体更新时间获取批量媒体数据
        modifyDate    更新时间，格式为 2000-1-1 00:00:00
        fieldGroupType	期望获取的媒体字段组
        pageSize    分页数量（由调用方指定，当为非法参数时，该参数设定为10;该参数有最大值限制，最大值可配置）
        pageIndex    页码（由调用方指定，当为非法参数时,该参数设定为1）
    */
	MediaListV2 getMediaListByDate(1: string modifyDate, 2: FieldGroupType type, 3: i32 pageSize, 4: i32 pageIndex),
	
	/*    按更新时间以指定顺序获取从指定时间点开始有更新的剧集状态列表
        modifyDate    更新时间，格式为 2000-1-1 00:00:00（GMT +8）
        limit    期望获得的结果集大小，最大限制为100
        order    排序规则，按更新时间acs | desc（默认asc）
    */
	MediaWithClientList getUpdatedMediasOrderByModifyTime(1: string modifyDate, 2: i32 limit, 3: string order),
	
	/*    获取指定媒体在指定终端的剧集状态
        mediaIds    媒体id数组
        clientType    客户端类型
    */
	MediaWithClientList getMediasStateOnClient(1: list<i32> mediaIds, 2: string clientType),

	/*    获取指定媒体在各终端的剧集状态
        mediaIds    媒体id数组
    */
	MediaWithClientList getMediasState(1: list<i32> mediaIds),
}