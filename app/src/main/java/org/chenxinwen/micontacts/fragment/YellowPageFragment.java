package org.chenxinwen.micontacts.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chenxinwen.micontacts.R;

/**
 * Created by chenxinwen on 16/8/9.10:09.
 * Email:191205292@qq.com
 */
public class YellowPageFragment extends Fragment{

    public static YellowPageFragment newInstance() {
        return new YellowPageFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_yellow_page,container,false);
        return view;
    }
}
