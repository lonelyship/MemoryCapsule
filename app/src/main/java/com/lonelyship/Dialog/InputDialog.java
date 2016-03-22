package com.lonelyship.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lonelyship.Main.R;

import object.TextViewChangeListenr;
import object.UIDefine;


public class InputDialog extends Dialog
{

	public interface OnInputDialogParameter
	{
		/**Set the type of the content with a constant as defined for {@link android.view.inputmethod.EditorInfo#inputType}*/
		int GetInputType();
		/**@return 取得「輸入長度限制」*/
		int getMaxLength();
		/**@return 取得「提示文字」*/
		String getHint();
		
	}

	public interface OnInputDialogListener
	{
		void onInputDialog_Confirm(String strInput);
		void onInputDialog_Cancel();
	}

	private String 		m_strTitle		= null;
	private String 		m_strContent	= null;
    private String 		m_strInput	= null;
	private String 		m_strConfirm	= null;
	private String 		m_strCancel		= null;
	private EditText	m_editInput		= null;

	private OnInputDialogListener m_onInputDialogListener = null;
	private OnInputDialogParameter m_onInputDialogParameter = null;

	private View.OnClickListener m_onClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			int viewID = v.getId();
			if(viewID == R.id.button_Yes)
			{
				if (m_onInputDialogListener != null)
				{
					String strOutput = "";
					if ( m_editInput != null ){
						strOutput = m_editInput.getText().toString(); //hill 2014/7/8 
					}
					m_onInputDialogListener.onInputDialog_Confirm( strOutput );
				}
				dismiss();
			}
			else if(viewID == R.id.button_No)
			{
				if (m_onInputDialogListener != null)
				{
					m_onInputDialogListener.onInputDialog_Cancel();
				}
				dismiss();
			}
		}
	};

	public InputDialog(Context context, OnInputDialogParameter onInputDialogParameter, OnInputDialogListener onInputDialogListener)
	{
        super(context, R.style.dialog);
		setCancelable(false);	//back 不可關閉
		setCanceledOnTouchOutside(false);	//點擊外面dialog不會dismiss
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//不顯示沒有title

		m_onInputDialogParameter= onInputDialogParameter;
		m_onInputDialogListener = onInputDialogListener;
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
	/**
	 * 設定EditText顯示的內容為*號
	 */
	public void uiSetEditText(String strInput) {
        m_strInput = strInput;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_layout_dialog_input);

		UIDefine uIDefine = UIDefine.getInstance(getContext());

        uIDefine.setLayoutParams(findViewById(R.id.linearLayout_Root));

		TextView textView_Title = (TextView)findViewById(R.id.textView_Title);
		textView_Title.setText(m_strTitle);
        uIDefine.setTextSizeAndLayoutParams(textView_Title);

        uIDefine.setLayoutParams(findViewById(R.id.imageView_Line));

		TextView textView_Content = (TextView)findViewById(R.id.textView_Content);
        uIDefine.setTextSizeAndLayoutParams(textView_Content);
		//設定如果內容textView是空的就設為GONE 就不會空一塊位置
		if (m_strContent != null) {
			textView_Content.setText(m_strContent);
		} else {
			textView_Content.setVisibility(View.GONE);
		}

		m_editInput = (EditText)findViewById(R.id.editText_Input);
        uIDefine.setTextSizeAndLayoutParams(m_editInput);
        m_editInput.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        m_editInput.setSingleLine(false);
        m_editInput.setHorizontallyScrolling(false);
        m_editInput.setGravity(Gravity.TOP);
        if(m_strInput != null && !m_strInput.isEmpty())
        {
            m_editInput.setText(m_strInput);
            m_editInput.setSelection(m_strInput.length());
        }

        uIDefine.setLayoutParams(findViewById(R.id.relativeLayout_Button));

		Button button;
		button = (Button) findViewById(R.id.button_Yes);
        uIDefine.setTextSizeAndLayoutParams(button);
		
		if (m_strConfirm != null) {
			button.setOnClickListener(m_onClickListener);
			button.setText(m_strConfirm);
		}else {
			button.setVisibility(View.GONE);
		}
		TextViewChangeListenr.vSetTextViewSizeWithSpecficWidth(uIDefine, button, button.getLayoutParams().width, UIDefine.FontSize_15u, UIDefine.FontSize_10u);

		button = (Button) findViewById(R.id.button_No);
        uIDefine.setTextSizeAndLayoutParams(button);
		if (m_strCancel != null) {
			button.setOnClickListener(m_onClickListener);
			button.setText(m_strCancel);
		}else {
			button.setVisibility(View.GONE);
		}
		TextViewChangeListenr.vSetTextViewSizeWithSpecficWidth(uIDefine, button, button.getLayoutParams().width, UIDefine.FontSize_15u, UIDefine.FontSize_10u);
	}

}
