//////////////////////////////////////////////////////////////////////////////////////////
// Author: 高珊
// Copyright 2005-, Funshion Online Technologies Ltd. All Rights Reserved
// 版权 2005-，北京风行在线技术有限公司 所有版权保护
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
// the contents of this file may not be disclosed to third parties, copied or
// duplicated in any form, in whole or in part, without the prior written
// permission of Funshion Online Technologies Ltd.
// 这是北京风行在线技术有限公司未公开的私有源代码。本文件及相关内容未经风行在线技术有
// 限公司事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份.
//////////////////////////////////////////////////////////////////////////////////////////

struct UcsCondition {
    1:string clientType,       // 客户端类型
    2:string clientVersion,    // 客户端版本号
	3:string channel,          // 渠道号
	4:string mac,              // 客户端mac地址
	5:i64 installtime,         // 客户端安装时间
	6:string ipaddr,           // 客户端IP地址
	7:i32 userid,              // 登录用户ID
}

struct Tactics {
    1:string userClass,     // 用户分类标识
    2:i32 areaTactic,       // 地域策略ID
	3:i32 areaTacticFlag,   // 地域策略是否生效
}

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

struct UcsObjectResult {
    1:RetCode retCode,    // 返回码
    2:string retMsg,      // 返回信息
	3:Tactics ucsObject,  // 用户分类结果
}

struct UcsStringResult {
    1:RetCode retCode,   // 返回码
    2:string retMsg,     // 返回信息
	3:string ucsString,  // 用户分类结果加密字符串
}

struct UserClassResult {
    1:RetCode retCode,   // 返回码
    2:string retMsg,     // 返回信息
	3:string userClass,  // 用户分类标识
}

struct AreaTacticResult {
    1:RetCode retCode,   // 返回码
    2:string retMsg,     // 返回信息
	3:i32 areaTactic,    // 地域策略ID
}


service UCS {

    /**
     * 获取用户策略数据，包括用户分类标识、地域策略值、地域策略是否生效三部分
     *
     * @param UcsCondition ucsCondition 用户分类条件
     * @return UcsObjectResult
    */
    UcsObjectResult getUcsObject(1:UcsCondition ucsCondition),
	
	/**
     * 获取用户策略数据，用户分类标识、地域策略值、地域策略是否生效的拼接加密字符串
     *
     * @param UcsCondition ucsCondition 用户分类条件
     * @return UcsStringResult
    */
    UcsStringResult getUcsString(1:UcsCondition ucsCondition),
	
	/**
     * 根据客户端类型获取默认的用户分类标识
     *
     * @param string 客户端类型
     * @return UserClassResult
    */
    UserClassResult getUserDefaultClassTag(1:string clientType),
	
	/**
     * 根据客户端类型和地区名称获取用户所在地域策略ID
     *
     * @param string, 客户端类型, string 用户所在地区
     * @return AreaTacticResult
    */
    AreaTacticResult getAreaTacticId(1:string clientType,2:string area),
}   
       
