<?php
//////////////////////////////////////////////////////////////////////////////////////////
// Author: xietao
// Copyright 2005-, Funshion Online Technologies Ltd. All Rights Reserved
// 版权 2005-，北京风行在线技术有限公司 所有版权保护
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
// the contents of this file may not be disclosed to third parties, copied or
// duplicated in any form, in whole or in part, without the prior written
// permission of Funshion Online Technologies Ltd.
// 这是北京风行在线技术有限公司未公开的私有源代码。本文件及相关内容未经风行在线技术有
// 限公司事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份.
//////////////////////////////////////////////////////////////////////////////////////////

//日志存放的目录
define(FILE_LOG_LOCATION,'log/');
class config
{
	//接收的邮箱
	public static function get_contacters_all()
	{
		return array(
				'xietao@funshion.com',
		//		'team-gamma@funshion.com',
				);
	}	
	//healthReport的服务器
	public static function get_hosts()
	{
		return array(
				array('ip' => '192.168.116.127','port'=>61666),
				array('ip' => '192.168.115.56','port'=>61666),
				);
	}
	//发送邮件的用户名和密码
	public static function get_send_mail_info()
	{
		return array(
				'userName' => 'rpc-health@funshion.com',
				'passWord' => 'wt9YyJh_3D',
		);
	}
	//指定用户邮箱地址对应的服务
	public static function contacter_services()
	{
		return array(
//					'xietao@funshion.com'=>array('ChgWatcher','mediaserviceV2'),
				);
	}
	
}