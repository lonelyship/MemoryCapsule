package com.lonelyship.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lonelyship.Main.R;

public class AboutMeDialog extends Dialog implements OnClickListener
{
	private Button              m_confirmButton = null;
	private TextView            m_contentTextView = null, m_tvTtile=null;

	private String              m_strContentAsset = null, m_sTitle=null;
	//private MBkUIDefine		m_mBkUIDefine;
	private int m_iW=0, m_iH=0; 

	public AboutMeDialog(Activity a)
	{
        super(a);
//		super(a, TheAppInfo.getInstance(a));
//		m_mBkUIDefine = MBkUIDefine.getInstance(a);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//不顯示沒有title
	}

	public void setAssetContent(String strContent)
	{
		m_strContentAsset = strContent;
	}    

	public void vSetTitle(String sTitle)
	{
		m_sTitle = sTitle;
	}
	private void vSetSizeOnCreate()
	{
		if (0==m_iW || 0==m_iH)
			return;

		LinearLayout ll = (LinearLayout)findViewById(R.id.layout_root);
		if (ll==null)
			return;

		LayoutParams para = ll.getLayoutParams();
		if (para==null)
			para = new LayoutParams(m_iW,m_iH);
		else
		{
			para.width = m_iW;
			para.height = m_iH;
		}
		ll.setLayoutParams(para);
	}
	public void vSetSize(int iW, int iH)
	{
		m_iW = iW;
		m_iH = iH;
	}

	@Override //第一次show時才會進來
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.dialog_about_me);
		this.setCancelable(false);

		m_confirmButton = (Button) findViewById(R.id.button_Confirm);
		m_confirmButton.setOnClickListener(this);
		//m_confirmButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_mBkUIDefine.getTextSize(MBkUIDefine.FontSize_14u));//TheApp.getTheApp().vGetObject().GetQuoteFontSize());

		m_contentTextView = (TextView) findViewById(R.id.textView_Content);
		m_contentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		//m_contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_mBkUIDefine.getTextSize(MBkUIDefine.FontSize_14u));

		m_tvTtile = (TextView) findViewById(R.id.tv_Title);
		//m_tvTtile.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_mBkUIDefine.getTextSize(MBkUIDefine.FontSize_14u));
		vSetSizeOnCreate();
	}

	@Override
	public void onStart()
	{
		m_contentTextView.setText(m_strContentAsset);
		m_contentTextView.scrollTo(0, 0);//回到最上 snoykuo 2013/08/20

		if (m_sTitle!=null)
			m_tvTtile.setText(m_sTitle);

		super.onStart();
	}

	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.button_Confirm)
		{
			this.dismiss();
			return;
		}
	}

}