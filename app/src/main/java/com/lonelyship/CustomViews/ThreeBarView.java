package com.lonelyship.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * 畫手機目前硬碟空間容量的Bar
 */

public class ThreeBarView extends View
{
    private static int Color_Disable = Color.argb(0, 0, 0, 0);
    private int m_width,m_hight;
    private int offset = 0;
    private double m_UsedSpaceWeight = 0, m_FreeSpaceWeight = 0;
    private Paint m_paint = new Paint();
    private boolean m_isDisable = false;
    private String m_sTotalSpace = "";
    private String m_sFreeSpace = "";
    private String m_sUsedSpace = "";

    private int m_iFreeBarColor;
    private int m_iFreeBarTextColor;
    private int m_iUsedBarColor;
    private int m_iUsedBarTextColor;
    private int m_iDefaultColor;


    public ThreeBarView(Context context)
    {
        super(context);
    }
    public ThreeBarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private void initUI()
    {
        if(m_width != getWidth() || m_hight != getHeight())
        {
            if(m_width == 0 || m_hight == 0)
            {
                m_paint.setTextSize(28);
                m_paint.setTypeface(Typeface.MONOSPACE);
                m_width = getWidth();
                m_hight = getHeight();

                //土黃色
                m_iFreeBarColor = Color.rgb(255, 153, 51);
                //亮綠色
                m_iFreeBarTextColor = Color.rgb(153,255,102);
                //咖啡色
                m_iUsedBarColor = Color.rgb(153,51,0);
                //粉紅色
                m_iUsedBarTextColor = Color.rgb(255,80,80);

                m_iDefaultColor = Color.WHITE;

            }
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        initUI();

        if(m_width == 0 || m_hight == 0)
        {
            return;
        }
        //初始底色
        if(m_isDisable)	//如果disable塗上透明
        {
            m_paint.setColor(Color_Disable);
            canvas.drawRect(0, 0, m_width, m_hight, m_paint);
        }
        else
        {
            m_paint.setColor(Color.WHITE);

            canvas.drawRect(0, 0, m_width, m_hight, m_paint);
            if(Double.isNaN(m_UsedSpaceWeight) || Double.isNaN(m_FreeSpaceWeight))
            {
                return;
            }

            //土黃色
            m_paint.setColor(m_iFreeBarColor);
            canvas.drawRect(0, 0, (int) (m_FreeSpaceWeight * m_width), m_hight, m_paint);

            //亮綠色
            m_paint.setColor(m_iFreeBarTextColor);
            canvas.drawText(m_sFreeSpace, 0, m_hight - 10, m_paint);


            //咖啡色
            m_paint.setColor(m_iUsedBarColor);
            canvas.drawRect((int) (m_width - m_UsedSpaceWeight * m_width) + offset, 0, m_width, m_hight, m_paint);

            //紅色
            m_paint.setColor(m_iUsedBarTextColor);
            canvas.drawText(m_sUsedSpace, (int) (m_width - m_UsedSpaceWeight * m_width), m_hight - 10, m_paint);
        }
    }

    public void setWeight(double freeSpaceWeight,double usedSpaceWeight)
    {
        m_FreeSpaceWeight = freeSpaceWeight;
        m_UsedSpaceWeight = usedSpaceWeight;
    }

    public void setDisable(boolean is)
    {
        m_isDisable = is;
    }

    public void setOffset (int offset)
    {
        this.offset = offset;
    }

    public void setSpaceString(String sFree, String sUsed)
    {
        m_sFreeSpace = sFree;
        m_sUsedSpace = sUsed;
    }

    public void uiSetDisable (boolean bDisable)
    {
        this.m_isDisable = bDisable;
    }

}