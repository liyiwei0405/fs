#/bin/sh

#please use ABSOLUTE PATH

release_dir=/usr/local

basejar=fsSREBase-1.0.jar
configdir=config
libdir=lib
tmpdir=GammaAtdd
templatedir=template
mainjar=dist/healthWatcher.jar


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
 cp -rf lib savedTestCases $tmpdir/
 cp -rf ./"$templatedir"/* $tmpdir
 if [ x$1 == xouter ]; then
  echo "copy templates(outer) to tmpdir:$tmpdir"
  mv $tmpdir/config/ConfForOuter/* $tmpdir/config/
 elif [ x$1 == xatdd ]; then
  echo "copy templates(atdd) to tmpdir:$tmpdir"
  mv $tmpdir/config/ConfForAtdd/* $tmpdir/config/
 else
  echo "copy templates(inner) to tmpdir:$tmpdir"
  mv $tmpdir/config/ConfForInner/* $tmpdir/config/
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
 mv $basejar ../GammaAtdd/$tmpdir/lib/
fi 

cd ../GammaAtdd
ant -buildfile build_healthWatcher.xml

if [ ! -f "$mainjar" ]; then  
 echo ""
 echo "can not find jarFile $mainjar. build fail???"
 exit 1
else
 mv $mainjar $tmpdir/lib/
fi    

echo "removing old backup release..."
if [ -d $release_dir/GammaAtdd-backup ]; then
 rm -rf $release_dir/GammaAtdd-backup
 if [ -d $release_dir/GammaAtdd-backup ]; then
  echo "remove old bak file fail!"
  exit 1
 else
  echo "old baked release has been removed"
 fi
else
 echo "no old baked release! "
fi

echo "rename old release to backup..."
if [ -d $release_dir/GammaAtdd ]; then
 mv $release_dir/GammaAtdd/ $release_dir/GammaAtdd-backup
 if [ ! -d $release_dir/GammaAtdd-backup ]; then
  echo "backup old release fail"
  exit 1
 else
  echo "backup old release ok"
 fi
else
 echo "no old release, do not need to backup"
fi

echo "installing new release into dir $release_dir/GammaAtdd/"
cp -rf $tmpdir $release_dir/
if [ -d $release_dir/GammaAtdd ]; then
 echo "install ok"
else
 echo "install failed" 
 exit 0
fi


echo "chmod +x to scripts..."
chmod +x $release_dir/GammaAtdd/*.sh

PID=`ps ax|grep HealthWatcher |grep java |awk '{print $1}'`

if [ -n "$PID" ]; then
 echo "the old process pid is: $PID"
 kill -9 $PID
 sleep 1
 OLDPID=`ps ax|grep HealthWatcher |grep java |awk '{print $1}'`
 
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
cd /usr/local/GammaAtdd
dos2unix start.sh
./start.sh
sleep 1
NEWPID=`ps ax|grep HealthWatcher |grep java |awk '{print $1}'`

if [ -n "$NEWPID" ]; then
 echo "new service start successfully, pid is:$NEWPID"
else
 echo "service start failed, exit"
 exit 1
fi

echo "shell Exit..."

