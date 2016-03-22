package com.lonelyship.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.lonelyship.Fragment.TimeFragment;
import com.lonelyship.Main.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lonelyship on 15/10/12.
 */
//時間Adapter
public class TimePagerAdapter extends FragmentPagerAdapter {

    private static ArrayList<TimeFragment> m_listTimePagerItem = new ArrayList<TimeFragment>();
    //public static final int Max_Day_To_Show = 100;

    public TimePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void vInitialListStockPagerItem() {
//            if (null == m_symbolGroup) {
//                return;
//            }
        String date = "";
        for(int i = 0; i < 3 ; i++)
        {
           // date = getDateString(i);
            TimeFragment timeFragment = new TimeFragment();
            m_listTimePagerItem.add(timeFragment);
           // Log.i("lonelyship", "時間:" + date);
        }
    }

    public static void vInitialDay(long _day_from_Today) {

        //SimpleDateFormat sdf_Date = new SimpleDateFormat("MMMMdd日 yyyy \n EEEE");
        //SimpleDateFormat sdf_Date = new SimpleDateFormat("MMMMdd日yyyyEEEE");
        Date date_1 = new Date(System.currentTimeMillis() + (_day_from_Today-1l)*24*60*60*1000l);
        Date date_2 = new Date(System.currentTimeMillis() +  _day_from_Today*24*60*60*1000l);
        Date date_3 = new Date(System.currentTimeMillis() + (_day_from_Today+1l)*24*60*60*1000l);


//        Log.e("lonelyship","前一天:"+ System.currentTimeMillis() + (_day_from_Today-1l)*24*60*60*1000l );
//        Log.e("lonelyship","當前天:"+ System.currentTimeMillis() + (_day_from_Today)*24*60*60*1000l);
//        Log.e("lonelyship","後一天:"+ System.currentTimeMillis() + (_day_from_Today+1l)*24*60*60*1000l );

        if(m_listTimePagerItem.get(0)!=null)
        m_listTimePagerItem.get(0).vInitialDay(date_1);
        if(m_listTimePagerItem.get(1)!=null)
        m_listTimePagerItem.get(1).vInitialDay(date_2);
        if(m_listTimePagerItem.get(2)!=null)
        m_listTimePagerItem.get(2).vInitialDay(date_3);

    }

//   public static String getDateString(int _day) {
//        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy \n EEEE");
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, _day - Max_Day_To_Show + 1);
//        return dateFormat.format(cal.getTime());
//    }

    @Override
    public int getCount() {
        if (null == m_listTimePagerItem) {
            return 0;
        }
        return m_listTimePagerItem.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
//            if (null == m_symbolGroup) {
//                return "未知商品";
//            }
//
//            return m_symbolGroup.uiGetSymbolGroupItems().get(position).uiGetSymbolName();
        return position+"";
    }

    @Override
    public Fragment getItem(int position) {
//            if (null == m_symbolGroup) {
//                return null;
//            }
        if (null == m_listTimePagerItem) {
            return null;
        }
        return m_listTimePagerItem.get(position);
    }

//        public String getCurrentInfoSymbolName() {
//            return m_listStockPagerItem.get(m_viewPager_Stock.getCurrentItem()).strGetSymbolName();
//        }
}
