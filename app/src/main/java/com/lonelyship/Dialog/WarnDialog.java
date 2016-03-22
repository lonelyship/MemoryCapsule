package com.lonelyship.Dialog;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lonelyship.Main.R;

import object.TextViewChangeListenr;
import object.UIDefine;

public class WarnDialog extends Dialog
{
    public interface OnWarnDialogListener
    {
        void onWarnDialog_Confirm();
        void onWarnDialog_Cancel();
    }

    private String		m_strTitle = null;
    private String		m_strContent = null;
    private String		m_strConfirm = null;
    private String		m_strCancel = null;

    private OnWarnDialogListener m_onWarnDialogListener = null;

    private View.OnClickListener m_onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int viewID = v.getId();
            if(viewID == R.id.button_Yes)
            {
                if (m_onWarnDialogListener != null)
                {
                    m_onWarnDialogListener.onWarnDialog_Confirm();
                }
                dismiss();
            }
            else if(viewID == R.id.button_No)
            {
                if (m_onWarnDialogListener != null)
                {
                    m_onWarnDialogListener.onWarnDialog_Cancel();
                }
                dismiss();
            }
        }
    };

    public WarnDialog(Context context, OnWarnDialogListener onWarnDialogListener)
    {
        super(context, R.style.dialog);
        setCancelable(false);	//back 不可關閉
        setCanceledOnTouchOutside(false);	//點擊外面dialog不會dismiss
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//不顯示沒有title

        m_onWarnDialogListener = onWarnDialogListener;
    }

    public void uiSetTitleText(String strTitle) {
        m_strTitle = strTitle;
    }

    public void uiSetContentText(String strContent) {
        m_strContent = strContent;
    }

    public void uiSetConfirmText(String strConfirm) {
        m_strConfirm = strConfirm;
    }

    public void uiSetCancelText(String strCancel) {
        m_strCancel = strCancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_layout_dialog_warn);

        UIDefine uiDefine = UIDefine.getInstance(getContext());

        uiDefine.setLayoutParams(findViewById(R.id.linearLayout_Root));

        TextView textView_Title = (TextView) findViewById(R.id.textView_Title);
        textView_Title.setText(m_strTitle);
        uiDefine.setTextSizeAndLayoutParams(textView_Title);

        uiDefine.setLayoutParams(findViewById(R.id.imageView_Line));

        TextView textView_Content = (TextView)findViewById(R.id.textView_Content);
        textView_Content.setText(m_strContent);
        uiDefine.setTextSizeAndLayoutParams(textView_Content);

        uiDefine.setLayoutParams(findViewById(R.id.relativeLayout_Button));

        Button button;

        button = (Button) findViewById(R.id.button_Yes);
        uiDefine.setTextSizeAndLayoutParams(button);
        if (m_strConfirm != null) {
            button.setOnClickListener(m_onClickListener);
            button.setText(m_strConfirm);
        }else {
            button.setVisibility(View.GONE);
        }
        TextViewChangeListenr.vSetTextViewSizeWithSpecficWidth(uiDefine, button, button.getLayoutParams().width, UIDefine.FontSize_15u, UIDefine.FontSize_10u);

        button = (Button) findViewById(R.id.button_No);
        uiDefine.setTextSizeAndLayoutParams(button);
        if (m_strCancel != null) {
            button.setOnClickListener(m_onClickListener);
            button.setText(m_strCancel);
        }else {
            button.setVisibility(View.GONE);
        }
        //設定如果內容textView是空的就設為GONE 就不會空一塊位置
        if (m_strContent != null) {
            textView_Content.setText(m_strContent);
        } else {
            textView_Content.setVisibility(View.GONE);
        }
        TextViewChangeListenr.vSetTextViewSizeWithSpecficWidth(uiDefine, button, button.getLayoutParams().width, UIDefine.FontSize_15u, UIDefine.FontSize_10u);
    }

}