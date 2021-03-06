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

class helper{
	public static function obj_to_arr($obj)
	{
		$obj = (array)$obj;
		foreach($obj as $key=>$val)
		{
			if(gettype($val) == 'resource') return;
			if(gettype($val) == 'object' || gettype($val) == 'array')
				$obj[$key] = (array)self::obj_to_arr($val);
		}
		return $obj;
	}
	
	public static function check_dir($dir)
	{
		if(is_dir($dir) || @mkdir ($dir,0777)){
			return;
		}else{
			echo '无法创建目录：'.$dir."\n";
			exit;
		}
	}
	
	public static function clock_to_timestamp($array)
	{
		$year = date('Y');
		$month = date('m');
		$day = date('d');
		
		return 	mktime($array['hour'], $array['minute'], $array['second'], $month, $day,$year);			
	}
}