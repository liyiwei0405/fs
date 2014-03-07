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

require_once 'Thrift/ClassLoader/ThriftClassLoader.php';
use Thrift\ClassLoader\ThriftClassLoader;
$loader = new ThriftClassLoader();
$loader->registerNamespace('Thrift','./');
$loader->register();


use Thrift\Transport\TSocket;
use Thrift\Transport\TSocketPool;
use Thrift\Transport\TBufferedTransport;
use Thrift\Transport\TFramedTransport;
use Thrift\Protocol\TBinaryProtocol;
use Thrift\Exception\TTransportException;
use Thrift\Protocol\TBinaryProtocolAccelerated;

require_once 'daemonMessage/MessageService.php';  
require_once 'daemonMessage/Types.php';  
include_once 'helper/helper.php';

class daemon_message_rpc_service
{
	private $host = '';
	private $port = '';
	private $transport = null;
	private $client = null;
	
	function __construct()
	{
		$this->host = "192.168.116.127";  
		$this->port = 61666;
		$this->_init();
	}
	
	private function _init()
	{
		$socket = new TSocket( $this->host , $this->port );
		$this->transport = new TFramedTransport($socket, 1024, 1024);
		$protocol = new TBinaryProtocolAccelerated($this->transport);
		$this->client = new MessageServiceClient($protocol);		
		$this->transport->open();
	}
	public  function get_query_msg_info()
	{		
		$parameter = array('messageName' => 'health','ienv' => '','messageBody' => array(),);
		$qm = new QueryMessage($parameter);;
		$result = $this->client->queryMsg($qm);
		
		return helper::obj_to_arr($result); 
	}

	function __destruct()
	{
		$this->transport->close();
	}
}



