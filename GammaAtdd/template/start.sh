ulimit -n 10240
nohup java -Dfile.encoding=utf-8 -cp .:lib/*  -XX:+PrintGC  -XX:+PrintGCTimeStamps -XX:+UseConcMarkSweepGC com.funshion.gamma.atdd.healthWatcher.HealthWatcher &