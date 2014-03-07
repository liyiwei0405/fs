//////////////////////////////////////////////////////////////////////////////////////////
// Author: 吴天放
// Copyright 2005-, Funshion Online Technologies Ltd. All Rights Reserved
// 版权 2005-，北京风行在线技术有限公司 所有版权保护
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
// the contents of this file may not be disclosed to third parties, copied or
// duplicated in any form, in whole or in part, without the prior written
// permission of Funshion Online Technologies Ltd.
// 这是北京风行在线技术有限公司未公开的私有源代码。本文件及相关内容未经风行在线技术有
// 限公司事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份.
//////////////////////////////////////////////////////////////////////////////////////////

namespace java com.funshion.gamma.atdd.thrift

enum RetCode {
	OK = 200,           // OK
	BadReq = 400,       // Bad Request //通用错误，如果找不到合适错误类型，优先返回此值。
	UnAuth = 401,       // Unauthorized //未登录
	NotFound = 404,     // Not Found //请求资源不存在
	LenRequire = 411,   // Length Required //未添加Content-Length header
	EntTooLarge = 413,  // Request Entity Too Large //JSON 参数超过系统设置值
	URITooLong = 414,   // Request-URI Too Long //方法或接口超过系统设置值
	Busy = 430,         // Too Busy //用户请求超出频次限制，
	                    // 并提前占用至434错误值，以便必要时添加
	                    // 431 超出月限制，432，超出日限制，433，超出小时或段时间限制等。
	SrvErr = 500,       // Internal Server Error //业务内部未知错误，同步请求时，客户端应明确提示用户。客户端不应重试。
	SrvUnavail = 503,   // Service Unavailable //业务暂时不可用，客户端需重试，N次后仍然失败则报不可用错。
	GateTimeout = 504   // Gateway Timeout //业务处理超时，客户端需重试，N次后仍然失败则报超时错。
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
	UNPUBLISHED = -2;//未审核
	RUBBISH = -1, //-1表示垃圾
	NORMAL = 0, //0表示普通
	WONDERFUL = 5, //5表示精彩
	TOP = 20, //20表示置顶
	RECOMMEND = 30, //30表示编辑推荐
}

struct PageParam {
	1:i32 pageNum,		// 页数
	3:i32 lastId,		// 上一个列表项的Id
	2:i32 pageSize,		// 每页个数
}

/**
 * clientType 取值范围:
 * 'ipad','iphone','apad','aphone','winpad','winphone',
 * 'pc','web','mweb','ott'
 */

/**
 *  用户进行发布动作时的通用信息
 */
struct AddActionInfo {
	1: i32 userId,				// 用户ID
	2: string clientType,	// 客户端类型
	3: i32  ipaddr,				// ip地址
	4: list<SyncsType> appsyncs,		// 三方同步类型
}
