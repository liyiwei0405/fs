ulimit -n 10240
nohup java -Dfile.encoding=utf-8 -cp .:bin:lib/* com.funshion.search.media.chgWatcher.MediaExportBootStrap&