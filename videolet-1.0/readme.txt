1.确认sphinx已经安装，如果没有安装sphinx，请按照以下步骤安装sphinx

	下载Sphinx和字典xdict_1.1.tar.gz
	http://code.google.com/p/sphinx-for-chinese/downloads/list
	#wget http://sphinx-for-chinese.googlecode.com/files/sphinx-for-chinese-2.1.0-dev-r3361.tar.bz2
	#wget http://sphinx-for-chinese.googlecode.com/files/xdict_1.1.tar.gz
	解压
	#bzip2 -d sphinx-for-chinese-2.1.0-dev-r3361.tar.bz2
	#tar -xvf sphinx-for-chinese-2.1.0-dev-r3361.tar
	#tar -zxvf xdict_1.1.tar.gz
	编译安装sphinx-for-chinese
	#cd sphinx-for-chinese-2.1.0-dev-r3361
	#./configure --prefix=/usr/local/sphinx-for-chinese --with-mysql
	#make && make install

2.确认编译环境（java、ant）已经存在
3.将sphinx路径配置到install-script.sh的“sphinx_dir=”项
4.给予install-script.sh可执行权限
5.运行install-script.sh
6.根据install-script.sh的提示完成部署和系统启动操作,具体如下：
	6.1 如果已经java守护进程已经存在，则先杀死此守护进程
	6.2 修改配置项
		进入目录：$sphinx_dir/bin/ videolet-daemon
		编辑config/collector.conf  文件中“[fs_video]”、“[fs_video_ugc]”区块下mongodb连接方式
		
		[fs_video]
		#mongo数据库的IP
		ip = 192.168.16.161
		#mongo数据库的端口
		port = 27017
		database = corsair_video
		table = fs_video
		
		[fs_video_ugc]
		#mongo数据库的IP
		ip = 192.168.16.161
		#mongo数据库的端口
		port = 27017
		database = corsair_ugc
		table = fs_video_ugc

	6.2 启动java守护进程，如：$sphinx_dir/bin/videolet-daemon/start-videolet-export-daemon.sh
	

7.其他注意事项：
	7.1 索引文件路径： $sphinx_dir/bin/index_videolet
	7.2 如必须要禁用ugc，可在配置文件中"main"区块下增加一行：loadUGC=0



