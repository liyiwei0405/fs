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

class query_msg_tpl
{
	private static $template_head = '
	<STYLE type="text/css">  <!--@import url(scrollbar_9512.css); -->BODY { font-size: 14px; line-height: 1.5  } </STYLE><HEAD><META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"></HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"><table border="1">';
	
	private static $template_tail = '</table>';
	
	/**
	 * 生成query-msg的模板。
	 * @param array $service_result
	 * @return string
	 */
	public static function query_msg_tpl_index($service_result)
	{
		
		if(!is_array($service_result)) return;	

		$str = '';
		foreach($service_result as  $services)
		{
			foreach($services as $service)
			{
			
				$str = $str .'<tr><td>'.$service['RPC service'].'</td><td>'."\n";	
	
				
				foreach ((array)$service['methods'] as $method)
				{
						if(!$method['data']){						
							$str = $str .'<table border="1">
										 <tr><td><b>Method</b></td><td><font color="green">'.$method['Method'].'</td></tr>
										<tr><td><b>State</b></td><td><font color="red">NOT RUN</td></tr>
										</table>'."\n";																								
						}else{
							//method
							$str = $str .'
							<table border="1"><tr><td>Method</td></tr><tr><td>'."\n";
							
							$str = $str. '
							<table border="1">'."\n";
							//location
							$str = $str .'
							<tr><td><b>Location</b></td><td><font color="green">'.$service['Location'].'</font>
							 </td></tr>'."\n";
							//method
							$str = $str .'<tr><td>
							<b>Method</b></td><td><font color="green">'.$method['Method'].'
							</td></tr>'."\n";
							//rpc service
							$str = $str .'<tr><td>
							<b>RPC service</b></td><td><font color="green">'.$service['RPC service'].'</font> 
							</td></tr>'."\n";
							//time
							$str = $str.'<tr><td>
							<b>Time </b></td><td>'.$method['FROM'].' '.$method['to'].' ('.$method['usetime'].')'.
							'</td></tr>'."\n";						
							//thread num
							$str = $str.'<tr><td>
							<b>thread Num</b></td><td>'.$method['thread num'].
							'</td></tr>'."\n";
							//sucessTestNum
							$str = $str.'<tr><td>
							<b>sucessTestNum</b></td><td>'.$method['oktest'].
							'</td></tr>'."\n";
							//compareFail
							$str = $str.'<tr><td>
							<b>compareFail</b></td><td>'.$method['compFail'].
							'</td></tr>'."\n";
							//exeFail
							$str = $str.'<tr><td>
							<b>exeFail</b></td><td>'.$method['exeFail'].
							'</td></tr>'."\n";
							
							$str = $str .
							'</table>';
							
							$str = $str .
							'<table border="1"><tr><td colspan="50">usedTimeMs | times(Px%s)</td></tr>'."\n";
							
							$count = count($method['data'][0]);
							
							$str .= self::create_diagram($method['data'][0],0,$count/2)."\n";
							$str .= self::create_diagram($method['data'][1],0,$count/2)."\n";
							$str .= self::create_diagram($method['data'][2],0,$count/2)."\n";
							
							$str .= self::create_diagram($method['data'][0],$count/2,$count)."\n";
							$str .= self::create_diagram($method['data'][1],$count/2,$count)."\n";
							$str .= self::create_diagram($method['data'][2],$count/2,$count)."\n";
							$str .= '</table>';
							
							$str.= '</td></tr></table>';				
					}		
				}		
				$str.= '</td></tr>'."\n";
			}
		}
		
		return self::$template_head.$str.self::$template_tail;
			
	}
	
	/*
	 * 生成ps|time|percent的表格
	 */
	private static function create_diagram($array,$start,$end)
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
