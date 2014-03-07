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

require_once 'daemonMessage/MessageService.php';  
require_once 'daemonMessage/Types.php';  
include_once 'rpc_service_base.php';

class daemon_message_rpc_service extends rpc_service_base
{
	public  function get_query_msg_info()
	{		
		//初始化参数
		$parameter = array('messageName' => 'health','ienv' => '','messageBody' => array(),);
		$qm = new QueryMessage($parameter);
		//调用server端的方法
		$result = $this->client->queryMsg($qm);
		
		return $result; 
	}

}



