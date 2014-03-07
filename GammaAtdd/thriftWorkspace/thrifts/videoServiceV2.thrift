namespace java com.funshion.videoService.thrift

//VideoletInfo��files�ֶ�
struct FileStruct{
	1:string hashId
	2:string fileName
	3:string fileFormat
	4:i32 bitRateKbps	//��ʶ��������ʣ�����������Ƶ����ʵ���ʣ���λ:kbps
	5:string clarity	//shot,tv,dvd,high-dvd,super-dvd
	6:i64 fileSizeByte
}

//������Ϣ�����б�ҳ��ʾ�Ļ����ֶ�
struct VideoletBaseInfo{
	1:i32 videoId    	//ID����redis��������������
    2:string title      //��Ƶ����
    3:string picturePath     //��Ƶ��ͼ·���������һ������ƵֵΪhttp��ͷ���ַ��������������ɵľ�ΪͼƬID�ţ�
    4:i32 timeLen       //��Ƶ��Ƶʱ������λ�룩
    5:i32 ordering  	//����ֵ
    6:double score      //��Ƶ���֣���Դ���������ּ���ͣ�
    7:i32 scoreNum     	//��������
    8:i32 playNum 		//�����ܹۿ���
    9:i32 playNumInit   //ץȡ���ܹۿ��� + �����ܹۿ���
    10:string plots 	//����
	11:i32 modifyDate	//��¼����ʱ��
}

struct VideoletInfo{
	1:i32 videoId	//ID����redis��������������
	2:i32 mapId		//��Ƶӳ��ID��ӳ��ԭ������videoid
	3:string vId	//��ʶ����macross���ɲ�ͨ���ӿ�֪ͨ��
	4:string source	//��Ƶ��Դ������(fs), ����(tudou), ����(tvm)����
	5:string cls	//��Ƶ���ͣ�normal����ͨС��Ƶ����ugc��UGC��Ƶ����news��������Ƶ
	6:string title	//��Ƶ����
	7:string picturePath	//��Ƶ��ͼ·���������һ������ƵֵΪhttp��ͷ���ַ��������������ɵľ�ΪͼƬID�ţ�
	8:i32 timeLen	//��Ƶ��Ƶʱ������λ�룩
	9:string plots	//��Ƶ����
	
	10:list<string> types	//��Ƶ����
	11:list<string> tags	//��Ƶ��ǩ
	12:list<FileStruct> files  	//�����ļ����ϡ�[{"hashid":$hashid, "filename":$filename, "bitrate":$bitrate, "clarity":$clarity, "filesize":$filesize, "fileformat":$fileformat},����]	
	
	13:list<i32> relateVideoIds 	//�����ƵID����
	14:list<i32> relateSpecialIds	//���ר��ID����
	15:list<i32> videoEventIds; //��Ƶ����¼�ID���ϡ��磺��ע��������
	
	16:double score	//��Ƶ���֣���Դ���������ּ���ͣ�
	17:i32 scoreNum	//��������
	18:i32 commentNum; //��������
	
	19:i32 dayNum	//���չۿ���
	20:i32 weekNum	//�ܹۿ���
	21:i32 monthNum	//�¹ۿ���
	22:i32 playNum	//�����ܹۿ���
	
	23:double dayIndex	//���չۿ�ָ��
	24:double weekIndex	//�ܹۿ�ָ��
	25:double monthIndex	//�¹ۿ�ָ��
	26:double playIndex	//�ܹۿ�ָ��
	27:string videoIndexes	//��Ƶָ���������磺"0,21,0,0"
	
	28:i32 showAd; //�Ƿ񲥷Ź��
	29:i32 votable; //�Ƿ������
	30:i32 ordering	//����ֵ
	31:string publishFlag;	//����״̬��ʶ��published��ʾ�ѷ�����topublish��ʾ��������
	32:i32 createDate	//����ʱ��
	33:i32 modifyDate	//��¼����ʱ��
	//news
	34:i32 lastNum; //���24Сʱ�ۿ���
	35:i32 lastIndex; //���24Сʱ�ۿ�ָ��
	36:i32 isVideo; //�Ƿ���Ƶ����
	37:i32 recommend; //�Ƿ��Ƽ���1Ϊ �Ƽ���0,Ϊ δ�Ƽ���
	38:i32 isRank; //�Ƿ�����
	//ugc
	39:i32 playNumInit	//ץȡ���ܹۿ��� + �����ܹۿ���
	40:map<string, i32> commentNumFunshion; //���еĵ�������ͳ�ƣ�array('all_num'=>0)
	41:i32 transmitNum;	//ץȡ��ת������ʼ��ֵ
	42:list<i32> relateMediaIds	//���ý��
	43:list<i32> relateStaffIds	//�������
	44:i32 userId	//�ϴ��û�ID
	45:string userName	//�ϴ��û���
	46:i32 moduleId; //����������ĵ�ģ��id
	47:string position; //�����������ģ���еľ���λ�á� ��Ľ���Сͼ����Ľ����ͼ��ģ�齹�㣬ģ�飬���а�
	48:string picF; //ugc video ����ͼ
	49:string picV; //ugc video ��ͼ
	50:i32 isBottomRecommend; //�Ƿ����. 1��ʾ���ƣ�0��ʾ���ǵ���
	51:i32 recommendDate; //ugc video ���Ƽ�����Ĵ�ֱҳ���ʱ�䣬������Ĵ�ֱҳ�������ȡֵ
	52:map<string, list<i32>> tacticArea; //΢��Ƶ�ĵ������: {"iphone":[1,2,3],"ipad":[3,4,12]}
	53:map<string, list<i32>> videoContentClasses; //΢��Ƶ�����ݲ���
	//normal
	54:list<i32> tagIds	//��Ŀ��ǩID�ֶ� 
	55:double hot	//΢��Ƶ�ȶ�
	56:string copyright; //��Ȩ��
	57:i32 picTitleModifydate; //ͼƬ���������ʱ��
	58:string extend	//���г�Ʒ�йص���չ�ֶΣ�"extend" : {"0" : "set", "twang_name" : "������", "twang_name" : "������", "titlepath" : "/subject/48497/"} ����������key����ȷ�������л�Ϊstring
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

/*��������fsql��ѯ�﷨���ͣ�
ʾ����(title SEARCH '�人' && TIMELEN = ?) &&(SCORE = '88' || PLAY_NUM = ?) not (TAG_IDS = ? || SOURCE = ?) order by id desc limit offset,length

�α�����
	֧��'?'���ֽ�ƴ���ַ�����֧�ַ�Χ: 1.�������Ҳ����  2.limit�� offset��length
���ӷ��� 
	and(&&), or(||), not(!)
�������� 
	=, >, <, >=, <=, search, like 
	search/like ��ֻ֧��title�ֶΣ�inΪ�����ؼ��֣��ݲ�֧�֣�Ҳ������ʹ��
	
�������Ҳ���Ϊ�ַ��������õ����Ż�˫����, ���ֿɲ��ӣ����Ų�֧��\' \"ת�壬��String��������������parameter��ʽ(��title = ?), �����ִ�Сд;
order by֧�����м��������ֶ�;
limit��offset��length��Ϊ������ֻ��һ��Ĭ��Ϊlength��offset 0;
true, false�Ǳ����ؼ���

�����ֶη�Χ��
	videoid, title, timelen, score, playnum, tagids, types, tags, createdate, modifydate, source
*/
	
//�������߼������, ��ѯVideoletBaseInfo
struct VideoletBaseRetrieveResult{
        1:i32 retCode //200: ok;300-499, error request; 500 - 599 inner error; others: not defined error
        2:string retMsg //Exception trace 
        3:i32 total //total matched result count
        4:double usedTime //used time, in ms
        5:list<VideoletBaseInfo> videoList //sub-result limit by LimitBy
}

//�������߼������, ��ѯVideoletInfo
struct VideoletRetrieveResult{
        1:i32 retCode //200: ok;300-499, error request; 500 - 599 inner error; others: not defined error
        2:string retMsg //Exception trace 
        3:i32 total //total matched result count
        4:double usedTime //used time, in ms
        5:list<VideoletInfo> videoList	 //sub-result limit by LimitBy
}

//retrive defines end

service VideoService{
	//������ȡС��Ƶ�����ֶ�
	VideoBaseListResult getVideoBaseListByIds(1:list<i32> videoIdList)
	//������ȡС��Ƶ�����ֶ�
	VideoListResult getVideoListByIds(1:list<i32> videoIdList)
	
	//���������VideoletBaseInfo
	VideoletBaseRetrieveResult retrieveVideoletBaseInfo(1:RetrieveStruct rs)
	//���������VideoletInfo
	VideoletRetrieveResult retrieveVideolet(1:RetrieveStruct rs)
}