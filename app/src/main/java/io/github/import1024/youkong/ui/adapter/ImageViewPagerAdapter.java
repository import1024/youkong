package io.github.import1024.youkong.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import io.github.import1024.youkong.ui.fragment.ImageViewFragment;

/**
 * Created by import1024 on 16-8-18.
 */
public class ImageViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> imgs;

    public ImageViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        ImageViewFragment fragment = ImageViewFragment.getInstance(imgs.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }
}
