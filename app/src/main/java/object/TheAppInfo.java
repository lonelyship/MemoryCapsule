package object;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用來存取APP會用到的設定參數
 */
public class TheAppInfo {

    //SharedPreferences檔名定義
    private static final String FILE_NAME_SHARED_PREFERENCES = "TheAppInfo";

    private static TheAppInfo m_instance = null;
    private SharedPreferences m_sharedPreferences = null;

    private String	m_strVersionName = "1.0.0";			//版本名
    private String	m_strVersionFullName = "1.0.0 R01";	//完整版本名
    private String	m_strFilePath = "";					//外部儲存空間路徑


    //SharedPreferences 欄位定義
    /**要用哪種動畫效果(0~16)*/		private static final String KEY_Animation_Position		= "KEY_Animation_Position";
    /**要用哪種語音辬識(0~2)*/		private static final String KEY_VoiceLanguage_Position	= "KEY_VoiceLanguage_Position";
    /**自動播放時幾秒換一張(1~7)*/	private static final String KEY_Animation_Duration		= "KEY_Animation_Duration";
    /**是否啟用自動播放*/		    private static final String KEY_Animation_Is_Auto_Play	= "KEY_Is_Auto_Play";

    //SharedPreferences 欄位預設值
    /**要用哪種動畫效果預設值*/		private static final int DEF_Animation_Position	= 0;
    /**要用哪種語音辬識預設值*/		private static final int DEF_VoiceLanguage_Position	= 0;
    /**自動播放時幾秒換一張預設值*/	private static final int DEF_Animation_Duration	= 3;
    /**是否啟用自動播放預設值*/     private static final boolean DEF_Animation_IsAutoPlay = true;

    public static TheAppInfo getInstance (Context context) {

        if (m_instance == null) {

            m_instance = new TheAppInfo();
            m_instance.initial(context);
        }

        return m_instance;
    }

    private void initial (Context context) {

        m_sharedPreferences = context.getSharedPreferences(FILE_NAME_SHARED_PREFERENCES,Context.MODE_PRIVATE);

        //設定「外部儲存空間路徑」，一般為「(SD卡路徑)/Android/data/(PackageName)/files」
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

            m_strFilePath = null;
            m_strFilePath = context.getExternalFilesDir(null).toString();

            if (m_strFilePath == null || m_strFilePath.equals("")) {
                // 組成getExternalFilesDir(…)需取到的路徑
                m_strFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "files";
            }

        }
    }

    /**@return 取得「外部儲存空間路徑」*/
    protected String getFilePath() {
        return m_strFilePath;
    }

    /**
     * 一般為「(外部儲存空間路徑)/(加密過的使用者登入ID)」，<p>
     * 其(使用者登入ID)請參考 setUserLoginID(…)。<p>
     * @return 取得「使用者相關檔案」路徑
     */
    public String getUserFilesPath(String strDir,String strDate)
    {
//        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyyMMdd");
//        String currentDate = sdf_Date.format(new Date()); //加入當入日期資料夾

        String strFileDir = getFilePath() + File.separator + strDir + File.separator + strDate + File.separator;
        File file = new File(strFileDir);
        file.mkdirs();
        file = null;
        return strFileDir;
    }


    /**@param iPosition 設定何種動畫(位置)*/
    public void uiSetAnimationPosition (int iPosition) {
        m_sharedPreferences.edit().putInt(KEY_Animation_Position,iPosition).commit();
    }

    /**@return int 取得要用何種動畫(位置)*/
    public int uiGetAnimationPosition() {
        if (null == m_sharedPreferences) {
            return DEF_Animation_Position;
        }
        return m_sharedPreferences.getInt(KEY_Animation_Position,DEF_Animation_Position);
    }

    /**@param iPosition 設定何種辬識語言(位置)*/
    public void uiSetVoiceLanguagePosition (int iPosition) {
        m_sharedPreferences.edit().putInt(KEY_VoiceLanguage_Position,iPosition).commit();
    }

    /**@return int 取得要用何種辬識語言(位置)*/
    public int uiGetVoiceLanguagePosition() {
        if (null == m_sharedPreferences) {
            return DEF_VoiceLanguage_Position;
        }
        return m_sharedPreferences.getInt(KEY_VoiceLanguage_Position,DEF_VoiceLanguage_Position);
    }

    /**@param iDuration 設定自動播放動畫幾秒換一張*/
    public void uiSetAnimationDuration (int iDuration) {
        m_sharedPreferences.edit().putInt(KEY_Animation_Duration,iDuration).commit();
    }

    /**@return int 取得自動播放動畫幾秒換一張*/
    public int uiGetAnimationDuration() {
        if (null == m_sharedPreferences) {
            return DEF_Animation_Duration;
        }
        return m_sharedPreferences.getInt(KEY_Animation_Duration,DEF_Animation_Duration);
    }

    /**@param bIsAutoPlay 設定是否自動播放*/
    public void uiSetAnimationIsAutoPlay (boolean bIsAutoPlay) {
        m_sharedPreferences.edit().putBoolean(KEY_Animation_Is_Auto_Play, bIsAutoPlay).commit();
    }

    /**@return boolean 取得是否自動播放*/
    public boolean uiGetAnimationIsAutoPlay() {
        if (null == m_sharedPreferences) {
            return DEF_Animation_IsAutoPlay;
        }
        return m_sharedPreferences.getBoolean(KEY_Animation_Is_Auto_Play, DEF_Animation_IsAutoPlay);
    }

}
