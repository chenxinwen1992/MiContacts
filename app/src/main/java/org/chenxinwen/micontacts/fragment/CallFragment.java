package org.chenxinwen.micontacts.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chenxinwen.micontacts.R;

/**
 * Created by chenxinwen on 16/8/9.10:07.
 * Email:191205292@qq.com
 */
public class CallFragment extends Fragment{


    public static CallFragment newInstance() {
        return new CallFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_call,container,false);
        return view;
    }
}
