//PortalCondition��php�ν�����������ֱ��ʹ��
enum TokenType{
        tSTART
        tEND
        tLstSTART
        tLstEnd
        tCONTENT
}

//ͬ�ϣ���ֱ��ʹ��
struct Token{
        1: TokenType type;
        2: string token;
}

//�൱�� SQL�� LIMIT, limit offset, length
struct LimitRetrieve{
        1: i32 offset
        2: i32 limit
}

//���򣬲����ý�ʹ��ȫ��ƥ��Ȩ��
struct SortRetrieve{
        1:string field 
        2:bool asc
}

struct RetrieveStruct{//maybe support multi QueryStruct search
        1:i32 ver //current version is 1, other value will get error 301
        2:LimitRetrieve limits; //limit define
        3:list<SortRetrieve> sortFields //sort define, ���� order by x ��desc�� order by y (desc)
        4:list<Token>conditions
}

//��¼��һ���ֶ�����ֵ
struct MedieRetrieveResultItem{//item k-v define
        1:string field
        2:string value
}
//һ����¼��record)
struct MediaRetrieveResultRecord{//one record
        1:list<MedieRetrieveResultItem>items
}

//�������߼������
struct MediaRetrieveResult{
        1:i32 retCode //200: ok;300-499, error request; 500 - 599 inner error; others: not defined error
        2:string retMsg //Exception trace 
        3:i32 total //total matched result count
        4:double usedTime //used time, in ms
        5:list<MediaRetrieveResultRecord> ids //sub-result limit by LimitBy
}

//�������߼�����API
service MediaSearchService{
        MediaRetrieveResult retrive1(1:RetrieveStruct qs)
}