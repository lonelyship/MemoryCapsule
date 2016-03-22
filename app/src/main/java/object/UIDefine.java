package object;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lonelyship.Main.R;

public class UIDefine
{
	//字型定義，內容需同 mbkui_mlayout_templates 字型定義
	/**字型「8U」定義*/	public static final double FontSize_8u	= 8;
    /**字型「8U」定義*/	public static final double FontSize_9u	= 9;
	/**字型「10U」定義*/	public static final double FontSize_10u	= 10;
	/**字型「11U」定義*/	public static final double FontSize_11u	= 11;
	/**字型「12U」定義*/	public static final double FontSize_12u	= 12;
	/**字型「13U」定義*/	public static final double FontSize_13u	= 13;
	/**字型「14U」定義*/	public static final double FontSize_14u	= 14;
	/**字型「15U」定義*/	public static final double FontSize_15u	= 15;	
	/**字型「16U」定義*/	public static final double FontSize_16u	= 16;
	/**字型「18U」定義*/	public static final double FontSize_18u	= 18;
	/**字型「20U」定義*/	public static final double FontSize_20u	= 20;
	/**字型「23U」定義*/	public static final double FontSize_23u	= 23;
    /**字型「26U」定義*/	public static final double FontSize_26u	= 26;
	/**字型「30U」定義*/	public static final double FontSize_30u	= 30;

	private static UIDefine m_instance = null;

	private int		m_iMaxLayoutHeightWeight = 0;	//長度單位最大值
	private int		m_iMaxLayoutWidthWeight = 0;	//寬度單位最大值
	private float	m_fHeightUnit = 0;				//長度單位
	private float	m_fWidthUnit = 0;				//寬度單位
	private float	m_fFontUnit = 0;				//字型單位
	private float	m_fBigUnit = 0;				//大單位
	private float	m_fSmallUnit = 0;				//小單位

	private float m_fScreenScaleDensity = 0; //螢幕密度

	private DisplayMetrics m_displayMetrics = null; //取的螢幕解析度

	public static UIDefine getInstance(Context context)
	{
		if (null == m_instance) {
			m_instance = new UIDefine();
			m_instance.initial(context);
		}

		return m_instance;
	}

	public void initial(Context context)
	{
		//設定「長度、寬度單位」最大值
		m_iMaxLayoutHeightWeight = context.getResources().getInteger(R.integer.ui_integer_activity_weight_sum_vertical);
		m_iMaxLayoutWidthWeight = context.getResources().getInteger(R.integer.ui_integer_activity_weight_sum_horizontal);

		//設定「長度、寬度、字型單位」
		m_displayMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(m_displayMetrics);
		m_fHeightUnit = (float) m_displayMetrics.heightPixels / m_iMaxLayoutHeightWeight;
		m_fWidthUnit = (float) m_displayMetrics.widthPixels / m_iMaxLayoutWidthWeight;
		m_fBigUnit = Math.max(m_fHeightUnit, m_fWidthUnit); //大U
		m_fSmallUnit = Math.min(m_fHeightUnit, m_fWidthUnit); //小U
		m_fFontUnit = m_fBigUnit;

		m_fScreenScaleDensity = m_displayMetrics.scaledDensity; //snoykuo 2014/05/02
	}
	/**@return 取出經長寬比運算出的高度*/
	public DisplayMetrics getDisplayMetrics() {
		return m_displayMetrics;
	}
	/**
	 * @return 螢幕密度
	 * @author snoykuo
	 */
	public float getScaleDensity() {
		return m_fScreenScaleDensity;
	}
	/**
	 * @param dWeight 高度比例，參數範圍為「1 ~ 480」
	 * @return 取出經長寬比運算出的高度
	 */
	public int getLayoutHeight(double dWeight) {
		return (int) (dWeight * m_fHeightUnit);
	}
	/**
	 * @param dWeight 寬度比例，參數範圍為「1 ~ 270」
	 * @return 取出經長寬比運算出的寬度
	 */
	public int getLayoutWidth(double dWeight) {
		return (int) (dWeight * m_fWidthUnit);
	}
	/**
	 * @param dTextSizeDefine 參數範圍為「{@link UIDefine#FontSize_10u FontSize_10u} ~ {@link UIDefine#FontSize_30u FontSize_30u}」
	 * @param iTextLength 總行數
	 * @return 取出經長寬比運算出的總行高度
	 */
	public int getLayoutHeightByTextSize(double dTextSizeDefine, int iTextLength)
	{
		if (iTextLength <= 0) {
			return 0;
		}

		int iLayoutHeight = getTextSize(dTextSizeDefine) * iTextLength;
		int iMaxLayoutHeight = getLayoutHeight(m_iMaxLayoutHeightWeight);
		if (iMaxLayoutHeight <= iLayoutHeight) {
			return iMaxLayoutHeight;
		}

		return iLayoutHeight;
	}
	/**
	 * @param dTextSizeDefine 參數範圍為「{@link UIDefine#FontSize_10u FontSize_10u} ~ {@link UIDefine#FontSize_30u FontSize_30u}」
	 * @param iTextLength 總字數
	 * @return 取出經長寬比運算出的總字寬度
	 */
	public int getLayoutWidthByTextSize(double dTextSizeDefine, int iTextLength)
	{
		if (iTextLength <= 0) {
			return 0;
		}

		int iLayoutWidth = getTextSize(dTextSizeDefine) * iTextLength;
		int iMaxLayoutWidth = getLayoutWidth(m_iMaxLayoutWidthWeight);
		if (iMaxLayoutWidth <= iLayoutWidth) {
			return iMaxLayoutWidth;
		}

		return iLayoutWidth;
	}
	/**
	 * 取出大U大小
	 */
	@SuppressWarnings("unused")
	private int getBigUSize(double dSizeDefine) {
		return (int) (dSizeDefine * m_fBigUnit);
	}
	/**
	 * 取出小U大小
	 */
	private int getSmallUSize(double dSizeDefine) {
		return (int) (dSizeDefine * m_fSmallUnit);
	}
	/**
	 * @param dTextSizeDefine 參數範圍為「{@link UIDefine#FontSize_10u FontSize_10u} ~ {@link UIDefine#FontSize_30u FontSize_30u}」
	 * @return 取出字型大小
	 */
	public int getTextSize(double dTextSizeDefine) {
		return (int) (dTextSizeDefine * m_fFontUnit);
	}
	/**
	 * 設定「字型」以及「LayoutParams」
	 * @param textView 要設定的 TextView
	 * @author kai-yu
	 */		
	public void setTextSizeAndLayoutParams(TextView textView) {
		setLayoutParams(textView);
		setTextSize(textView.getTextSize(), textView);		
	}
	/**
	 * 設定「字型」以及「LayoutParams」
	 * @param button 要設定的 Button
	 * @author kai-yu
	 */		
	public void setTextSizeAndLayoutParams(Button button) {
		setLayoutParams(button);
		setTextSize(button.getTextSize(), button);		
	}
	/**
	 * 設定「字型」以及「LayoutParams」
	 * @param button 要設定的 RadioButton
	 * @author kai-yu
	 */		
	public void setTextSizeAndLayoutParams(RadioButton button) 
	{		
		setLayoutParams(button);
		setTextSize(button.getTextSize(), button);		
	}
	/**
	 * 設定「字型」以及「LayoutParams」<br/>
	 * 有鈕字分離的RadioButton設定，為了4.1手機...<br/>
	 * 反正4.1就是要設PaddingLeft
	 * @param button 要設定的 RadioButton
	 * @author snoykuo
	 */		
	public void setTextSizeAndLayoutParams(RadioButton button, boolean bHasLeftCheckBtn) 
	{
		if (!bHasLeftCheckBtn)
		{
			setTextSizeAndLayoutParams(button);
			return;
		}
		//http://stackoverflow.com/questions/2134591/add-margin-between-a-radiobutton-its-label-in-android
		String sVer = VERSION.RELEASE;
		
		if (sVer.compareTo("4.2")>=0) //4.2以上paddingLeft從button右側起算
		{
			//aLog.i("RDTest", "[MBkUIDefine][setTextSizeAndLayoutParams(RadioButton)4.2]sVer="+sVer);
		}
		else if (sVer.compareTo("4.1")>=0) //4.1以上paddingLeft從button左側起算	
		{
			//aLog.i("RDTest", "[MBkUIDefine][setTextSizeAndLayoutParams(RadioButton)4.1]sVer="+sVer);
			int iH = button.getLayoutParams().height;

			if (iH>0)
			{
				button.setPadding(button.getPaddingLeft()+iH-1, //先當成這樣吧,喵的4.1
								  button.getPaddingTop(), 
								  button.getPaddingRight(), 
								  button.getPaddingBottom());
			}
		}	
		//else
			//aLog.i("RDTest", "[MBkUIDefine][setTextSizeAndLayoutParams(RadioButton)]sVer="+sVer);
				
		setLayoutParams(button);
		setTextSize(button.getTextSize(), button);		
	}	
	/**
	 * 設定「字型」以及「LayoutParams」
	 * @param editText 要設定的 EditText
	 * @author kai-yu
	 */		
	public void setTextSizeAndLayoutParams(EditText editText) {
		setLayoutParams(editText);
		setTextSize(editText.getTextSize(), editText);		
	}

	/**
	 * 設定字型
	 * @param dTextSizeDefine 參數範圍為「{@link UIDefine#FontSize_10u FontSize_10u} ~ {@link UIDefine#FontSize_30u FontSize_30u}」
	 * @param textView
	 */
	public void setTextSize(double dTextSizeDefine, TextView textView) {
		textView.setIncludeFontPadding(false);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(dTextSizeDefine));
	}
	/**
	 * 設定字型
	 * @param dTextSizeDefine 參數範圍為「{@link UIDefine#FontSize_10u FontSize_10u} ~ {@link UIDefine#FontSize_30u FontSize_30u}」
	 * @param radioButton
	 */
	public void setTextSize(double dTextSizeDefine, RadioButton radioButton) {
		radioButton.setIncludeFontPadding(false);
		radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(dTextSizeDefine));
	}	
	/**
	 * 設定字型
	 * @param dTextSizeDefine 參數範圍為「{@link UIDefine#FontSize_10u FontSize_10u} ~ {@link UIDefine#FontSize_30u FontSize_30u}」
	 * @param button
	 */
	public void setTextSize(double dTextSizeDefine, Button button) {
		button.setIncludeFontPadding(false);
		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(dTextSizeDefine));
	}
	/**
	 * 設定字型
	 * @param dTextSizeDefine 參數範圍為「{@link UIDefine#FontSize_10u FontSize_10u} ~ {@link UIDefine#FontSize_30u FontSize_30u}」
	 * @param editText
	 */
	public void setTextSize(double dTextSizeDefine, EditText editText) {
		editText.setIncludeFontPadding(false);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(dTextSizeDefine));
	}
	/**
	 * 設定「LayoutParams」，包含「width」、「height」等參數
	 * @param view 要設定 LayoutParams 的 View
	 * @author WaterMelon add 2014/09/30
	 */
	public void setLayoutParams_Width_Height(View view)
	{
		int iW=view.getLayoutParams().width, 
			iH=view.getLayoutParams().height;
		
		if (iW != 0 &&
			iW != ViewGroup.LayoutParams.MATCH_PARENT && 
			iW != ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			if (iW==iH) //正方形用小單位
				view.getLayoutParams().width = getSmallUSize(iW);
			else
				view.getLayoutParams().width = getLayoutWidth(iW);	
		}

		if (iH != 0 &&
			iH != ViewGroup.LayoutParams.MATCH_PARENT && 
			iH != ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			if (iW==iH) //正方形用小單位
				view.getLayoutParams().height = getSmallUSize(iH);	
			else
				view.getLayoutParams().height = getLayoutHeight(iH);	
		}	
	}
	/**
	 * 設定「LayoutParams」，包含「width」、「height」、「Margins」、「Padding」等參數
	 * @param view 要設定 LayoutParams 的 View
	 * @author kai-yu
	 */
	public void setLayoutParams(View view)
	{

		setLayoutParams_Width_Height(view);

		if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
		{
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
			params.setMargins(
					getLayoutWidth(params.leftMargin),
					getLayoutHeight(params.topMargin), 
					getLayoutWidth(params.rightMargin),
					getLayoutHeight(params.bottomMargin));
		}

		view.setPadding(
				getLayoutWidth(view.getPaddingLeft()),
				getLayoutHeight(view.getPaddingTop()),
				getLayoutWidth(view.getPaddingRight()),
				getLayoutHeight(view.getPaddingBottom()));		
		
		if (view instanceof TextView){
			TextView tv = (TextView) view;
			tv.setCompoundDrawablePadding(getLayoutWidth(tv.getCompoundDrawablePadding()));
		}
	}	
}
