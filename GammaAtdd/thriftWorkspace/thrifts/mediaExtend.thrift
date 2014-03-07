namespace java com.funshion.gamma.thrift.mediaExtend

struct Plots {
	1: i32 mediaId,
	2: string plots,
}
struct Behind {
	1: i32 mediaId,
	2: string behind,
}

service MediaExtendService {
	/*    根据媒体ID数组批量获取plots数据
        mediaIds    媒体ID数组
    */
	list<Plots> getPlotsByMediaIds(1: list<i32> mediaIds),
	/*    根据媒体ID数组批量获取behind数据
        mediaIds    媒体ID数组
    */
	list<Behind> getBehindByMediaIds(1: list<i32> mediaIds),
}