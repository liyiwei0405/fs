namespace java com.funshion.gamma.atdd.playInfo.thrift

// ������Ϣ��ʽ
struct PlayInfoRec {
	1: i32 playInfoID,
	2: i32 mediaID,	
	3: string hashID,
	
	// �缯��������ţ����硰1��
	4: i32 serialNumber,
	
	// ���Ӿ缯��������ţ����硰��1������
	// �������ս�Ŀ��ȡֵҲ�������ơ�20120713����Ӣ����׳�Ǻ���� ��ŮΪ�Ÿ��߸裩��
	5: string serialName,
	
	// �缯����ͼID
	6: i32 serialPicID,
	
	// �༭�޸ĺ�������������ƣ��硰���ഺ�йص�����|��7-9����
	// �������ս�Ŀ��ȡֵҲ��������"�й�������|20120713����Ӣ����׳�Ǻ���� ��ŮΪ�Ÿ��߸裩"
	7: string taskName,
	
	// ��Ƶ�����ȣ����� shot, tv, dvd, hi-dvd, super-dvd�е�ĳһ��
	8: string clarity,
	
	// �������ͣ�ֵΪ ��/chi/arm/und������ֱ�Ϊ ��/����/����/ԭ��
	// ���Ӿ�ͨ�����ֶ����ֶ�����
	9: string langType,
	
	// �������ԣ�ֵΪ�ջ��ַ����硰chi����������
	// ��Ӱͨ�������������ֶ�����
	10: string soundTrackLang,
	
	// ���������޸�ʱ��
	11: i32 modifyDate,
	
	// fsp��ַ�����嶨����ο�redmineҳ��
	12: string fspURL,
	
	// fsp��չ�ֶΣ����嶨����ο�redmineҳ��
	13: string fspExt
}

// Ϊ�˼��ٽӿڸ����������ǵ�����������ID��ѯ������list��ʽ����<������Ϣ>�Ľ����
/*
	�㲥��Ϣ�����ڲ�����
	retCode:500 
	retMsg:"map was null" 
	data: null
	
	�������󣬱���idlistΪ�գ�����id���������ض�����
	retCode:400 
	retMsg:"idlist was null/empty" 
	data:null
	
	��������id�����������ƣ�Ĭ��ֵ1000�������ã�
	retCode:413
	retMsg:"idlist size too large" 
	data:null
	
	ͨ��mediaid��ѯδ�ҵ����
	retCode:404 
	retMsg:"got no result for the id" 
	data:null

	δ�����������ʱ���򷵻�200
	retCode:200 
	retMsg:OK 
	data: ������ݣ���ĳ��idδ��ѯ�������������������Ӧλ��PlayInfoRec��playInfoIDΪ0��
*/
struct PlayInfoRs {
	1: i32 retCode,
	2: string retMsg,
	3: list<PlayInfoRec> data
}

service PlayInfoService {
	// ���������ids��˳������ĳidû�н��������������Ӧλ��ΪplayinfoidΪ0�Ŀս��
	PlayInfoRs getInfoByPlayIDList(1: list<i32> playIDs),
	
	// ���������ids��˳������ĳidû�н��������������Ӧλ��ΪplayinfoidΪ0�Ŀս��
	PlayInfoRs getInfoByHashIDList(1: list<string> hashIDs),
	
	PlayInfoRs getInfoByMediaID(1: i32 mediaID),
}