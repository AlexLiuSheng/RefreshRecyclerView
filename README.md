# RefreshRecyclerView
一个可以添加自定义header的recyclerView，实现了下拉刷新，上拉加载，继承Viewgroup添加header和recyclerview，同步监听和判断滚动事件实现
的recyclerview，目前支持下拉加载上拉刷新，还在不断完善。
先上图：

  ![image](https://github.com/AlexLiuSheng/RefreshRecyclerView/blob/master/g.gif)
  
  使用:
  
  跟普通recyclerview一样使用方法:
  
           //设置layoutmanager
           recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
           //设置adapter
           recyclerView.setAdapter(new MyAdapter(this,data));
           //设置刷新模式:REFRESH,LOADMORE,BOTH,NEITHER
           recyclerView.setMode(Mode.BOTH);
           //监听刷新
           recyclerView.setRereshListener(RefreshListener listener)
           //加载数据调用
           recyclerView.notifyDataSetChanged();
           //插入数据更新item
           recyclerView.notifyDataInserted(int positionStart, int itemCount);
           //移除item
           recyclerView.notifyDataRemoved(int positon);
           //设置头部headerview，默认使用自带DefaultHeaderView
           recyclerView.setHeaderView(View view)
           //设置动画
           recyclerView.setItemAnimator(RecyclerView.ItemAnimator animator)
  
  还在不断完善中...
