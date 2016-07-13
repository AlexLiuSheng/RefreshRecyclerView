package com.allenliu.refreshrecyclerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Allen Liu on 2016/7/13.
 */
public class RefreshRecyclerView extends ViewGroup {
    private View headerView;
    private int headerHeight;
    private RecyclerView recyclerView;
    private RefreshListener refreshListener;
    /**
     * 布局模式
     */
    private RecyclerView.LayoutManager recyclerLayoutManager;
    /**
     * 刷新的模式
     */
    private Mode mode=Mode.NEITHER;
    int mLastVisibleItem;
    int mFirstVisibleItem;
    /**
     * 是否在顶部
     */
    private boolean isOnTop=false;
    /**
     * 记录最开始的getScrollY
     */
    int mFristScollerY;
    /**
     * 是否显示底部footerview
     */
    private boolean isShowFooter;
    /**
     * 当前recyclerview 状态
     */
     private  int mStatus;
    /**
     * 3个状态值
     */
    private final int STATUS_NORMAL=0, STATUS_REFRESH = 1,STATUS_LOAD = 2;
    private float lastY;
    private Scroller mScroller;

    public RefreshRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mScroller=new Scroller(context);
        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(params);
        recyclerView.addOnScrollListener(recyclerScrollListener);
        headerView=new DefaultHeaderView(context);
        ViewGroup.LayoutParams params1=new LayoutParams(LayoutParams.MATCH_PARENT,300);
        headerView.setLayoutParams(params1);
        addView(headerView);
        addView(recyclerView);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            height += child.getMeasuredHeight();
        }
        //总高度是两个控件加起来一起的
        setMeasuredDimension(width, height);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            //让header 居中件事
            if (i == 0) {
                int headerLeft = getMeasuredWidth() / 2 - child.getMeasuredWidth() / 2;
                child.layout(headerLeft, top, headerLeft + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            } else
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            top += child.getMeasuredHeight();
        }
        headerHeight = getPaddingTop() + headerView.getMeasuredHeight();
        scrollTo(0, headerHeight);//移动到header下方以显示recyleview
        //记录第一次getScrollY
        mFristScollerY = getScrollY();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return false;
            case MotionEvent.ACTION_MOVE:
                //截取recyclerview 触摸事件
                if (mode!=Mode.LOADMORE&&mode!= Mode.NEITHER && isOnTop && ev.getRawY() > lastY)
                    return true;
                break;

        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
             float  offsetY = Math.abs(event.getRawY() - lastY);
                if (offsetY > 0)
                    scrollToPosition(offsetY);
                else {
                    isOnTop = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (getScrollY() <= 0) {
                   startRefresh();
                } else release();
                break;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }

    }
    /**
     * 滚动到指定位置
     * @param offsetY
     */
    private void scrollToPosition(float offsetY) {
//        if (getScrollY() == mFristScollerY && refreshListener != null)
//            refreshListener.pullToReresh();
        int value = Math.round(offsetY / 2.0F);
        value = mFristScollerY - value;
        scrollTo(0, value);
    }

    /**
     * 还没有到达刷新点释放
     */
    public void release() {
        mStatus = STATUS_NORMAL;
        int currentY = getScrollY();
        mScroller.startScroll(0, currentY, 0, mFristScollerY - currentY);
        invalidate();
    }

    /**
     * 开始刷新
     */
    private void startRefresh() {
        mStatus = STATUS_REFRESH;
        int currentY = getScrollY();
        mScroller.startScroll(0, currentY, 0, (mFristScollerY - headerHeight) - currentY);
        invalidate();
        if(refreshListener!=null)
            refreshListener.pullToReresh();
    }


    RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //滚动结束
            if(newState==RecyclerView.SCROLL_STATE_IDLE){
                //到达顶部
             if(mFirstVisibleItem==0){
                 isOnTop=true;
                 //没到顶部判断是不是到达底部，并且是否在加载
             }else{
                 isOnTop=false;
                 //到达底部
                 if(mLastVisibleItem==recyclerView.getAdapter().getItemCount()-1){
                     //可以加载
                    if(mStatus!=STATUS_LOAD&&mode!=Mode.REFRESH&&mode!= Mode.NEITHER){
                        mStatus=STATUS_LOAD;
                        if(refreshListener!=null)
                        refreshListener.loadMore();
                    }
                     //没到到
                 }
             }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerLayoutManager instanceof LinearLayoutManager) {
                mLastVisibleItem = ((LinearLayoutManager) recyclerLayoutManager).findLastVisibleItemPosition();
                mFirstVisibleItem = ((LinearLayoutManager) recyclerLayoutManager).findFirstCompletelyVisibleItemPosition();

            } else if (recyclerLayoutManager instanceof GridLayoutManager) {
                mLastVisibleItem = ((GridLayoutManager) recyclerLayoutManager).findLastVisibleItemPosition();
                mFirstVisibleItem = ((GridLayoutManager) recyclerLayoutManager).findFirstCompletelyVisibleItemPosition();
            } else if (recyclerLayoutManager instanceof StaggeredGridLayoutManager) {
                //瀑布流布局获取最大的那个一个
                int[] lastPositions = new int[((StaggeredGridLayoutManager) recyclerLayoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) recyclerLayoutManager).findLastVisibleItemPositions(lastPositions);
                mLastVisibleItem = getMaxPosition(lastPositions);
                mFirstVisibleItem = ((StaggeredGridLayoutManager) recyclerLayoutManager).findFirstVisibleItemPositions(lastPositions)[0];
            }
            setFootviewVisible();
        }
    };

    /**
     * 判断是否显示footerview
     */
    private void setFootviewVisible() {
        if(mode==Mode.LOADMORE||mode==Mode.BOTH){
            //显示
          if(mLastVisibleItem+1==recyclerView.getAdapter().getItemCount()){
              isShowFooter=true;
          }else{
              isShowFooter=false;
          }
        }else{
            isShowFooter=false;
        }
        notifyDataSetChanged();
    }

    private int getMaxPosition(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    /**
     *
     * @param animator set recycler animator
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
    }

    /**
     * 数据加载成功调用此方法
     *
     */
    public void notifyDataSetChanged() {
        mStatus = STATUS_NORMAL;
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * 加载成功调用方法
     * @param positionStart
     * @param itemCount
     */
    public void notifyDataInserted(int positionStart, int itemCount) {
        mStatus = STATUS_NORMAL;
        recyclerView.getAdapter().notifyItemRangeInserted(positionStart, itemCount);
    }

    /**
     * 删除item 刷新数据
     * @param position
     */
    public void notifyDataRemoved(int position) {
        mStatus = STATUS_NORMAL;
        recyclerView.getAdapter().notifyItemRemoved(position);
    }
   public void setAdapter(RecyclerView.Adapter adapter){
       recyclerView.setAdapter(adapter);
   }
    public void setMode(Mode mode){
        this.mode=mode;
    }
    public void setRereshListener(RefreshListener refreshListener){
        this.refreshListener=refreshListener;
    }
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager){
        recyclerLayoutManager=layoutManager;
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 设置头部view
     * @param view
     */
    public void setHederView(View view){
        removeView(headerView);
        this.headerView=view;
        addView(headerView,0);
    }
}
