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

//�ֶ��顣Ŀǰֻ�����˼򵥵ļ��֡���������ݿͻ�������������
enum FieldGroupType {
	base,	//ý������ֶ�(mediaId, nameCN, nameEN, nameOT, displayType, language, mediaLength, country, releaseDate, releaseInfo, createDate, modifyDate, website, pinyinCN)
	name,	//ý��id��ý������nameCN��nameEN��nameOT��
	nameTime,	//ý��id��ý������nameCN��nameEN��nameOT���͸���ʱ�䣨releaseDate��createDate��modifyDate��
	all			//ȫ��ý���ֶ�
}

struct MediaListV2 {
    1: i32 retCode,            // ״̬�루200 | 400��
    2: string retMsg,        // ��Ӧ��Ϣ�����ڴ������
    3: i32 sum,            // ���������
    4: list<MediaV2> pageRet,     // ��ҳ���������
}

struct MediaWithClient {
	1: i32 mediaId,
	2: string clientType,
	3: i32 mediaLastNum,
	4: string mediaLastReadableNum,
	5: string mediaLastUpdateTime
}

struct MediaWithClientList {
    1: i32 retCode,            // ״̬�루200 | 400��
    2: string retMsg,        // ��Ӧ��Ϣ�����ڴ������
    3: list<MediaWithClient> ret,     // ���������
}

service MediaServiceV2 {
	/*    ����ý��id�����ȡ����ý������
        mediaId    ý��id
        fieldGroupType	������ȡ��ý���ֶ���
    */
	MediaV2 getMediaById(1: i32 mediaId, 2: FieldGroupType type),
	
	/*    ����ý��id�����ȡ����ý������
        mediaIds    ý��id����
        fieldGroupType	������ȡ��ý���ֶ���
    */
	list<MediaV2> getMediaListByIds(1: list<i32> mediaIds, 2: FieldGroupType type),
	
	/*    ����ý�����ͻ�ȡ����ý������
        displayType    ý������
        fieldGroupType	������ȡ��ý���ֶ���
        pageSize    ��ҳ�������ɵ��÷�ָ������Ϊ�Ƿ�����ʱ���ò����趨Ϊ10;�ò��������ֵ���ƣ����ֵ�����ã�
        pageIndex    ҳ�루�ɵ��÷�ָ������Ϊ�Ƿ�����ʱ,�ò����趨Ϊ1��
    */
	MediaListV2 getMediaListByType(1: string displayType, 2: FieldGroupType type, 3: i32 pageSize, 4: i32 pageIndex),
	
	/*    ����ý�����ʱ���ȡ����ý������
        modifyDate    ����ʱ�䣬��ʽΪ 2000-1-1 00:00:00
        fieldGroupType	������ȡ��ý���ֶ���
        pageSize    ��ҳ�������ɵ��÷�ָ������Ϊ�Ƿ�����ʱ���ò����趨Ϊ10;�ò��������ֵ���ƣ����ֵ�����ã�
        pageIndex    ҳ�루�ɵ��÷�ָ������Ϊ�Ƿ�����ʱ,�ò����趨Ϊ1��
    */
	MediaListV2 getMediaListByDate(1: string modifyDate, 2: FieldGroupType type, 3: i32 pageSize, 4: i32 pageIndex),
	
	/*    ������ʱ����ָ��˳���ȡ��ָ��ʱ��㿪ʼ�и��µľ缯״̬�б�
        modifyDate    ����ʱ�䣬��ʽΪ 2000-1-1 00:00:00��GMT +8��
        limit    ������õĽ������С���������Ϊ100
        order    ������򣬰�����ʱ��acs | desc��Ĭ��asc��
    */
	MediaWithClientList getUpdatedMediasOrderByModifyTime(1: string modifyDate, 2: i32 limit, 3: string order),
	
	/*    ��ȡָ��ý����ָ���ն˵ľ缯״̬
        mediaIds    ý��id����
        clientType    �ͻ�������
    */
	MediaWithClientList getMediasStateOnClient(1: list<i32> mediaIds, 2: string clientType),

	/*    ��ȡָ��ý���ڸ��ն˵ľ缯״̬
        mediaIds    ý��id����
    */
	MediaWithClientList getMediasState(1: list<i32> mediaIds),
}