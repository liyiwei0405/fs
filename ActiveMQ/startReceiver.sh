ulimit -n 10240
nohup java -Dfile.encoding=utf-8 -cp .:lib/* -Xms2g -Xmx3g com.funshion.activemq.Receiver &