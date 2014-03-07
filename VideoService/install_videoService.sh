#/bin/sh

#please use ABSOLUTE PATH

release_dir=/usr/local/search

basejar=fsSREBase.jar
configdir=config
libdir=lib
tmpdir=videolet
mainjar=$tmpdir/lib/videolet.jar
templatedir=template

echo "release_dir is $release_dir"
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
 cp -rf lib $tmpdir/
 cp -rf ./"$templatedir"/* $tmpdir
 if [ x$1 == xouter ]; then
  echo "copy templates(outer) to tmpdir:$tmpdir"
  mv $tmpdir/config/ConfForOuter/videoService.conf $tmpdir/config/
 elif [ x$1 == xatdd ]; then
  echo "copy templates(atdd) to tmpdir:$tmpdir"
  mv $tmpdir/config/ConfForAtdd/videoService.conf $tmpdir/config/
 else
  echo "copy templates(inner) to tmpdir:$tmpdir"
  mv $tmpdir/config/ConfForInner/videoService.conf $tmpdir/config/
 fi
 rm -rf $tmpdir/config/ConfFor*
fi 

cd ../fs_SRE_base/;
ant -buildfile build_base.xml
if [ ! -f "$basejar" ]; then  
 echo ""
 echo "can not find jarFile $basejar. build fail???"
 exit 1
else
 mv $basejar ../VideoService/$tmpdir/lib/
fi 

cd ../VideoService
ant -buildfile build_videoService.xml
if [ ! -f "$mainjar" ]; then  
 echo ""
 echo "can not find jarFile $mainjar. build fail???"
 exit 1
fi    

echo "removing old backup release..."
if [ -d $release_dir/videolet-backup ]; then
 rm -rf $release_dir/videolet-backup
 if [ -d $release_dir/videolet-backup ]; then
  echo "remove old bak file fail!"
  exit 1
 else
  echo "old baked release has been removed"
 fi
else
 echo "no old baked release! "
fi

echo "rename old release to backup..."
if [ -d $release_dir/videolet ]; then
 mv $release_dir/videolet/ $release_dir/videolet-backup
 if [ ! -d $release_dir/videolet-backup ]; then
  echo "backup old release fail"
  exit 1
 else
  echo "backup old release ok"
 fi
else
 echo "no old release, do not need to backup"
fi

echo "installing new release into dir $release_dir/videolet/"
cp -rf $tmpdir $release_dir/
if [ -d $release_dir/videolet ]; then
 echo "install ok"
else
 echo "install failed" 
 exit 1
fi

PID=`ps ax|grep videoService |grep java |awk '{print $1}'`

if [ -n "$PID" ]; then
 echo "the old process pid is: $PID"
 kill -9 $PID
 sleep 1
 OLDPID=`ps ax|grep videoService |grep java |awk '{print $1}'`
 
 if [ ! -n "$OLDPID" ]; then
  echo "killed old process successfully"
 else
  echo "old service cannot be killed:'$OLDPID'. exit"
  exit 1
 fi

else
 echo "there is no old process"
fi

cd $release_dir/videolet
echo "chmod +x to scripts..."
chmod +x *.sh
dos2unix *.sh

echo "starting new service..."
./start.sh
sleep 1
NEWPID=`ps ax|grep videoService |grep java |awk '{print $1}'`

if [ -n "$NEWPID" ]; then
 echo "new service start successfully, pid is:$NEWPID"
else
 echo "service start failed, exit"
 exit 1
fi

echo "shell Exit..."

