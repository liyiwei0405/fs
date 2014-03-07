//////////////////////////////////////////////////////////////////////////////////////////
// Author: �����
// Copyright 2005-, Funshion Online Technologies Ltd. All Rights Reserved
// ��Ȩ 2005-�������������߼������޹�˾ ���а�Ȩ����
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
// the contents of this file may not be disclosed to third parties, copied or
// duplicated in any form, in whole or in part, without the prior written
// permission of Funshion Online Technologies Ltd.
// ���Ǳ����������߼������޹�˾δ������˽��Դ���롣���ļ����������δ���������߼�����
// �޹�˾��������ͬ�⣬���������κε�����͸¶��й�ܲ��ֻ�ȫ��; Ҳ�������κ���ʽ��˽�Ա���.
//////////////////////////////////////////////////////////////////////////////////////////

namespace java com.funshion.gamma.atdd.thrift

enum RetCode {
	OK = 200,           // OK
	BadReq = 400,       // Bad Request //ͨ�ô�������Ҳ������ʴ������ͣ����ȷ��ش�ֵ��
	UnAuth = 401,       // Unauthorized //δ��¼
	NotFound = 404,     // Not Found //������Դ������
	LenRequire = 411,   // Length Required //δ���Content-Length header
	EntTooLarge = 413,  // Request Entity Too Large //JSON ��������ϵͳ����ֵ
	URITooLong = 414,   // Request-URI Too Long //������ӿڳ���ϵͳ����ֵ
	Busy = 430,         // Too Busy //�û����󳬳�Ƶ�����ƣ�
	                    // ����ǰռ����434����ֵ���Ա��Ҫʱ���
	                    // 431 ���������ƣ�432�����������ƣ�433������Сʱ���ʱ�����Ƶȡ�
	SrvErr = 500,       // Internal Server Error //ҵ���ڲ�δ֪����ͬ������ʱ���ͻ���Ӧ��ȷ��ʾ�û����ͻ��˲�Ӧ���ԡ�
	SrvUnavail = 503,   // Service Unavailable //ҵ����ʱ�����ã��ͻ��������ԣ�N�κ���Ȼʧ���򱨲����ô�
	GateTimeout = 504   // Gateway Timeout //ҵ����ʱ���ͻ��������ԣ�N�κ���Ȼʧ���򱨳�ʱ��
}

struct ResultStatus {
    1:i32 retCode,
    2:string retMsg,
}

struct ResultI32 {
	1:i32 retCode,
	2:string retMsg,
	3:i32 result
}

struct ResultI64 {
	1:i32 retCode,
	2:string retMsg,
	3:i64 result
}

struct ResultListI32 {
	1:i32 retCode,
	2:string retMsg,
	3:list<i32> result
}

struct ResultListI64 {
	1:i32 retCode,
	2:string retMsg,
	3:list<i64> result
}

enum SyncsType {
	SINA    = 1,
	ZONE    = 2,
	TENCENT = 4,
	RENREN  = 8,
	KAIXIN  = 16,
}


enum QualityType {
	UNPUBLISHED = -2;//δ���
	RUBBISH = -1, //-1��ʾ����
	NORMAL = 0, //0��ʾ��ͨ
	WONDERFUL = 5, //5��ʾ����
	TOP = 20, //20��ʾ�ö�
	RECOMMEND = 30, //30��ʾ�༭�Ƽ�
}

struct PageParam {
	1:i32 pageNum,		// ҳ��
	3:i32 lastId,		// ��һ���б����Id
	2:i32 pageSize,		// ÿҳ����
}

/**
 * clientType ȡֵ��Χ:
 * 'ipad','iphone','apad','aphone','winpad','winphone',
 * 'pc','web','mweb','ott'
 */

/**
 *  �û����з�������ʱ��ͨ����Ϣ
 */
struct AddActionInfo {
	1: i32 userId,				// �û�ID
	2: string clientType,	// �ͻ�������
	3: i32  ipaddr,				// ip��ַ
	4: list<SyncsType> appsyncs,		// ����ͬ������
}
