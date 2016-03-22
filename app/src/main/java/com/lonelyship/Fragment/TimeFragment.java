package com.lonelyship.Fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lonelyship.Main.MainActivity;
import com.lonelyship.Main.R;

import java.util.Calendar;
import java.util.Date;

import object.UIDefine;

/**
 * Created by lonelyship on 15/10/6.
 */
public class TimeFragment extends Fragment {

    String m_date = "";
    private TextView tv_day;
    private TextView tv_year;
    private TextView tv_month;
    private TextView tv_week;
    public TimeFragment(){

        m_date = MainActivity.m_strFocusDay;

    };

    public TimeFragment(String _date) {
        m_date = _date;
    }

    protected LayoutInflater	m_layoutInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_layoutInflater = inflater;
        return inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initialLayoutComponent(m_layoutInflater, view);

        UIDefine uiDefine = UIDefine.getInstance(getContext());
//
        setTextSizeAndLayoutParams(view, uiDefine);
//
//        setOnParameterAndListener(view);
//
//        registerFragmentAndMBkView(getChildFragmentManager(), view);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.ui_layout_fragment_main_info_time;
    }

    protected void initialLayoutComponent(LayoutInflater inflater, View view)
    {
        tv_day   = (TextView) view.findViewById(R.id.tv_day);
        tv_month = (TextView) view.findViewById(R.id.tv_month);
        tv_year  = (TextView) view.findViewById(R.id.tv_year);
        tv_week  = (TextView) view.findViewById(R.id.tv_week);
        //tv_time.setText(m_date);
        setTimeFragmentView(new Date());
    }

    public void setTextSizeAndLayoutParams(View view, UIDefine uiDefine){

        uiDefine.setTextSizeAndLayoutParams(tv_day);
        uiDefine.setTextSizeAndLayoutParams(tv_month);
        uiDefine.setTextSizeAndLayoutParams(tv_year);
        uiDefine.setTextSizeAndLayoutParams(tv_week);
    }

    public void vInitialDay(Date date)
    {
        if(tv_day!=null)
        setTimeFragmentView(date);
    }

    public void setTimeFragmentView(Date date)
    {
        int iColor = Color.BLACK;

        String Week = (String) DateFormat.format("EEEE", date);//Thursday
        String Month = (String) DateFormat.format("MMM", date); //06
        String Year = (String) DateFormat.format("yyyy", date); //2013
        String Day = (String) DateFormat.format("dd", date); //20

//        Log.e("loneylyship","Week:"+Week);
//        Log.e("loneylyship","Month:"+Month);
//        Log.e("loneylyship","Year:"+Year);
//        Log.e("loneylyship","Day:"+Day);

        tv_day.setText(Day);
        tv_month.setText(Month);
        tv_year.setText(Year);
        tv_week.setText(Week);

        Calendar c = Calendar.getInstance();
        c.setTime(date); // yourdate is a object of type Date

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

//        Log.e("loneylyship","dayOfWeek:"+dayOfWeek);
        if(dayOfWeek == 7)iColor =0xFF227700;         //禮拜六設綠色
        else if(dayOfWeek == 1)iColor =0XFFCC0000;    //禮拜日設紅色

        tv_day.setTextColor(iColor);
        tv_month.setTextColor(iColor);
        tv_year.setTextColor(iColor);
        tv_week.setTextColor(iColor);
    }
}
