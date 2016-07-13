package com.allenliu.refreshrecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Allen Liu on 2016/7/13.
 */
public class RecyclerViewHelper {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public RecyclerViewHelper(RecyclerView recyclerView) {
        this.recyclerView=recyclerView;
    }
    public RecyclerView getRecyclerView(){
        return  recyclerView;
    }

}
