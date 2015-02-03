package pl.torun.zsmeie.meteozsmeie.adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;


public class PagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
    private final Context mContext;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<>();
    private final String TAG = "";
    private TextView mCategoryTypeNameTextView;

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;
        private final String title;

        TabInfo(Class<?> _class, Bundle _args, String _title) {
            clss = _class;
            args = _args;
            title = _title;
        }
    }


    public PagerAdapter(FragmentManager fMenager, FragmentActivity fa, ViewPager pager, TextView pageTitleTextView) {
        super(fMenager);

        mContext = fa;
        mCategoryTypeNameTextView = pageTitleTextView;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(Class<?> clss, Bundle args, String title) {
        TabInfo info = new TabInfo(clss, args, title);
        mTabs.add(info);
        notifyDataSetChanged();
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }



    @Override
    public void onPageSelected(int position) {
        mCategoryTypeNameTextView.setText(mTabs.get(position).title);
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }


    @Override
    public int getCount() {
        return mTabs.size();
    }

}