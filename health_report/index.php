<?php
//////////////////////////////////////////////////////////////////////////////////////////
// Author: 谢涛
// Copyright 2005-, Funshion Online Technologies Ltd. All Rights Reserved
// 版权 2005-，北京风行在线技术有限公司 所有版权保护
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
// the contents of this file may not be disclosed to third parties, copied or
// duplicated in any form, in whole or in part, without the prior written
// permission of Funshion Online Technologies Ltd.
// 这是北京风行在线技术有限公司未公开的私有源代码。本文件及相关内容未经风行在线技术有
// 限公司事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份.
//////////////////////////////////////////////////////////////////////////////////////////

include_once 'mail/send_mail.php';
include_once 'service/daemon_message_rpc_service.php';
include_once 'service/daemonMessage/Types.php';
include_once 'module/queryMsg.php';
include_once 'template/queryMsgTemplateNew.php';
include_once 'template/queryMsgTemplate.php';
include_once 'template/sendMailTemplate.php';
include_once 'configure.php';
include_once 'helper/helper.php';
date_default_timezone_set('PRC');

$hosts = config::get_hosts();
$contactersAll = config::get_contacters_all();
$contacter_services = config::contacter_services();
helper::check_dir(FILE_LOG_LOCATION);

$service_results = query_msg_module::get_service_info($hosts);				
if($contactersAll)	sendAll($service_results,$contactersAll);	
if($contacter_services) sendPart($service_results, $contacter_services);	

function sendAll($service_results,$contactersAll)
{
	if(empty($service_results)) return;	
	foreach ($service_results as $ip => $service_result)
	{
		$query_msg_tpl_new = query_msg_tpl_new::query_msg_tpl_index($service_result);
		$query_msg_tpl = query_msg_tpl::query_msg_tpl_index($service_result);
		$short = FILE_LOG_LOCATION.date('Y-m-d').'_'.$ip.'_short.html';
		$detail = FILE_LOG_LOCATION.date('Y-m-d').'_'.$ip.'_detailed.html';
		file_put_contents($short,$query_msg_tpl_new);
		file_put_contents($detail,$query_msg_tpl);
		send_mail_tmp::send_msg_contacters($contactersAll,'DailyReport['.date('Y-m-d')."]".$ip,$query_msg_tpl_new,array($short,$detail));
	}
}

function sendPart($service_results,$contacter_services)
{
	if(empty($service_results)) return;
	
	foreach ($contacter_services as $contacter => $services)
	{
		$service_dir = FILE_LOG_LOCATION.$contacter.'/';
		helper::check_dir($service_dir);
		
		foreach ($service_results as $ip => $service_result)
		{
			$query_msg_tpl_new = query_msg_tpl_new::query_msg_tpl_index($service_result,$services);
			$query_msg_tpl = query_msg_tpl::query_msg_tpl_index($service_result,$services);
			$short = $service_dir.date('Y-m-d').'_'.$ip.'_short.html';
			$detail = $service_dir.date('Y-m-d').'_'.$ip.'_detailed.html';			
			file_put_contents($short,$query_msg_tpl_new);
			file_put_contents($detail,$query_msg_tpl);			
			send_mail_tmp::send_msg_contacters(array($contacter),'DailyReport['.date('Y-m-d')."]".$ip,$query_msg_tpl_new,array($short,$detail));
		}
				
	}
}


