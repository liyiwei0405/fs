1.ȷ��sphinx�Ѿ���װ�����û�а�װsphinx���밴�����²��谲װsphinx

	����Sphinx���ֵ�xdict_1.1.tar.gz
	http://code.google.com/p/sphinx-for-chinese/downloads/list
	#wget http://sphinx-for-chinese.googlecode.com/files/sphinx-for-chinese-2.1.0-dev-r3361.tar.bz2
	#wget http://sphinx-for-chinese.googlecode.com/files/xdict_1.1.tar.gz
	��ѹ
	#bzip2 -d sphinx-for-chinese-2.1.0-dev-r3361.tar.bz2
	#tar -xvf sphinx-for-chinese-2.1.0-dev-r3361.tar
	#tar -zxvf xdict_1.1.tar.gz
	���밲װsphinx-for-chinese
	#cd sphinx-for-chinese-2.1.0-dev-r3361
	#./configure --prefix=/usr/local/sphinx-for-chinese --with-mysql
	#make && make install

2.ȷ�ϱ��뻷����java��ant���Ѿ�����
3.��sphinx·�����õ�install-script.sh�ġ�sphinx_dir=����
4.����install-script.sh��ִ��Ȩ��
5.����install-script.sh
6.����install-script.sh����ʾ��ɲ����ϵͳ��������,�������£�
	6.1 ����Ѿ�java�ػ������Ѿ����ڣ�����ɱ�����ػ�����
	6.2 �޸�������
		����Ŀ¼��$sphinx_dir/bin/ videolet-daemon
		�༭config/collector.conf  �ļ��С�[fs_video]������[fs_video_ugc]��������mongodb���ӷ�ʽ
		
		[fs_video]
		#mongo���ݿ��IP
		ip = 192.168.16.161
		#mongo���ݿ�Ķ˿�
		port = 27017
		database = corsair_video
		table = fs_video
		
		[fs_video_ugc]
		#mongo���ݿ��IP
		ip = 192.168.16.161
		#mongo���ݿ�Ķ˿�
		port = 27017
		database = corsair_ugc
		table = fs_video_ugc

	6.2 ����java�ػ����̣��磺$sphinx_dir/bin/videolet-daemon/start-videolet-export-daemon.sh
	

7.����ע�����
	7.1 �����ļ�·���� $sphinx_dir/bin/index_videolet
	7.2 �����Ҫ����ugc�����������ļ���"main"����������һ�У�loadUGC=0



