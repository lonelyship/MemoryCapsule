package com.lonelyship.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lonelyship.Main.MainActivity;
import com.lonelyship.Main.R;

/**
 * Created by lonelyship on 15/10/6.
 */
public class ProgressFragment extends Fragment {

    String m_date = "";
    private TextView tv_time;

    public ProgressFragment() {


    }

    protected LayoutInflater m_layoutInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_layoutInflater = inflater;
        return inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialLayoutComponent(m_layoutInflater, view);

//        MBkUIDefine mBkUIDefine = MBkUIDefine.getInstance(getActivity());
//
//        setTextSizeAndLayoutParams(view, mBkUIDefine);
//
//        setOnParameterAndListener(view);
//
//        registerFragmentAndMBkView(getChildFragmentManager(), view);
    }

    protected int getLayoutResourceId() {
        return R.layout.ui_layout_fragment_progress;
    }

    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

    }
}
