package org.techtown.practice.Tabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    // 프래그먼트 교체를 보여주는 처리를 구현한 곳
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return Frag1.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "";
            default:
                return null;
        }
    }
}
