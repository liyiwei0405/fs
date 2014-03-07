namespace java com.funshion.gamma.atdd.thrift.staff

struct StaffMediaRole{
         1: i32 staffId,      //����ID
         2: string nameCn,    //����������
         3: string job, //�����ڸ�ý���еĲ������ͣ�'actor', 'compere', 'director', 'leadingactor', 'producer', 'scripter', 'guest'��
         4: string roleName,  //��ɫ��
}
struct StaffInfo {
         1: i32 staffId,      //����ID
         2: string nameCn,    //����������
         3: i16 quality,      //��������
         4: i32 midNum,       //���ݵ�ý������
}
struct MediaStaffs {
	1: i32 mediaId,		//ý��ID
	2: list<StaffMediaRole> staffs //���ݸ�ý�����Ա��Ϣ
}
enum staffTypeGroup {
	base,	//director,leadingactor,compere
	all
}
struct MediaStaffsResult {
	1: i32 retCode,     // ״̬�루200 | 400��
    2: string retMsg,   // ��Ӧ��Ϣ�����ڴ������
    3: list<MediaStaffs> mediaStaffs
}
struct StaffInfoResult {
	1: i32 retCode,     // ״̬�루200 | 400��
    2: string retMsg,   // ��Ӧ��Ϣ�����ڴ������
    3: list<StaffInfo> staffInfos
}
service StaffService {
	/*    ����ý��ID����������ȡ��������
        mediaIds    ý��ID����
        type		��ְԱ��
    */
	MediaStaffsResult getStaffsByMediaIds(1: list<i32> mediaIds, 2: staffTypeGroup type),
	/*    ��������ID����������ȡ��������
		staffIds ����ID����
	*/ 
	StaffInfoResult getStaffsByStaffIds(1: list<i32> staffIds),
}
