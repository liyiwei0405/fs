namespace java com.funshion.gamma.atdd.playInfo.thrift

// 播放信息格式
struct PlayInfoRec {
	1: i32 playInfoID,
	2: i32 mediaID,	
	3: string hashID,
	
	// 剧集的数字序号，比如“1”
	4: i32 serialNumber,
	
	// 电视剧集的中文序号，比如“第1集”；
	// 对于综艺节目，取值也可能类似“20120713（那英光脚献唱呛刘欢 萌女为逝父高歌）”
	5: string serialName,
	
	// 剧集缩略图ID
	6: i32 serialPicID,
	
	// 编辑修改后的任务中文名称，如“与青春有关的日子|第7-9集”
	// 对于综艺节目，取值也可能类似"中国好声音|20120713（那英光脚献唱呛刘欢 萌女为逝父高歌）"
	7: string taskName,
	
	// 视频清晰度，返回 shot, tv, dvd, hi-dvd, super-dvd中的某一个
	8: string clarity,
	
	// 语言类型，值为 空/chi/arm/und，含义分别为 无/国语/粤语/原声
	// 电视剧通过此字段区分多音轨
	9: string langType,
	
	// 音轨语言，值为空或字符串如“chi”（含义国语）
	// 电影通过音轨语言区分多音轨
	10: string soundTrackLang,
	
	// 任务的最近修改时间
	11: i32 modifyDate,
	
	// fsp地址，具体定义请参考redmine页面
	12: string fspURL,
	
	// fsp扩展字段，具体定义请参考redmine页面
	13: string fspExt
}

// 为了减少接口个数，不管是单个还是批量ID查询，均以list形式返回<播放信息>的结果集
/*
	点播信息服务内部错误
	retCode:500 
	retMsg:"map was null" 
	data: null
	
	参数错误，比如idlist为空，或者id个数超过特定限制
	retCode:400 
	retMsg:"idlist was null/empty" 
	data:null
	
	参数错误，id个数超过限制，默认值1000（可配置）
	retCode:413
	retMsg:"idlist size too large" 
	data:null
	
	通过mediaid查询未找到结果
	retCode:404 
	retMsg:"got no result for the id" 
	data:null

	未出现上面错误时，则返回200
	retCode:200 
	retMsg:OK 
	data: 结果数据（若某个id未查询到结果，则结果集里其相应位置PlayInfoRec的playInfoID为0）
*/
struct PlayInfoRs {
	1: i32 retCode,
	2: string retMsg,
	3: list<PlayInfoRec> data
}

service PlayInfoService {
	// 结果集按照ids的顺序排序；某id没有结果，则结果集的相应位置为playinfoid为0的空结果
	PlayInfoRs getInfoByPlayIDList(1: list<i32> playIDs),
	
	// 结果集按照ids的顺序排序；某id没有结果，则结果集的相应位置为playinfoid为0的空结果
	PlayInfoRs getInfoByHashIDList(1: list<string> hashIDs),
	
	PlayInfoRs getInfoByMediaID(1: i32 mediaID),
}