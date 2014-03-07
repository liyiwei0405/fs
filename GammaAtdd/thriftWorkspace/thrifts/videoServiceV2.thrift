namespace java com.funshion.videoService.thrift

//VideoletInfo中files字段
struct FileStruct{
	1:string hashId
	2:string fileName
	3:string fileFormat
	4:i32 bitRateKbps	//标识任务的码率，但并不是视频的真实码率，单位:kbps
	5:string clarity	//shot,tv,dvd,high-dvd,super-dvd
	6:i64 fileSizeByte
}

//基本信息用于列表页显示的基本字段
struct VideoletBaseInfo{
	1:i32 videoId    	//ID，由redis计数器自增生成
    2:string title      //视频标题
    3:string picturePath     //视频截图路径（最早的一部分视频值为http打头的字符串，后续新生成的均为图片ID号）
    4:i32 timeLen       //视频视频时长（单位秒）
    5:i32 ordering  	//排序值
    6:double score      //视频评分（来源于所有评分计算和）
    7:i32 scoreNum     	//评分总数
    8:i32 playNum 		//风行总观看数
    9:i32 playNumInit   //抓取的总观看数 + 风行总观看数
    10:string plots 	//描述
	11:i32 modifyDate	//记录更新时间
}

struct VideoletInfo{
	1:i32 videoId	//ID，由redis计数器自增生成
	2:i32 mapId		//视频映射ID，映射原来库表的videoid
	3:string vId	//标识（由macross生成并通过接口通知）
	4:string source	//视频来源：风行(fs), 土豆(tudou), 天脉(tvm)……
	5:string cls	//视频类型：normal（普通小视频）、ugc（UGC视频）、news（新闻视频
	6:string title	//视频标题
	7:string picturePath	//视频截图路径（最早的一部分视频值为http打头的字符串，后续新生成的均为图片ID号）
	8:i32 timeLen	//视频视频时长（单位秒）
	9:string plots	//视频描述
	
	10:list<string> types	//视频分类
	11:list<string> tags	//视频标签
	12:list<FileStruct> files  	//播放文件集合。[{"hashid":$hashid, "filename":$filename, "bitrate":$bitrate, "clarity":$clarity, "filesize":$filesize, "fileformat":$fileformat},……]	
	
	13:list<i32> relateVideoIds 	//相关视频ID集合
	14:list<i32> relateSpecialIds	//相关专辑ID集合
	15:list<i32> videoEventIds; //视频相关事件ID集合。如：关注、点评等
	
	16:double score	//视频评分（来源于所有评分计算和）
	17:i32 scoreNum	//评分总数
	18:i32 commentNum; //评论总数
	
	19:i32 dayNum	//昨日观看数
	20:i32 weekNum	//周观看数
	21:i32 monthNum	//月观看数
	22:i32 playNum	//风行总观看数
	
	23:double dayIndex	//昨日观看指数
	24:double weekIndex	//周观看指数
	25:double monthIndex	//月观看指数
	26:double playIndex	//总观看指数
	27:string videoIndexes	//视频指数参数，如："0,21,0,0"
	
	28:i32 showAd; //是否播放广告
	29:i32 votable; //是否可评论
	30:i32 ordering	//排序值
	31:string publishFlag;	//发布状态标识（published表示已发布，topublish表示待发布）
	32:i32 createDate	//创建时间
	33:i32 modifyDate	//记录更新时间
	//news
	34:i32 lastNum; //最近24小时观看数
	35:i32 lastIndex; //最近24小时观看指数
	36:i32 isVideo; //是否视频新闻
	37:i32 recommend; //是否推荐（1为 推荐，0,为 未推荐）
	38:i32 isRank; //是否进入榜单
	//ugc
	39:i32 playNumInit	//抓取的总观看数 + 风行总观看数
	40:map<string, i32> commentNumFunshion; //风行的点评数量统计：array('all_num'=>0)
	41:i32 transmitNum;	//抓取的转发量初始数值
	42:list<i32> relateMediaIds	//相关媒体
	43:list<i32> relateStaffIds	//相关明星
	44:i32 userId	//上传用户ID
	45:string userName	//上传用户名
	46:i32 moduleId; //关联到的题材的模块id
	47:string position; //关联到的题材模块中的具体位置。 题材焦点小图，题材焦点大图，模块焦点，模块，排行榜
	48:string picF; //ugc video 焦点图
	49:string picV; //ugc video 竖图
	50:i32 isBottomRecommend; //是否底推. 1表示底推，0表示不是底推
	51:i32 recommendDate; //ugc video 被推荐到题材垂直页面的时间，用于题材垂直页面排序和取值
	52:map<string, list<i32>> tacticArea; //微视频的地域策略: {"iphone":[1,2,3],"ipad":[3,4,12]}
	53:map<string, list<i32>> videoContentClasses; //微视频的内容策略
	//normal
	54:list<i32> tagIds	//节目标签ID字段 
	55:double hot	//微视频热度
	56:string copyright; //版权商
	57:i32 picTitleModifydate; //图片、标题更新时间
	58:string extend	//风行出品有关的扩展字段："extend" : {"0" : "set", "twang_name" : "新娱乐", "twang_name" : "新娱乐", "titlepath" : "/subject/48497/"} 其所包含的key不能确定，序列化为string
}

struct VideoBaseListResult{
	1:i32 retCode	//200: ok;300-499, error request; 500 - 599 inner error; others: not defined error
	2:string retMsg
	3:list<VideoletBaseInfo> videoBaseList
}

struct VideoListResult{
	1:i32 retCode	//200: ok;300-499, error request; 500 - 599 inner error; others: not defined error
	2:string retMsg
	3:list<VideoletInfo> videoList
}


struct RetrieveStruct{//maybe support multi QueryStruct search
        1:i32 ver //current version is 1, other value will get error 301
        2:string fsql; //sql style query
        3:list<string> paras //fsql's parameters, just replace the '?'s
}

/*搜索检索fsql查询语法解释：
示例：(title SEARCH '武汉' && TIMELEN = ?) &&(SCORE = '88' || PLAY_NUM = ?) not (TAG_IDS = ? || SOURCE = ?) order by id desc limit offset,length

参变量：
	支持'?'或字节拼接字符串，支持范围: 1.操作符右侧参数  2.limit的 offset、length
连接符： 
	and(&&), or(||), not(!)
操作符： 
	=, >, <, >=, <=, search, like 
	search/like 暂只支持title字段；in为保留关键字，暂不支持，也不允许使用
	
操作符右侧若为字符串，需用单引号或双引号, 数字可不加，引号不支持\' \"转义，对String类参数，建议采用parameter形式(如title = ?), 不区分大小写;
order by支持所有检索条件字段;
limit的offset和length需为整数，只传一个默认为length，offset 0;
true, false是保留关键字

条件字段范围：
	videoid, title, timelen, score, playnum, tagids, types, tags, createdate, modifydate, source
*/
	
//搜索或者检索结果, 查询VideoletBaseInfo
struct VideoletBaseRetrieveResult{
        1:i32 retCode //200: ok;300-499, error request; 500 - 599 inner error; others: not defined error
        2:string retMsg //Exception trace 
        3:i32 total //total matched result count
        4:double usedTime //used time, in ms
        5:list<VideoletBaseInfo> videoList //sub-result limit by LimitBy
}

//搜索或者检索结果, 查询VideoletInfo
struct VideoletRetrieveResult{
        1:i32 retCode //200: ok;300-499, error request; 500 - 599 inner error; others: not defined error
        2:string retMsg //Exception trace 
        3:i32 total //total matched result count
        4:double usedTime //used time, in ms
        5:list<VideoletInfo> videoList	 //sub-result limit by LimitBy
}

//retrive defines end

service VideoService{
	//批量获取小视频基本字段
	VideoBaseListResult getVideoBaseListByIds(1:list<i32> videoIdList)
	//批量获取小视频所有字段
	VideoListResult getVideoListByIds(1:list<i32> videoIdList)
	
	//搜索或检索VideoletBaseInfo
	VideoletBaseRetrieveResult retrieveVideoletBaseInfo(1:RetrieveStruct rs)
	//搜索或检索VideoletInfo
	VideoletRetrieveResult retrieveVideolet(1:RetrieveStruct rs)
}