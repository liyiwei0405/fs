#/bin/sh

#please use ABSOLUTE PATH

parent_dir=/usr/local/search
release_dir=/usr/local/search/media

configdir=config
libdir=lib
tmpdir=search
templatedir=search-template
mainjar=search.jar

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
  rm -rf $tmpdir/config/mediaSSForOuter
  mv $tmpdir/config/mediaSSForInner $tmpdir/config/mediaSS
 elif [ $1 == outer ]; then
  echo "copy templates(outer) to tmpdir:$tmpdir"
  rm -rf $tmpdir/config/mediaSSForInner
  mv $tmpdir/config/mediaSSForOuter $tmpdir/config/mediaSS
 else
  echo "copy templates(inner) to tmpdir:$tmpdir"
  rm -rf $tmpdir/config/mediaSSForOuter
  mv $tmpdir/config/mediaSSForInner $tmpdir/config/mediaSS
 fi
fi 

if [ ! -f "$mainjar" ]; then  
 echo ""
 echo "can not find dist jarFile $mainjar. build fail???"
 exit 1
fi    

echo "move jar to tmpdir:$tmpdir"
mv "$mainjar" $tmpdir/lib/

echo "removing old backup release..."
if [ -d $release_dir/search-backup ]; then
 rm -rf $release_dir/search-backup
 if [ -d $release_dir/search-backup ]; then
  echo "remove old bak file fail!"
  exit 1
 else
  echo "old baked release has been removed"
 fi
else
 echo "no old baked release! "
fi

echo "rename old release to backup..."
if [ -d $release_dir/search ]; then
 mv $release_dir/search/ $release_dir/search-backup
 if [ ! -d $release_dir/search-backup ]; then
  echo "backup old release fail"
  exit 1
 else
  echo "backup old release ok"
 fi
else
 echo "no old release, do not need to backup"
fi

echo "installing new release into dir $release_dir/search/"
cp -rf $tmpdir $release_dir/
if [ -d $release_dir/search ]; then
 echo "install ok"
else
 echo "install failed" 
 exit 0
fi


echo "chmod +x to scripts..."
chmod +x $release_dir/search/*.sh

PID=`ps ax|grep MediaSSDaemon |grep java |awk '{print $1}'`

if [ -n "$PID" ]; then
 echo "the old process pid is: $PID"
 kill -9 $PID
 sleep 1
 OLDPID=`ps ax|grep MediaSSDaemon |grep java |awk '{print $1}'`
 
 if [ ! -n "$OLDPID" ]; then
  echo "killed old process successfully"
 else
  echo "old service cannot be killed:'$OLDPID'. exit"
  exit 1
 fi

else
 echo "there is no old process"
fi

echo $BUILD_ID
$BUILD_ID="donkillme"
echo $BUILD_ID

echo "starting new service..."
cd /usr/local/search/media/search
java -Dfile.encoding=utf-8 -cp .:bin:lib/* -Xmx512m com.funshion.search.media.search.MediaSSDaemon &
sleep 1
NEWPID=`ps ax|grep MediaSSDaemon |grep java |awk '{print $1}'`

if [ -n "$NEWPID" ]; then
 echo "new service start successfully, pid is:$NEWPID"
else
 echo "service start failed, exit"
 exit 1
fi

echo "shell Exit..."

