package object;


import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


/**
 * 動態改變「TextView」的文字大小
 *  * <br>2014-06-12 調整為mBroker的字型定義模式，modify by ryan
 * @author kai-yu
 */
public class TextViewChangeListenr implements View.OnLayoutChangeListener
{
	
	public interface TextViewChangeListenrInterface
	{
		void onChangeSymbolInteger(Integer _iTextAvailableWidth);
	}
	
	public final static Double[] FONT_SIZE_DEFINE = {	
														UIDefine.FontSize_8u,
                                                        UIDefine.FontSize_9u,
														UIDefine.FontSize_10u,
														UIDefine.FontSize_11u,
														UIDefine.FontSize_12u,
														UIDefine.FontSize_13u,
														UIDefine.FontSize_14u,
														UIDefine.FontSize_15u,
														UIDefine.FontSize_16u,
														UIDefine.FontSize_18u,
														UIDefine.FontSize_20u,
														UIDefine.FontSize_23u,
														UIDefine.FontSize_26u,
														UIDefine.FontSize_30u };
	
	private TextView	m_tv					= null;
	private String		m_strShowString			= null;
	private double 		m_dFontdefineSize		= 0.0;
	private int 		m_nAvailablewidth		= 0;
	private Integer 	m_iTextAvailableWidth	= null;
	//private UIDefine	m_uidefine			= null;
	
	private TextViewChangeListenrInterface m_onChangeSymbolInterface = null;
	
	
	public TextViewChangeListenr(TextView _textView, double _dFontDefineSize, Integer _iTextAvailableWidth, TextViewChangeListenrInterface _onChangeSymbolInterface, UIDefine _uidefine ) 
	{
		m_tv = _textView;
		m_strShowString = _textView.getText().toString();
		m_dFontdefineSize = _dFontDefineSize;
		m_iTextAvailableWidth = _iTextAvailableWidth;
		m_onChangeSymbolInterface = _onChangeSymbolInterface;
		
		//增加計算字形定義
		//m_uidefine = _uidefine;
	}
	
	
	@Override
	public void onLayoutChange(View v, int left, int top, int right,
			int bottom, int oldLeft, int oldTop, int oldRight,
			int oldBottom) 
	{
		v.removeOnLayoutChangeListener(this);
		if ( m_strShowString == null || m_tv == null || m_dFontdefineSize == 0 )
		   return;

		//計算有效寬度
		m_nAvailablewidth = right - left - m_tv.getCompoundPaddingLeft() - m_tv.getCompoundPaddingRight();
	   
		m_iTextAvailableWidth = Integer.valueOf(m_nAvailablewidth);
		//marked by snoykuo 2015/04/26 這裡應該不要改字型大小，用的地方自己呼叫vSetTextViewSizeWithSpecficWidth
		//vSetTextViewSizeWithSpecficWidth(m_uidefine, m_tv, m_iTextAvailableWidth, m_dFontdefineSize ,UIDefine.FontSize_13u);	//wanru :
		if (m_onChangeSymbolInterface != null)
		{
			m_onChangeSymbolInterface.onChangeSymbolInteger(m_iTextAvailableWidth);
		}  
	}
	
	/**
	 * 計算TV內容寬度囉<p/>
	 * _tv.getWidth()常為0，_iDefaultW不要填_tv.getWidth()啦
	 * @author snoykuo
	 * */
	public static int iGetAvailableWidth(TextView _tv, int _iDefaultW)
	{
		int iRet=0;
		
		if (null==_tv || 0==_tv.getWidth())
		{
			//aLog.w("RDLOG", "[TVCL][iGetAvailableWidth]_iDefaultW="+_iDefaultW);
			return _iDefaultW;
		}
		//計算有效寬度
		int iW=0, iCPL=0, iCPR=0;
		iW = _tv.getWidth();
		iCPL = _tv.getCompoundPaddingLeft();
		iCPR = _tv.getCompoundPaddingRight();
		
//		int iPL=0, iPR=0,  iCDP=0, iCDRW=0, iCDLW=0;
//		
//		iPL = _tv.getPaddingLeft();
//		iPR = _tv.getPaddingRight();
//		iCDP = _tv.getCompoundDrawablePadding();
//		
//		if (_tv.getCompoundDrawables() != null)
//		{
//			Drawable[] drawables = _tv.getCompoundDrawables(); //為了漲跌小圖 snoykuo 2015/04/26
//			if (drawables[2]!=null && drawables[2].getBounds()!=null)
//				iCDRW = drawables[2].getBounds().width();
//			if (drawables[0]!=null && drawables[0].getBounds()!=null)
//				iCDLW = drawables[0].getBounds().width();
//		}
		
		iRet = iW - iCPL - iCPR;//-iCDP - iCPL - iCPR -iCDRW -iCDLW;
		
//		aLog.i("RDTest", String.format("[TVCL][iGetAvailableWidth]iRet=%d, _iDefaultW=%d, iW=%d, iPL=%d, iPR=%d, iCDP=%d, iCPL=%d, iCPR=%d, iCDRW=%d, iCDLW=%d", 
//				iRet, _iDefaultW, iW, iPL, iPR, iCDP, iCPL, iCPR, iCDRW, iCDLW));
		return iRet;
	}
	
	/**
	 * @param _cAvailableWidth
	 * @param _tv: TextView
	 * @author wanru
	 */
	public static void vSetTextViewSizeWithSpecficWidth( UIDefine _uidefine, TextView _tv, final Integer _cAvailableWidth, double _dFontDefineSize, double _dFontMinSize )
	{
	   if (_tv == null || _dFontDefineSize == 0 || _cAvailableWidth == null || _cAvailableWidth.intValue() == 0)
		   return;
 	   
	   // 由於 measureText 不能為 null，所以先判斷取出的字串是否為 null
	   String ShowString = _tv.getText().toString();
 	   if (ShowString == null)
 		   return;

 	   List<Double> lst = Arrays.asList(FONT_SIZE_DEFINE);
 	   int nIdx = lst.indexOf(_dFontDefineSize);
 	   
	   int iAvailableWidth = _cAvailableWidth.intValue();
	   _tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, _uidefine.getTextSize(_dFontDefineSize));
	   Paint paint = _tv.getPaint();
	   
	   //wanru :判斷是否分行 ============================================
	   String[] AfterSplit = ShowString.split("\n");
	   int iLineCount =0;
	   int iMaxIndex=-1;
	   float fMaxLength = 0;
	   iLineCount = AfterSplit.length;
	   for (int i = 0; i < iLineCount; i++) {
		   if (paint.measureText(AfterSplit[i]) > fMaxLength){
			   iMaxIndex = i;
			   fMaxLength = paint.measureText(AfterSplit[i]);
		   }
	   }
	   if (iMaxIndex > -1 && iLineCount>1){
		   ShowString = AfterSplit[iMaxIndex].toString();
	   }
	   //wanru 須知!:目前元件只判斷寬,未判斷高度,當文字超出textview的高時,應縮小文字
	   // =============================================================
	   float txtwidth = paint.measureText(ShowString);
	   
	   while (txtwidth > iAvailableWidth)
	   {
		   if ( nIdx < 0 ){
			   break;
		   }
		   _dFontDefineSize = FONT_SIZE_DEFINE[nIdx];
		   if (_dFontDefineSize < _dFontMinSize){
			   break;
		   }
		   _tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, _uidefine.getTextSize(_dFontDefineSize));
		   txtwidth = paint.measureText(ShowString);

		   nIdx--;
	   }	
	}
	
};
