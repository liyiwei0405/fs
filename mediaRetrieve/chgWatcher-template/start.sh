ulimit -n 10240
nohup java -Dfile.encoding=utf-8 -cp .:bin:lib/* com.funshion.retrieve.media.chgWatcher.MediaRetrieveExportBootStrap &