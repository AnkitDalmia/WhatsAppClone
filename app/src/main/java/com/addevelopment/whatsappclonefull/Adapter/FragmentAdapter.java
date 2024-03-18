package com.addevelopment.whatsappclonefull.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.addevelopment.whatsappclonefull.fragment.CallsFragment;
import com.addevelopment.whatsappclonefull.fragment.ChatFragment;
import com.addevelopment.whatsappclonefull.fragment.StatusFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0 : return new ChatFragment();
            case 1 : return new StatusFragment();
            case 2 : return new CallsFragment();
            default: return new ChatFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0 : return "Chats";
            case 1 : return "Status";
            case 2 : return "Calls";
            default: return "Chats";
        }
    }
}
