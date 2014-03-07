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

include_once 'queryMsgCss.php';
class query_msg_tpl
{		
	/**
	 * 生成query-msg的模板。
	 * @param array $service_result
	 * @return string
	 */
	public static function query_msg_tpl_index($service_result,$rpc_services=array())
	{		
		if(!is_array($service_result)) return;	
		
		
		$str = queryMsgCss::get_query_msg_css();
		$str .= '<table border="1">';
		foreach($service_result as  $services)
		{
			foreach($services as $service)
			{	
				if(!empty($rpc_services) && !in_array($service['RPC service'], $rpc_services)) continue;				
				$str = $str .'<tr><td>'.$service['RPC service'].'</td><td>'."\n";	
				
				foreach ((array)$service['methods'] as $method)
				{
						if(!$method['data']){						
							$str = $str .'
										<table border="1">
										<tr><td><b>Method</b></td><td><font color="green">'.$method['Method'].'</td></tr>
										<tr><td><b>State</b></td><td><font color="red">NOT RUN</td></tr>
										</table>';																								
						}else{
							//method
							$str = $str .'
										<table border="1"><tr><td>Method</td></tr><tr><td>';							
							$str = $str. '
										<table border="1">';
							//location
							$str = $str .'
										<tr><td><b>Location</b></td><td><font color="green">'.$service['Location'].'</font>
										 </td></tr>';
							//method
							$str = $str .'<tr><td>
										<b>Method</b></td><td><font color="green">'.$method['Method'].'
										</td></tr>';
							//rpc service
							$str = $str .'<tr><td>
										<b>RPC service</b></td><td><font color="green">'.$service['RPC service'].'</font> 
										</td></tr>';
							//time
							$str = $str.'<tr><td>
										<b>Time </b></td><td>'.$method['FROM'].' '.$method['to'].' ('.$method['usetime'].')'.
										'</td></tr>';						
							//thread num
							$str = $str.'<tr><td>
										<b>thread Num</b></td><td>'.$method['thread num'].
										'</td></tr>';
							//sucessTestNum
							$str = $str.'<tr><td>
										<b>sucessTestNum</b></td><td>'.$method['oktest'].
										'</td></tr>';
							//compareFail
							$str = $str.'<tr><td>
										<b>compareFail</b></td><td>'.$method['compFail'].
										'</td></tr>';
							//exeFail
							$str = $str.'<tr><td>
										<b>exeFail</b></td><td>'.$method['exeFail'].
										'</td></tr>';						
							$str = $str .
										'</table>';
							
							$str = $str .
										'<table border="1"><tr><td colspan="50">usedTimeMs | times(Px%s)</td></tr>';							
							$count = count($method['data'][0]);							
							$str .= self::_create_diagram($method['data'][0],0,$count/2);
							$str .= self::_create_diagram($method['data'][1],0,$count/2);
							$str .= self::_create_diagram($method['data'][2],0,$count/2);							
							$str .= self::_create_diagram($method['data'][0],$count/2,$count);
							$str .= self::_create_diagram($method['data'][1],$count/2,$count);
							$str .= self::_create_diagram($method['data'][2],$count/2,$count);
							$str .= '
									</table>';
							
							$str.= '
									</td></tr></table>';				
					}		
				}		
				$str.= '
						</td></tr>';
			}
		}
		$str .= '
				</table>';
		
		return $str;			
	}
	
	/*
	 * 生成ps|time|percent的表格
	 */
	private static function _create_diagram($array,$start,$end)
	{
		if(!is_array($array)) return;
	
		$str = '
				<tr>';
		for($i=$start;$i<$end;$i++)
			$str = $str.'
						<td>'.$array[$i].'</td>';			
		$str.= '
				</tr>';
	
		return $str;
	}
		
}
