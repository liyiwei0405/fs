ulimit -n 10240
nohup java -Dfile.encoding=utf-8 -cp .:bin:lib/* -Xmx512m com.funshion.luc.defines.SSDaemon &