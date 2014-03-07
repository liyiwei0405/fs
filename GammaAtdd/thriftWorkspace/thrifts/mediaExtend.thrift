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
	/*    ����ý��ID����������ȡplots����
        mediaIds    ý��ID����
    */
	list<Plots> getPlotsByMediaIds(1: list<i32> mediaIds),
	/*    ����ý��ID����������ȡbehind����
        mediaIds    ý��ID����
    */
	list<Behind> getBehindByMediaIds(1: list<i32> mediaIds),
}