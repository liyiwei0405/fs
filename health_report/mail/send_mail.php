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

include_once __DIR__.'/class.smtp.php';
include_once __DIR__.'/class.phpmailer.php';

class mail
{
	
	/** 
	 * @param string $to 表示收件人地址 $subject 表示邮件标题
	 * @param string $subject 表示邮件的主题
	 * @param string $body表示邮件正文
	 */
	public static function send_mail($subject = "",$body = "",$contacter,$files=array())
	{
		$config = config:: get_send_mail_info();
		$to = $contacter;
		error_reporting(E_STRICT);		
		date_default_timezone_set("Asia/Shanghai");//设定时区东八区

		$mail             = new PHPMailer(); //new一个PHPMailer对象出来
		$body             = eregi_replace("[\]",'',$body); //对邮件内容进行必要的过滤
		$mail->CharSet ="UTF-8";//设定邮件编码，默认ISO-8859-1，如果发中文此项必须设置，否则乱码
		$mail->IsSMTP(); // 设定使用SMTP服务
		$mail->SMTPDebug  = 1;                     // 启用SMTP调试功能  1 = errors and messages;2 = messages only 		
		$mail->SMTPAuth   = true;                  // 启用 SMTP 验证功能
		$mail->Host       = "mail.funshion.com";      // SMTP 服务器
		$mail->Port       = 25;                   // SMTP服务器的端口号
		$mail->Username   =  $config['userName'];;  // SMTP服务器用户名
		$mail->Password   = $config['passWord'];            // SMTP服务器密码
		$mail->SetFrom($config['userName'], '-healthrpc <rpc-health@funshion.com>');
		$mail->Subject    = $subject;
		$mail->AltBody    = "To view the message, please use an HTML compatible email viewer!"; 		
		$mail->MsgHTML($body);
		$address = $to;
		
		$mail->AddAddress($address, "收件人名称");
		if($files && is_array($files)){
			foreach($files as $file)
				$mail->AddAttachment($file);
		}
		if(!$mail->Send()) {
			echo "Mailer Error: " . $mail->ErrorInfo;
		} else {
			echo "Message sent! 邮件发送成功！\n";
		}
	}
	
}
