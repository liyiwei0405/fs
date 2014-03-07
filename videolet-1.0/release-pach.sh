#/bin/sh

#sphinx_dir=/usr/local/sphinx-videolet
#please use ABSOLUTE PATH
sphinx_dir=$1

releasedir=videolet-deamon-release-1.0/videolet-daemon-v2
mainjar=$releasedir/lib/videolet-collect1.0.jar

start_script=$releasedir/start-videolet-export-daemon.sh
startcmd=$releasedir/startcmd

sphinx_start_script=$releasedir/sphinx_start.sh
sphinx_stop_script=$releasedir/sphinx_stop.sh

sphinx_start_cmd=$releasedir/sphinx_start
sphinx_stop_cmd=$releasedir/sphinx_stop

if [ ! -n "$sphinx_dir" ]; then 
 echo "sphinx_dir is not set! can not continue installing" 
 exit 1 
else
 echo "sphinx_dir is $sphinx_dir"
fi 

if [ ! -d "$sphinx_dir" ]; then
 echo "sphinx dir is not exsit:$sphinx_dir"
 exit 1
fi

if [ -d "$releasedir" ]; then 
 echo "rm -rf $releasedir to delete old build files..."
 rm -rf $releasedir
fi 


ant -buildfile build_main.xml

if [ ! -f "$mainjar" ]; then  
 echo ""
 echo "ERROR...."
 echo "build-fail?????????????? can not find dist jarFile $mainjar"
 exit 1
else
 
 echo "has successfully create temp release package" 
 echo "createing start script: $start_script"

 if [ ! -f $start_script ] ; then
  touch $start_script
 fi
 
 aim_dir=$sphinx_dir/bin/videolet-daemon-v2
 echo "cd $aim_dir" > $start_script
 cat $startcmd >>$start_script
 rm $startcmd
 echo "start script has successfully create into temp package" 
 
 echo "gen $sphinx_start_script"
 echo "cd $aim_dir" > $sphinx_start_script
 cat $sphinx_start_cmd >>$sphinx_start_script
 rm $sphinx_start_cmd
 echo "$sphinx_start_script gen ok"
 
 echo "gen $sphinx_stop_script"
 echo "cd $aim_dir" > $sphinx_stop_script
 cat $sphinx_stop_cmd >>$sphinx_stop_script
 rm $sphinx_stop_cmd
 echo "$sphinx_stop_script gen ok"
  
 echo "has packed the release version into dir videolet-deamon-release-1.0"
fi    


echo "removing old baked release ${aim_dir}-bak..."
if [ -d ${aim_dir}-bak ]; then
 rm -rf ${aim_dir}-bak
 if [ -d ${aim_dir}-bak ]; then
  echo "remove old bak file ${aim_dir}-bak fail!"
  exit 1
 else
  echo "old baked release ${aim_dir}-bak has been removed"
 fi
else
 echo "no old baked release! "
fi

echo "baking old release"
if [ -d $aim_dir ]; then
 mv $aim_dir ${aim_dir}-bak
 if [ ! -d ${aim_dir}-bak ]; then
  echo "bak old release fail"
  exit 1
 else
  echo "bak old release ok: ${aim_dir}-bak"
 fi
else
 echo "no old release, do not need to bak"
fi

echo "installing new release into dir $sphinx_dir/bin/"
cp -rf $releasedir $sphinx_dir/bin/
if [ -d $aim_dir ]; then
 echo "install ok"
else
 echo "install failed: target is $aim_dir" 
 exit 0
fi

if [ -f $sphinx_dir/xdict ]; then
 echo "WARN: xdict has already installed!"
else
 echo "installing chinese lexicon xdict..."
 cp $releasedir/../xdict $sphinx_dir
 
 if [ ! -f $sphinx_dir/xdict ]; then
  echo "WARN: xdict install fail! No enough privileges ?"
  exit 1
 fi
fi

 indexpath=$sphinx_dir/bin/index_3618_videolet
 if [ -d $indexpath ]; then
   echo "WARN: $indexpath has already installed!"
 else
  echo "installing $indexpath..."
  mkdir $indexpath
 
  if [ ! -d $indexpath ]; then
   echo "WARN: $indexpath can not be created at $sphinx_dir/bin/, No enough privileges ?"
   exit 1
  fi
 fi

echo "chmod +x to scripts..."
chmod +x $aim_dir/*.sh
chmod +x $aim_dir/shell/*

echo "system prepare ok"
echo ""
echo  "please kill the old java process, and start $aim_dir/start-videolet-export-daemon.sh"
echo  "!!!!set mongo config at config/collector.conf !!!!!!!!!"

