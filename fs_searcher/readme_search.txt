search部署流程：
1.从ssh://git@192.168.16.205:22/home/git_source/search.git上取得最新版本,获取路径：/fs_searcher
2.内网:
     ./start_search_inner.sh
  外网：
     ./start_search_outer.sh