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

class query_msg_helper
{
	/**
	 * 获取服务具体的信息。
	 * @param array $services
	 * @return array
	 */
	public static function get_service_infos($services){
		
		if(!is_array($services)) return ;		
		if(count($services) < 3) return;
			
		//rpc service
		$temp = explode(':', $services[0]);
		$temp[1] = preg_replace('/\r|\n|\t/', '', $temp[1]);
		$temp[0] = preg_replace('/\r|\n|\t/', '', $temp[0]);
		$rpc_service = trim($temp[1]);
		
		//rpc server
		$temp = explode(':', $services[1]);
		$temp[1] = preg_replace('/\r|\n|\t/', '', $temp[1]);
		$temp[0] = preg_replace('/\r|\n|\t/', '', $temp[0]);
		$temp[0] = preg_replace('/class-name/', '', $temp[0]);
	
		$rpc_server = trim($temp[0]);
		
		$rpc_service_methods = array();
		
		$location = $temp[2].':'.$temp[3].',';
		
		unset( $services[0]);
		unset( $services[1]);
		//true
		foreach($services as $service)
		{
			if(!$service) continue;
			if(strstr($service,'RpcServerInstance')){
				
				$explode_server_instance = explode('RpcServerInstance:', $service);
				
				$service = $explode_server_instance[0];
				
				$method_infos = explode('Method', $service);
				if(!$method_infos) return;
				$temp = explode('=', $method_infos[0]);
				$temp[1] = preg_replace('/\r|\n|\t/', '', $temp[1]);
				$temp[0] = preg_replace('/\r|\n|\t/', '', $temp[0]);
				
				if(isset($method_infos[0])) unset($method_infos[0]);
				
				$rpc_service_methods[] = array(
						'RPC service' => $rpc_service,
						'RPC Server' => $rpc_server,
						'Location'  => $location.trim($temp[0]).'='.trim($temp[1]),
						'methods' => self::_get_method_info($method_infos),
				);
				
				
				$location = trim($explode_server_instance[1]).',';
				
								
			}else{
				$method_infos = explode('Method', $service);
				if(!$method_infos) return;
				$temp = explode('=', $method_infos[0]);
				$temp[1] = preg_replace('/\r|\n|\t/', '', $temp[1]);
				$temp[0] = preg_replace('/\r|\n|\t/', '', $temp[0]);
				
				if(isset($method_infos[0])) unset($method_infos[0]);
				
				$rpc_service_methods[] = array(
						'RPC service' => $rpc_service,
						'RPC Server' => $rpc_server,
						'Location'  => $location.trim($temp[0]).'='.trim($temp[1]),
						'methods' => self::_get_method_info($method_infos),
						);
			}
					
		}
		return $rpc_service_methods;

	}
	
	
	private static  function _get_method_info($method_infos)
	{
		if(!is_array($method_infos)) return array();
		
		$rpc_service_methods = array();
		
		foreach($method_infos as $method_info ){
			if(!$method_info) continue;
			if(strstr('NOT',$method_info)){
				$temp = explode('\n', $method_info);
				$result['Method'] = strstr(':', $temp[0]) ? substr($temp[0],1,-1):$temp[0];
				$result['data'] = array();
			}else{
				$data = explode('Px%s', $method_info);
				$head = !$data[0] ? array():self::get_service_info($data[0]);
				$body = !isset($data[1]) ? array():self::get_service_data($data[1]);
		
				$result = array_merge($head,array('data' => $body));
			}
		
			$rpc_service_methods[] = $result;
		
		}
		
		return $rpc_service_methods;
	}
	
	/*
	 * 从string中获取ps|time|percent
	 */
	private static function get_service_data($string)
	{
		if(!is_string($string)) return;
		if(!$string) return;
	
		$array = explode(' ', $string);
		if(!$array) return;
		$result = array();
		$number = array();
		$times  = array();
		$percent = array();
		
		foreach($array as $key => $value){	
			if(!$value) continue;
			$var = preg_replace('/\r|\n|\t/', '#', $value);
			$temp = explode('#', $var);

			$count = count($temp);
			if(!$temp[$count-1]) continue;
			$number[] = $temp[$count-3];
			$times[] = $temp[$count-2];
			$percent[] = $temp[$count-1];
		}
		
		return array($number,$times,$percent);
	}
	
	/*
	 * 获取Location time等数据
	 */
	private static function get_service_info($string)
	{
		if(!is_string($string)) return;
		if(!$string) return;
		
		$var = preg_replace('/\r|\n|\t/', '#', $string);
		$array = explode('#', $var);
		if(!$array) return;
		$result = array();
		foreach($array as $key => $value)
		{
			if(!$value) continue;
		
			if(strstr($value,'=')){
				$temp = explode('=', $value);
				$result[trim($temp[0])] = $temp[1];
			}elseif (strstr($value,':')){	
				if(strstr($value,'FROM') || strstr($value,'from') ||strstr($value,'TO')|| strstr($value,'to')){
					$temp = explode(' ', trim($value));
					$date = explode(':', $temp[0]);
					$mico_seonds = explode('.', $temp[1]);
					$result[trim($date[0])] = $date[1].'_' .$mico_seonds[0];					
				}elseif(strstr($value,'seconds')){
					$temp = explode(':', $value);
					$seconds = explode(' ', trim($temp[1]));					
					$result[trim($temp[0])] = round($temp[1]/60,2).' minutes';

				}else{
					$temp = explode(':', $value);
					if($temp[0]){
						$result[trim($temp[0])] = $temp[1];
					}else{
						$result['Method'] = $temp[1];
					}
				}				

			}elseif (strstr($value,'test') || strstr($value,'Fail')){
				$temp = explode(' ', $value);
				$result[trim($temp[0])] = $temp[1];
			}else{
				continue;
			}
		}
		
		return $result;
		
	}
	
	
	public static function fun_px($array,$start,$end)
	{
		if(!is_array($array)) return;
	
		$str = '<tr>';
		for($i=$start;$i<$end;$i++){
			$str = $str.'<td>'.$array[$i].'</td>';
	
		}
		$str.= '</tr>';
	
		return $str;
	}
}
