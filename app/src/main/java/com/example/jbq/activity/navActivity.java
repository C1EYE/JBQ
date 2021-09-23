package com.example.jbq.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.jbq.R;
import com.example.jbq.frame.BaseActivity;

import java.util.ArrayList;

public class navActivity extends AppCompatActivity {

    private ViewPager view_pager;
    private int[] mLayoutIDs = {
            R.layout.activity_home,
            R.layout.activity_about
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav2);
        final ArrayList<View> views = new ArrayList<View>();
        for (int i = 0; i < mLayoutIDs.length; i++) {
            views.add(getLayoutInflater().inflate(mLayoutIDs[i], null));
        }
        initView();
        //需要重写四个方法来实现
        view_pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mLayoutIDs.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return false;
            }


            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View child = views.get(position);
                container.addView(child);
                return child;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(views.get(position));
            }
        });
    }

    private void initView() {
        view_pager = findViewById(R.id.vp);
    }
}
