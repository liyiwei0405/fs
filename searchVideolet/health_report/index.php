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

include_once 'service/daemon_message_rpc_service.php';
include_once 'service/daemonMessage/Types.php';
include_once 'helper/queryMsgHelper.php';
include_once 'template/queryMsgTemplate.php';

$daemon_message_rpc_service = new daemon_message_rpc_service();
$query_msg_info = $daemon_message_rpc_service->get_query_msg_info();

unset($query_msg_info['answerBody'][0]);
foreach($query_msg_info['answerBody'] as $service)
	$service_result[] = query_msg_helper::get_service_infos(explode(',',$service));

$query_msg_tpl = query_msg_tpl::query_msg_tpl_index($service_result);

echo $query_msg_tpl;
echo "\n";
