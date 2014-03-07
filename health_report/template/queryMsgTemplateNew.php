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

class query_msg_tpl_new
{
	
	/**
	 * 生成query-msg的模板。
	 * @param array $service_result
	 * @return string
	 */
	public static function query_msg_tpl_index($service_result,$rpc_services=array())
	{		
		if(!is_array($service_result)) return;	
		
		$str = queryMsgCss::get_query_msg_new_css();
		$str .= '<table>
					<tr>
						<td><b>RPC Service</b></td>
						<td><b>Location</b></td>
						<td><b>Method</b></td>
						<td><b>Fail</b></td>
						<td><b>Success</b></td>
						<td><b>Total<b></b></td>
						<td><b>Fail/Total</b></td>
				     </tr>';
		
		foreach($service_result as  $services)
		{
			foreach($services as $service)
			{	
				if(!empty($rpc_services) && !in_array($service['RPC service'], $rpc_services)) continue;
				
				foreach ((array)$service['methods'] as $method)
				{
						if(!$method['data']){													
							$str = $str .'
									<tr>
										<td>'.$service['RPC service'].'</td>
										<td>'.$service['Location'].'</td>
										<td>'.$method['Method'].'</td>
										<td><font color="red">NOT RUN</font></td>
										<td><font color="red">NOT RUN</font></td>
										<td><font color="red">NOT RUN</font></td>
										<td><font color="red">NOT RUN</font></td>
									</tr>';																								
						}else{
							$rate = round(($method['compFail']+$method['exeFail'])/($method['compFail']+$method['exeFail']+$method['oktest']),5);
							$str = $str.'
									<tr>
									<td>'.$service['RPC service'].'</b></td>
									<td>'.$service['Location'].'</b></td>
									<td>'.$method['Method'].'</b></td>
									<td>'.($method['compFail']+$method['exeFail']).'</b></td>
									<td>'.$method['oktest'].'</b></td>
									<td>'.($method['compFail']+$method['exeFail']+$method['oktest']).'</b></td>
									<td>'.($rate*100).'%</b></td>
								    </tr>';							
					}		
				}		
			}
		}		
		$str = $str.'
					</table>';		
				
		return $str;			
	}
	
}
