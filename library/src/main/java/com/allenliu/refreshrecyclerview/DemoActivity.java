package com.allenliu.refreshrecyclerview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class DemoActivity extends AppCompatActivity {
RefreshRecyclerView recyclerView;
    private Handler handler;
    private ArrayList<String>data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        init();
    }

    private void init() {
        handler=new Handler(){
            public void handleMessage(Message msg){
                recyclerView.notifyDataSetChanged();
            }
        };
        recyclerView= (RefreshRecyclerView) findViewById(R.id.recyclerView);
        data=new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String text="测试数据";
            data.add(text);
        }
        //设置layoutmanager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //设置adapter
        recyclerView.setAdapter(new MyAdapter(this,data));
        //设置模式:REFRESH,LOADMORE,BOTH,NEITHER
        recyclerView.setMode(Mode.BOTH);
        recyclerView.setRereshListener(new RefreshListener() {
            @Override
            public void pullToReresh() {
                Toast.makeText(DemoActivity.this,"refresh",Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5; i++) {
                            data.add(0,"新增数据");
                        }
                        handler.sendEmptyMessage(1);
                    }
                },2000);
            }

            @Override
            public void loadMore() {
                Toast.makeText(DemoActivity.this,"load more",Toast.LENGTH_SHORT).show();
            }
        });

    }

}
