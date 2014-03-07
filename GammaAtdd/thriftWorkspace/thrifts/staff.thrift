namespace java com.funshion.gamma.atdd.thrift.staff

struct StaffMediaRole{
         1: i32 staffId,      //明星ID
         2: string nameCn,    //明星中文名
         3: string job, //明星在该媒体中的参演类型（'actor', 'compere', 'director', 'leadingactor', 'producer', 'scripter', 'guest'）
         4: string roleName,  //角色名
}
struct StaffInfo {
         1: i32 staffId,      //明星ID
         2: string nameCn,    //明星中文名
         3: i16 quality,      //明星质量
         4: i32 midNum,       //参演的媒体总数
}
struct MediaStaffs {
	1: i32 mediaId,		//媒体ID
	2: list<StaffMediaRole> staffs //参演该媒体的演员信息
}
enum staffTypeGroup {
	base,	//director,leadingactor,compere
	all
}
struct MediaStaffsResult {
	1: i32 retCode,     // 状态码（200 | 400）
    2: string retMsg,   // 响应信息，用于错误调试
    3: list<MediaStaffs> mediaStaffs
}
struct StaffInfoResult {
	1: i32 retCode,     // 状态码（200 | 400）
    2: string retMsg,   // 响应信息，用于错误调试
    3: list<StaffInfo> staffInfos
}
service StaffService {
	/*    根据媒体ID数组批量获取明星数据
        mediaIds    媒体ID数组
        type		演职员组
    */
	MediaStaffsResult getStaffsByMediaIds(1: list<i32> mediaIds, 2: staffTypeGroup type),
	/*    根据明星ID数组批量获取明星数据
		staffIds 明星ID数组
	*/ 
	StaffInfoResult getStaffsByStaffIds(1: list<i32> staffIds),
}
