package com.mateus.resweb;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    private SwipeMenuListView listView;
    private ArrayList<HolderChat> dataArrayHolderChat;
    private AdapterChat listAdapterChat;
    private HolderChat data;
    private Context ctx;
    private ImageView backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (SwipeMenuListView) findViewById(R.id.listview);
        dataArrayHolderChat = new ArrayList<>();
        ctx = Chat.this;

        Toolbar toolbar = findViewById(R.id.toolbarchat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        backbutton = findViewById(R.id.backchat);
        backbutton.setClickable(true);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dataArrayHolderChat.add(data = new HolderChat("User 1", "Text 1",R.drawable.logo));
        dataArrayHolderChat.add(data = new HolderChat("User 2", "Text 2",R.drawable.logo));
        dataArrayHolderChat.add(data = new HolderChat("User 3", "Text 3",R.drawable.logo));

        listAdapterChat = new AdapterChat(this, dataArrayHolderChat);
        listView.setAdapter(listAdapterChat);

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:

                        Toast.makeText(ctx, "Deleted!", Toast.LENGTH_SHORT).show();
                        dataArrayHolderChat.remove(position);
                        listAdapterChat.notifyDataSetChanged();

                        break;
                    case 1:
                        break;
                }
                return false;
            }
        });

    }


    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.parseColor("#F45557")));
            // set item width
            deleteItem.setWidth(150);
            deleteItem.setTitle("x");
            deleteItem.setTitleColor(Color.WHITE);
            deleteItem.setTitleSize(15);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

}
