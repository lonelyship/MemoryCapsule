package com.lonelyship.Dialog;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lonelyship.Main.R;

import object.TextViewChangeListenr;
import object.UIDefine;

public class SelectModeDialog extends Dialog
{
    public interface OnSelectModeDialogListener
    {
        void onSelectModeDialog_Confirm();
        void onSelectModeDialog_Cancel();
        void onSelectModeDialog_SelectLeft();
        void onSelectModeDialog_SelectRight();
    }

    private String		m_strTitle = null;
    private String		m_strContent = null;
    private String		m_strConfirm = null;
    private String		m_strCancel = null;

    private int 		m_iIvLeft  ;
    private int		    m_iIvRight ;

    private OnSelectModeDialogListener m_onSelectModeDialogListener = null;

    private View.OnClickListener m_onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int viewID = v.getId();
            if(viewID == R.id.button_Yes)
            {
                if (m_onSelectModeDialogListener != null)
                {
                    m_onSelectModeDialogListener.onSelectModeDialog_Confirm();
                }
                dismiss();
            }
            else if(viewID == R.id.button_No)
            {
                if (m_onSelectModeDialogListener != null)
                {
                    m_onSelectModeDialogListener.onSelectModeDialog_Cancel();
                }
                dismiss();
            }
            else if(viewID == R.id.iv_left)
            {
                if (m_onSelectModeDialogListener != null)
                {
                    m_onSelectModeDialogListener.onSelectModeDialog_SelectLeft();
                }
                dismiss();
            }
            else if(viewID == R.id.iv_right)
            {
                if (m_onSelectModeDialogListener != null)
                {
                    m_onSelectModeDialogListener.onSelectModeDialog_SelectRight();
                }
                dismiss();
            }
        }
    };

    public SelectModeDialog(Context context, OnSelectModeDialogListener onSelectModeDialogListener)
    {
        super(context, R.style.dialog);
        setCancelable(false);	//back 不可關閉
        setCanceledOnTouchOutside(false);	//點擊外面dialog不會dismiss
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//不顯示沒有title

        m_onSelectModeDialogListener = onSelectModeDialogListener;
    }

    public void uiSetTitleText(String strTitle) {
        m_strTitle = strTitle;
    }

    public void uiSetContentText(String strContent) {
        m_strContent = strContent;
    }

    public void uiSetIvLeftId(int iIvLeft) {
        m_iIvLeft = iIvLeft;
    }

    public void uiSetIvRightId(int iIvRight) {
        m_iIvRight = iIvRight;
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
        this.setContentView(R.layout.app_layout_dialog_select_mode);

        UIDefine uiDefine = UIDefine.getInstance(getContext());

        uiDefine.setLayoutParams(findViewById(R.id.linearLayout_Root));

        TextView textView_Title = (TextView) findViewById(R.id.textView_Title);
        textView_Title.setText(m_strTitle);
        uiDefine.setTextSizeAndLayoutParams(textView_Title);

        uiDefine.setLayoutParams(findViewById(R.id.imageView_Line));

        uiDefine.setLayoutParams(findViewById(R.id.linearLayout_Mode));
        ImageView iv_left = (ImageView) findViewById(R.id.iv_left);
        ImageView iv_right = (ImageView) findViewById(R.id.iv_right);
        if(m_iIvLeft != 0 && m_iIvRight!=0)
        {
            iv_left.setBackgroundResource(m_iIvLeft);
            iv_right.setBackgroundResource(m_iIvRight);

            iv_left.setOnClickListener(m_onClickListener);
            iv_right.setOnClickListener(m_onClickListener);
        }
        uiDefine.setLayoutParams(findViewById(R.id.iv_left));
        uiDefine.setLayoutParams(findViewById(R.id.iv_right));

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