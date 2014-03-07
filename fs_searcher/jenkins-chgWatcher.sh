#/bin/sh

#please use ABSOLUTE PATH

parent_dir=/usr/local/search
release_dir=/usr/local/search/media

configdir=config
libdir=lib
tmpdir=chgWatcher
templatedir=chgWatcher-template
mainjar=chgWatcher.jar
reportdir=unitReports
successReport=success.xml
failReport=fail.xml

start_script=$tmpdir/start.sh


if [ ! -n "$release_dir" ]; then 
 echo "release_dir is not set! can not continue installing" 
 exit 1 
else
 echo "release_dir is $release_dir"
fi 

if [ ! -d "$parent_dir" ];then
 mkdir $parent_dir
fi

if [ ! -d "$release_dir" ]; then
 mkdir $release_dir
 if [ ! -d "$release_dir" ]; then
  echo "make release_dir fail!exit"
  exit 1
 fi
fi

if [ ! -d "$reportdir" ];then
 mkdir $reportdir
fi

if [ -d "$tmpdir" ]; then 
 echo "delete old tmpdir..."
 rm -rf $tmpdir
fi 
mkdir $tmpdir

if [ ! -d "$templatedir" ]; then 
 echo "template resources doesn't exists!exit"
 exit 1
else
 cp -rf ./"$templatedir"/* $tmpdir
 if [ x$1 == x ]; then
  echo "copy templates(inner) to tmpdir:$tmpdir"
  rm -rf $tmpdir/config/MediaExportForOuter
  mv $tmpdir/config/MediaExportForInner $tmpdir/config/MediaExport
 elif [ $1 == outer ]; then
  echo "copy templates(outer) to tmpdir:$tmpdir"
  rm -rf $tmpdir/config/MediaExportForInner
  mv $tmpdir/config/MediaExportForOuter $tmpdir/config/MediaExport
 else
  echo "copy templates(inner) to tmpdir:$tmpdir"
  rm -rf $tmpdir/config/MediaExportForOuter
  mv $tmpdir/config/MediaExportForInner $tmpdir/config/MediaExport
 fi
fi 

if [ ! -f "$mainjar" ]; then  
 echo ""
 echo "can not find dist jarFile $mainjar. build fail???"
 exit 1
fi    

echo "move jar to tmpdir:$tmpdir"
mv "$mainjar" $tmpdir/lib/

if [ -f "$failReport" ] || [ ! -f "$successReport" ]; then
 echo ""
 echo "junit test failed! exit"
 exit 1
else
 echo ""
 echo "junit test ok"
 echo ""
fi

echo "removing old backup release..."
if [ -d $release_dir/chgWatcher-backup ]; then
 rm -rf $release_dir/chgWatcher-backup
 if [ -d $release_dir/chgWatcher-backup ]; then
  echo "remove old bak file fail!"
  exit 1
 else
  echo "old baked release has been removed"
 fi
else
 echo "no old baked release! "
fi

echo "rename old release to backup..."
if [ -d $release_dir/chgWatcher ]; then
 mv $release_dir/chgWatcher/ $release_dir/chgWatcher-backup
 if [ ! -d $release_dir/chgWatcher-backup ]; then
  echo "backup old release fail"
  exit 1
 else
  echo "backup old release ok"
 fi
else
 echo "no old release, do not need to backup"
fi

echo "installing new release into dir $release_dir/chgWatcher/"
cp -rf $tmpdir $release_dir/
if [ -d $release_dir/chgWatcher ]; then
 echo "install ok"
else
 echo "install failed" 
 exit 0
fi


echo "chmod +x to scripts..."
chmod +x $release_dir/chgWatcher/*.sh

PID=`ps ax|grep MediaExportBootStrap |grep java |awk '{print $1}'`

if [ -n "$PID" ]; then
 echo "the old process pid is: $PID"
 kill -9 $PID
 sleep 1
 OLDPID=`ps ax|grep MediaExportBootStrap |grep java |awk '{print $1}'`
 
 if [ ! -n "$OLDPID" ]; then
  echo "killed old process successfully"
 else
  echo "old service cannot be killed:'$OLDPID'. exit"
  exit 1
 fi

else
 echo "there is no old process"
fi

echo "starting new service..."
cd /usr/local/search/media/chgWatcher
./start.sh
sleep 1
NEWPID=`ps ax|grep MediaExportBootStrap |grep java |awk '{print $1}'`

if [ -n "$NEWPID" ]; then
 echo "new service start successfully, pid is:$NEWPID"
else
 echo "service start failed, exit"
 exit 1
fi

echo "shell Exit..."

