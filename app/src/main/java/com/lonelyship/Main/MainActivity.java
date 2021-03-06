package com.lonelyship.Main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.lonelyship.Adapter.TimePagerAdapter;
import com.lonelyship.ArcMenu.ArcMenu;
import com.lonelyship.CardsSwiped.CardsSwipedActivity;
import com.lonelyship.CommonUtils.DiskUtils;
import com.lonelyship.Dialog.InputDialog;
import com.lonelyship.Dialog.SelectModeDialog;
import com.lonelyship.Dialog.SettingDialog;
import com.lonelyship.Fragment.PhotoFragment;
import com.lonelyship.Fragment.ProgressFragment;
import com.lonelyship.Fragment.TimeFragment;
import com.lonelyship.bean.Folder;
import com.lonelyship.bean.Image;
import com.lonelyship.transforms.FlipVerticalTransformer;
import com.lonelyship.utils.AutoScrollTextView;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import object.TheAppInfo;
import object.UIDefine;

public class MainActivity extends BaseActivity {

    public static final String BUNDEL_KEY_LOGIN_TYPE = "BUNDEL_KEY_LOGIN_TYPE";

    public static final String BUNDEL_KEY_NAME = "BUNDEL_KEY_NAME";
    public static final String BUNDEL_KEY_EMAIL = "BUNDEL_KEY_EMAIL";
    public static final String BUNDEL_KEY_URL = "BUNDEL_KEY_URL";

    public static final String BUNDEL_KEY_FB_TOKEN = "BUNDEL_KEY_FB_TOKEN";

    public final String dialogTag = "CALDROID_DIALOG_FRAGMENT";

    /**
     * 照片
     */
    public static final int Flag_Fragment_Photo = 0x0100;
    /**
     * 地圖
     */
    public static final int Flag_Fragment_Map = 0x0101;
    /**
     * 錄音
     */
    public static final int Flag_Fragment_Sound_Recording = 0x0102;
    /**
     * 錄影
     */
    public static final int Flag_Fragment_Video = 0x0103;
    /**
     * 轉圈
     */
    public static final int Flag_Fragment_Progress = 0x0104;


    /**
     * 側邊選單-開啟地圖
     */
    public static final int Flag_MainMenu_GoogleMap = 1;

    /**
     * 側邊選單-圖卡模式
     */
    public static final int Flag_MainMenu_CardSwiped = 3;
    /**
     * 側邊選單-相關設定
     */
    public static final int Flag_MainMenu_Setting = 4;
    /**
     * 側邊選單-使用者登出離開
     */
    public static final int Flag_MainMenu_Exit = 5;

    /**
     * 相機intent的RESULT
     */
    public static final int RESULT_CAMERA = 2;

    /**
     * 語音intent的RESULT
     */
    public static final int RESULT_SPEECH = 1;

    private String m_ListTitles[] = {"開啟地圖", "雲端備份", "圖卡模式", "相關設定", "使用者登出"};

    private ViewPager m_viewPager_time;
    private TimePagerAdapter m_pagerAdapter_time;
    private Context m_context = null;
    private View coordinatorLayoutView = null;
    private int m_iMainMenuViewFlag = 0;
    private String m_strMainMenuViewTag = "";

    private ProgressDialog m_progressDialog = null;

    private AutoScrollTextView tv_marquee = null;
    private LinearLayout linearLayout_up  = null;
    private LinearLayout linearLayout_middle  = null;
    private LinearLayout linearLayout_bottom = null;
    private ImageButton img_btn_photo = null;

    private UIDefine m_UIDefine = null;

    private int m_ListIcons[] = {R.drawable.ic_announcement_black, R.drawable.ic_backup_black, R.drawable.ic_list_black, R.drawable.ic_settings_applications_black, R.drawable.ic_settings_applications_black};
    private String m_sUserTitle = "使用者";
    private String m_sUserMail = "XXXX@gmail.com.tw";
    //private int m_iUserIcon = R.drawable.flashwolves;
    private Bitmap m_iUserIcon = null;

    private RecyclerView m_MainMenuRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.Adapter m_MainMenuRecyclerViewAdapter;            // Declaring Adapter For Recycler View
    private RecyclerView.LayoutManager m_RecyclerViewLayoutManager;// Declaring Layout Manager as a linear layout manager
    private DrawerLayout m_DrawerLayout;                           // Declaring DrawerLayout
    private ActionBarDrawerToggle m_DrawerToggle;                  // Declaring Action Bar m_DrawerLayout Toggle

    private boolean isFirstTime  = true;
    private boolean isFromCamera  = false;
    /**
     * ViewPager預設動畫的位置
     */
    private int m_iAnimationPosition;
    /**
     * ViewPager預設自動播放每張持續的時間(秒)
     */
    private int m_iPlayDuration;
    /**
     * ViewPager預設是否自動播放
     */
    private boolean m_bIsAutoPlay;

    public static String m_strFocusDay = "";

    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    private boolean hasFolderGened = false;

    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<>();
    // 结果数据
    private ArrayList<String> resultList = new ArrayList<>();

    public static HashMap<String, ArrayList<String>> photoMap = new HashMap<String, ArrayList<String>>();

    private SimpleDateFormat m_formatter = new SimpleDateFormat("yyyyMMdd");
    private Date m_selectedDate = null;
    private Bundle m_saveInstance = null;

    private static long day_from_Today = 0;

    private static ArcMenu arcMenu = null;

    public static Handler m_handler;

    static {
        m_handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 1:
                        if (CurrentPhotoFragment != null) {

                            // changeViewByReplace(Flag_Fragment_Photo);

                            CurrentPhotoFragment.initAdapter();
                            Log.e("lonelyship", "收到通知initAdapter");
                        }
                        break;
                    case 2:
                        if (arcMenu.getVisibility() == View.VISIBLE)
                        {
                            arcMenu.setVisibility(View.GONE);
                        }
                        else
                        {
//                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                            params.leftMargin = (int) msg.getData().getFloat("X"); //XCOORD
//                            params.topMargin = (int) msg.getData().getFloat("Y"); //YCOORD
//                            arcMenu.setLayoutParams(params);
//                            arcMenu.setX();
//                            arcMenu.setY();
                            arcMenu.setVisibility(View.VISIBLE);
                        }
                        break;
                }

            }
        };
    }


    private static final int[] ITEM_DRAWABLES = {R.drawable.arc_menu_composer_camera, R.drawable.arc_menu_composer_music,
            R.drawable.arc_menu_composer_place, R.drawable.arc_menu_composer_sleep, R.drawable.arc_menu_composer_thought, R.drawable.arc_menu_composer_with};

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 橫向
            getSupportActionBar().hide();
            linearLayout_up.setVisibility(View.GONE);
            linearLayout_bottom.setVisibility(View.GONE);
            tv_marquee.setVisibility(View.GONE);

//            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }
        else {
            // 直向
            getSupportActionBar().show();
            linearLayout_up.setVisibility(View.VISIBLE);
            //linearLayout_bottom.setVisibility(View.VISIBLE);
            tv_marquee.setVisibility(View.VISIBLE);

//            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        m_context = this;
        m_saveInstance = savedInstanceState;

        TheAppInfo.getInstance(m_context);//初始化TheAppInfo

        SimpleDateFormat sdf_Date = new SimpleDateFormat("MMMMdd日 yyyy \n" +
                "EEEE");
        String currentDate = sdf_Date.format(new Date()); //加入當入日期資料夾

        m_strFocusDay = currentDate;
        Log.e("lonelyship", "[MainActivity][onCreate]今天:" + currentDate);


        this.getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);

        initalDefalSettings();
        initialActionBar();
        initialLayoutComponent();
        //initialLoginData();
        initalDrawerLayout();
        initialDiary();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            getSupportActionBar().hide();
            linearLayout_up.setVisibility(View.GONE);
            linearLayout_bottom.setVisibility(View.GONE);
            tv_marquee.setVisibility(View.GONE);
        }

        changeViewByReplace(Flag_Fragment_Photo);
    }

    private PhotoFragment.OnPhotoFragmentListener m_onPhotoFragmentListener = new PhotoFragment.OnPhotoFragmentListener() {

        @Override
        public void selectPhoto(String path) {

            if (path.equals("none")) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, RESULT_CAMERA);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //initialCurrentPhotoFragment();    //加這第一次進app照片才會出現
    }

    protected void initialLayoutComponent() {
//        SetActionBarTitleClick();
        m_UIDefine = UIDefine.getInstance(m_context);
        m_viewPager_time = (ViewPager) findViewById(R.id.viewPager_time);
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        tv_marquee = (AutoScrollTextView) findViewById(R.id.tv_marquee);
        //tv_marquee.setSelected(true);
        tv_marquee.init(getWindowManager());
        tv_marquee.startScroll();
        tv_marquee.setOnClickListener(MarqueeOnClickListener);
        linearLayout_up = (LinearLayout) findViewById(R.id.linearLayout_up);
        linearLayout_middle =  (LinearLayout) findViewById(R.id.linearLayout_middle);
        linearLayout_bottom = (LinearLayout) findViewById(R.id.linearLayout_bottom);

        img_btn_photo = (ImageButton) findViewById(R.id.img_btn_photo);
        img_btn_photo.setOnClickListener(TabOnClickListener);


        arcMenu = (ArcMenu) findViewById(R.id.arc_menu);
        initArcMenu(arcMenu, ITEM_DRAWABLES);

        m_viewPager_time.setOnPageChangeListener(m_onPageChangeListener_Time);

        if (null == m_pagerAdapter_time) {//為空，代表是「第一次啟用此 Fragment」或者「非第一次啟用但此 Fragment 被回收」
            if (null != getSupportFragmentManager().getFragments()) {//若 member 為空但 ViewPager 內卻有子 Fragment，代表此 Fragment 被回收
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {//由於 Fragment 被回收後， ViewPager 內雖然留有子 Fragment，但子 Fragment 跟未回收前的子 Fragment 不相同
                    if (null != fragment) {//所以將所有子 Fragment 移除，讓 ViewPager 重新取得子 Fragment
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
            }

            m_pagerAdapter_time = new TimePagerAdapter(getSupportFragmentManager());
        }
        m_viewPager_time.setAdapter(m_pagerAdapter_time);
        m_viewPager_time.setPageTransformer(true, new FlipVerticalTransformer());

        m_pagerAdapter_time.vInitialListStockPagerItem();
        m_pagerAdapter_time.notifyDataSetChanged();
        m_viewPager_time.setCurrentItem(1);
    }

    /**
     * init浮動按鈕
     */
    private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    switch(position)
                    {
                        case 0:
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, RESULT_CAMERA);
                            Toast.makeText(MainActivity.this, "開啟相機", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            changeViewByReplace(Flag_Fragment_Map);
                            Toast.makeText(MainActivity.this, "切換至地圖頁面", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            changeViewByReplace(Flag_Fragment_Photo);
                            Toast.makeText(MainActivity.this, "切換至相片頁面", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                        Intent intent = new Intent(
                                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"說點什麼吧？");
                        switch (TheAppInfo.getInstance(m_context).uiGetVoiceLanguagePosition()) {
                            case 0:
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.TAIWAN.toString());
                                break;
                            case 1:
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString());
                                break;
                            case 2:
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPANESE.toString());
                                break;
                            default:
                                break;
                        }

                        try {
                            startActivityForResult(intent, RESULT_SPEECH);
                            //tv_marquee.setText("");
                        } catch (ActivityNotFoundException a) {
                            Toast t = Toast.makeText(getApplicationContext(),
                                    "Opps! Your device doesn't support Speech to Text",
                                    Toast.LENGTH_SHORT);
                            t.show();
                        }
                            break;
                        case 5:
                            Intent CardsSwipedIntent = new Intent(MainActivity.this, CardsSwipedActivity.class);
                            CardsSwipedIntent.putExtra("m_strFocusDay", m_strFocusDay);
                            startActivity(CardsSwipedIntent);
                            Toast.makeText(MainActivity.this, "切換至圖卡模式", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }

//                    Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 跑馬燈的OnClickListener
     */
    private View.OnClickListener MarqueeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputDialog inputDialog = new InputDialog(m_context, null, onInputDialogListener);
            inputDialog.uiSetTitleText(m_strFocusDay+"的心情紀錄");
            inputDialog.uiSetConfirmText("確定");
            inputDialog.uiSetCancelText("取消");
            if(!tv_marquee.getText().toString().equals(m_context.getString(R.string.default_marquee_hint))) {
                inputDialog.uiSetEditText(tv_marquee.getText().toString());
            }
            inputDialog.show();
        }
    };
    /**
     * 下方Tab的OnClickListener
     */
    private View.OnClickListener TabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.img_btn_photo:
                    SelectModeDialog sd = new SelectModeDialog(m_context, onSelectModeDialogListener);
                    sd.uiSetTitleText("新增圖片");
                    sd.uiSetIvLeftId(R.drawable.folder_photo);
                    sd.uiSetIvRightId(R.drawable.camera);
                    //sd.uiSetConfirmText("確定");
                    sd.uiSetCancelText("關閉");
                    sd.show();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 初始化日記
     */
    public void initialDiary()
    {
        String strDiary = "";
        try {
             strDiary = ReadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(strDiary == null)strDiary = "";
        if(!strDiary.isEmpty())
        {
            tv_marquee.setText(strDiary);
            tv_marquee.init(getWindowManager());
            tv_marquee.startScroll();
        }
        else
        {
            tv_marquee.setText(m_context.getString(R.string.default_marquee_hint));
            tv_marquee.init(getWindowManager());
            tv_marquee.startScroll();

        }
    }
    /**
     * 存文字檔
     * 日記
     */
    synchronized public void SaveFile(String strInput)
    {
        try {
            FileWriter fw = new FileWriter(TheAppInfo.getInstance(m_context).getUserFilesPath("Diary",m_strFocusDay) + m_strFocusDay + ".txt");
            BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
            bw.write((strInput));
            bw.newLine();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 讀文字檔
     * 日記
     */
    synchronized public String ReadFile() throws IOException {
        FileReader fr = new FileReader(TheAppInfo.getInstance(m_context).getUserFilesPath("Diary",m_strFocusDay) + m_strFocusDay + ".txt");
        //將BufferedReader與FileReader做連結
        BufferedReader br = new BufferedReader(fr);
        String readData = "";
        String temp = br.readLine(); //readLine()讀取一整行
        while (temp!=null){
            readData+=temp;
            temp=br.readLine();
        }
       return  readData;
    }

    /**
     * 跑馬燈Dialog的Listener
     */
    InputDialog.OnInputDialogListener onInputDialogListener = new InputDialog.OnInputDialogListener() {
        @Override
        public void onInputDialog_Confirm(String strInput) {
            if (!strInput.isEmpty()) {
                tv_marquee.setText(strInput);
                tv_marquee.init(getWindowManager());
                tv_marquee.startScroll();
                SaveFile(strInput);
            } else {
                tv_marquee.setText(m_context.getString(R.string.default_marquee_hint));
                tv_marquee.init(getWindowManager());
                tv_marquee.startScroll();
                SaveFile("");
                //tv_marquee.setHint(m_context.getString(R.string.default_marquee_hint));
            }
        }

        @Override
        public void onInputDialog_Cancel() {

        }
    };
    /**
     * 上傳照片Dialog的Listener
     */
    SelectModeDialog.OnSelectModeDialogListener onSelectModeDialogListener = new SelectModeDialog.OnSelectModeDialogListener() {
        @Override
        public void onSelectModeDialog_Confirm() {
        }

        @Override
        public void onSelectModeDialog_Cancel() {
        }

        @Override
        public void onSelectModeDialog_SelectLeft() {
            Toast.makeText(m_context, "點擊左邊圖示", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSelectModeDialog_SelectRight() {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, RESULT_CAMERA);
            Toast.makeText(m_context, "點擊右邊圖示", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RESULT_CAMERA:
  //          從相機回來
            if (resultCode == RESULT_OK  && data != null)
            {
                isFromCamera = true;
                String path = "";
                path = getRealPathFromURI(this,data.getData());
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String  exifDate =  exif.getAttribute(ExifInterface.TAG_DATETIME);
                // String Key = TimeUtils.formatPhotoDate(img.path);
                String Key = exifDate;
                if(Key == null)return;
                Key = Key.replace(":","");
                if(Key.length() >= 8)
                {
                    Key = Key.substring(0,8);
                }
                else
                {
                    return;
                }
//                            }
                Log.e("lonelyship", "路徑"+path+"時間"+ Key);
                ArrayList<String> al = new ArrayList<String>();
                if (photoMap.get(Key) == null) {
                    al.add(path);
                    photoMap.put(Key, al);
                    //   photoMap.put(TimeUtils.formatPhotoDate(img.path), )
                } else {
                    al = photoMap.get(Key);
                    al.add(0,path);         //加到最前面
                    photoMap.put(Key, al);
                }

                day_from_Today = 0; //跳到今天

                if(CurrentPhotoFragment != null)CurrentPhotoFragment.showProgressBar();

                changPhotoFragment();
                initialDiary();


//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//                try {
//                SimpleDateFormat sdf_Time = new SimpleDateFormat("HHmmss");
//                String currentTime = sdf_Time.format(new Date());
//
//                m_strFocusDay = getFocusDay();

                    //               this.getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);

//                FileOutputStream out = new FileOutputStream(TheAppInfo.getInstance(m_context).getUserFilesPath("Photo",m_strFocusDay) + currentTime + ".jpg");
//                // Toast.makeText(m_context,getFilePath()+"/filename",Toast.LENGTH_SHORT).show();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);

//                initialCurrentPhotoFragment();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
            }
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if((text.get(0).equals("clear") || text.get(0).equals("清除")))
                    {
                        tv_marquee.setText(m_context.getString(R.string.default_marquee_hint));
                        SaveFile("");
                    }
                    else if(tv_marquee.getText().toString().equals(m_context.getString(R.string.default_marquee_hint)))
                    {
                        tv_marquee.setText(text.get(0));
                        SaveFile(text.get(0));
                    }
                    else
                    {
                        tv_marquee.setText(tv_marquee.getText()+text.get(0));
                        SaveFile(tv_marquee.getText()+text.get(0));
                    }
                    tv_marquee.init(getWindowManager());
                    tv_marquee.startScroll();

                    //vShowSelectWhichWordDialog(text.toArray(new String[text.size()]));//收到語音資料後，彈出給使用者選擇字串的Dialog

//                    if((text.get(0).equals("clear") || text.get(0).equals("清除")))
//                    {
//                        tv_marquee.setText(m_context.getString(R.string.default_marquee_hint));
//                        SaveFile("");
//                    }
//                    else if(tv_marquee.getText().toString().equals(m_context.getString(R.string.default_marquee_hint)))
//                    {
//                        tv_marquee.setText(text.get(0));
//                        SaveFile(text.get(0));
//                    }
//                    else
//                    {
//                        tv_marquee.setText(tv_marquee.getText()+text.get(0));
//                        SaveFile(tv_marquee.getText()+text.get(0));
//                    }
//                    tv_marquee.init(getWindowManager());
//                    tv_marquee.startScroll();
                }
                break;
            }
        }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    static Thread td = null;
    public static boolean isRunning = true;
    public static void initialCurrentPhotoFragment() {
        if(td != null)
        {
            isRunning = false;
            td.interrupt();
            td = null;
            Log.e("lonelyship","執行序終止");
        }

         td  = new Thread(new Runnable() {
            @Override
            public void run() {

                if (isRunning)
                {
                    if (CurrentPhotoFragment != null) {
                        try {
                            CurrentPhotoFragment.initPhotos(getFocusDay());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
                isRunning = true;
                td.start();

//            CurrentPhotoFragment.initAdapter();

    }

    public void ResumePhotoFragment() {
        if (CurrentPhotoFragment != null) {
            CurrentPhotoFragment.onResume();
        }
    }

    public void StopPhotoFragment() {
        if (CurrentPhotoFragment != null) {
            CurrentPhotoFragment.onStop();
        }
    }

    public static String getFocusDay() {
        return m_strFocusDay;
    }

    public void setFocusDay(String strFocusDay) {
        m_strFocusDay = strFocusDay;
    }

    @Override
    protected void initialActionBar() {
        super.initialActionBar();
    }

    private ViewPager.OnPageChangeListener m_onPageChangeListener_Time;

    {
        m_onPageChangeListener_Time = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {


                if (arg0 == 1 && isFirstTime == false)
                {
                    return;  //第一次進要show
                }

                isFirstTime = false;

                //changeViewByReplace(Flag_Fragment_Progress);
                if(CurrentPhotoFragment != null)
                {
                    CurrentPhotoFragment.iIndex = 0;
                    CurrentPhotoFragment.showProgressBar();
                }

                if (arg0 == 2) day_from_Today++;
                if (arg0 == 0) day_from_Today--;

                changPhotoFragment();
                initialDiary();
                //int iDate = TimePagerAdapter.Max_Day_To_Show-arg0-1;
//                Date date = new Date(System.currentTimeMillis() + day_from_Today*24*60*60*1000l);
//                SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyyMMdd");
//                setFocusDay(sdf_Date.format(date)); //加入當入日期資料夾
//
//                Log.e("lonelyship","當前頁面日期:"+ m_strFocusDay );
//
//                initialCurrentPhotoFragment();
//                TimePagerAdapter.vInitialDay(day_from_Today);
//
//                Snackbar snackbar =
//                        Snackbar.make(coordinatorLayoutView, sdf_Date.format(date), Snackbar.LENGTH_LONG);
//                snackbar.show();


                if (arg0 != 1) {
                    m_viewPager_time.setCurrentItem(1);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };
    }

    public void changPhotoFragment() {
        Date date = new Date(System.currentTimeMillis() + day_from_Today * 24 * 60 * 60 * 1000l);
        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyyMMdd");
        setFocusDay(sdf_Date.format(date)); //加入當入日期資料夾

        Log.e("lonelyship", "當前頁面日期:" + m_strFocusDay);

        initialCurrentPhotoFragment();
        TimePagerAdapter.vInitialDay(day_from_Today);

//        Snackbar snackbar =
//                Snackbar.make(coordinatorLayoutView, sdf_Date.format(date), Snackbar.LENGTH_LONG);
//        snackbar.show();

    }

    public void changeViewByReplace(int iFlag) {
        // String strTag = m_onMainMenuViewInterface.uiMappingFlagToTag(iFlag);
        String strTag = getTargetStrFlag(iFlag);

        if (null == strTag) {
            return;
        }

        if(iFlag == m_iMainMenuViewFlag)return;
        m_iMainMenuViewFlag = iFlag;
        m_strMainMenuViewTag = strTag;

        //开始Fragment的事务Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(m_strMainMenuViewTag);

//        if (null == fragment)
//        {
//            fragment = getTargetFragment(m_iMainMenuViewFlag,iAnimationPosition);
//
//        }
        //改成這樣重新設定動畫才有效果
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragment = getTargetFragment(m_iMainMenuViewFlag);
        //替换容器(container)原来的Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment, m_strMainMenuViewTag);
        //设置转换效果
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        //提交事务
        fragmentTransaction.commit();

        //m_actionBar.setTitle(m_strMainMenuViewTag);
    }

    public String getTargetStrFlag(int iFlag) {
        switch (iFlag) {
            case Flag_Fragment_Photo:
                return "照片區";
            case Flag_Fragment_Map:
                return "地圖";
            case Flag_Fragment_Sound_Recording:
                return "錄音區";
            case Flag_Fragment_Video:
                return "錄影區";
            case Flag_Fragment_Progress:
                return "轉圈";
            default:
                return null;
        }
    }

    public static PhotoFragment CurrentPhotoFragment = null;

    public Fragment getTargetFragment(int iFlag) {
        switch (iFlag) {
            case Flag_Fragment_Photo:
                CurrentPhotoFragment = new PhotoFragment(getFocusDay());
                CurrentPhotoFragment.uiSetOnParameterAndListener(null, m_onPhotoFragmentListener);
                return CurrentPhotoFragment;
            case Flag_Fragment_Map:
                CurrentPhotoFragment = null;
                return new com.lonelyship.Fragment.MapFragment();
            case Flag_Fragment_Sound_Recording:
                return new TimeFragment("錄音區");
            case Flag_Fragment_Video:
                return new TimeFragment("錄影區");
            case Flag_Fragment_Progress:
                return new ProgressFragment();

            default:
                break;
        }
        return null;
    }


    /**
     * 側拉式選單初始化設定
     */
    private void initalDrawerLayout() {

        double dTotalSpace = (double) DiskUtils.lGetTotalSpace(true);
        double dFreeSpace = (double) DiskUtils.lGetFreeSpace(true);
        double dFreeSpaceWeight = dFreeSpace / dTotalSpace;
        double dUsedSpaceWeight = 1.0 - dFreeSpaceWeight;
        String sFreeSpaceText = "可用空間:" + DiskUtils.sGetFreeSpace(true);
        String sUsedSpaceText = "已用空間:" + DiskUtils.sGetBusySpace(true);

        m_MainMenuRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        m_MainMenuRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        m_MainMenuRecyclerViewAdapter = new MainMenuRecyclerViewAdapter(m_context, m_ListTitles, m_ListIcons, m_sUserTitle, m_sUserMail, m_iUserIcon, dFreeSpaceWeight, dUsedSpaceWeight, sFreeSpaceText, sUsedSpaceText);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)

        m_MainMenuRecyclerView.setAdapter(m_MainMenuRecyclerViewAdapter);                              // Setting the adapter to RecyclerView

        m_RecyclerViewLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        m_MainMenuRecyclerView.setLayoutManager(m_RecyclerViewLayoutManager);                 // Setting the layout Manager

        m_DrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);        // m_DrawerLayout object Assigned to the view

        m_DrawerToggle = new ActionBarDrawerToggle(this, m_DrawerLayout, m_ToolBar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        }; // m_DrawerLayout Toggle Object Made
        m_DrawerLayout.setDrawerListener(m_DrawerToggle); // m_DrawerLayout Listener set to the m_DrawerLayout toggle
        m_DrawerToggle.syncState();

        m_MainMenuRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(m_context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        if (position == Flag_MainMenu_GoogleMap) //點擊開啟地圖
                        {
                            changeViewByReplace(Flag_Fragment_Map);
                        }
                        else if (position == Flag_MainMenu_Setting) //點擊相關設定
                        {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            Fragment prevDialog = getSupportFragmentManager().findFragmentByTag("dialog");
                            if (prevDialog != null) {
                                ft.remove(prevDialog);
                            }

                            StopPhotoFragment();

                            SettingDialog settingDialog = SettingDialog.newInstance();
                            settingDialog.show(ft, "dialog");
                        } else if (position == Flag_MainMenu_Exit) {
                            //log out for google plus
//                            if (GooglePlusLoginFragment.mGoogleApiClient.isConnected()) {
//                                Plus.AccountApi.clearDefaultAccount(GooglePlusLoginFragment.mGoogleApiClient);
//                                GooglePlusLoginFragment.mGoogleApiClient.disconnect();
//                            }

                            //log out for fb
//                            FacebookSdk.sdkInitialize(getApplicationContext());
//                            LoginManager.getInstance().logOut();
                            MainActivity.this.finish();
                        } else if (position == Flag_MainMenu_CardSwiped) {
                            //log out for google plus
                            Intent it = new Intent(MainActivity.this, CardsSwipedActivity.class);
                            it.putExtra("m_strFocusDay", m_strFocusDay);
                            startActivity(it);
                        } else {
                            Toast.makeText(m_context, "此功能尚未實作", Toast.LENGTH_SHORT).show();
                        }

                        m_DrawerLayout.closeDrawers();
                    }
                })
        );
    }

    /**
     * 初始化動畫效果相關設定
     */
    private void initalDefalSettings() {
        m_iAnimationPosition = TheAppInfo.getInstance(m_context).uiGetAnimationPosition();
        m_iPlayDuration = TheAppInfo.getInstance(m_context).uiGetAnimationDuration();
        m_bIsAutoPlay = TheAppInfo.getInstance(m_context).uiGetAnimationIsAutoPlay();
    }


    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(m_context,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(m_context,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if(isFromCamera)return;
                List<Image> images = new ArrayList<>();
                int count = data.getCount();

                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        images.add(image);

                        if (!hasFolderGened) {
                            // 获取文件夹名称
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.cover = image;
                            if (!mResultFolder.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                mResultFolder.add(folder);
                            } else {
                                // 更新
                                Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
                                f.images.add(image);

                            }
                        }

                    } while (data.moveToNext());


                    for (Folder f : mResultFolder) {
                        for (Image img : f.images) {


                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(img.path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String  exifDate =  exif.getAttribute(ExifInterface.TAG_DATETIME);
                           // String Key = TimeUtils.formatPhotoDate(img.path);
                            String Key = exifDate;
                            if(Key == null)continue;
//                            else
//                            {
                                Key = Key.replace(":","");
                                if(Key.length() >= 8)
                                {
                                    Key = Key.substring(0,8);
                                }
                                else
                                {
                                    continue;
                                }
//                            }
//                            Log.e("lonelyship", "名稱"+img.name+"路徑"+img.path+"時間"+ Key);
                            ArrayList<String> al = new ArrayList<String>();
                            if (photoMap.get(Key) == null) {
                                al.add(img.path);
                                photoMap.put(Key, al);
                                //   photoMap.put(TimeUtils.formatPhotoDate(img.path), )
                            } else {
                                al = photoMap.get(Key);
                                al.add(img.path);
                                photoMap.put(Key, al);
                            }
                        }
                    }


                    //mImageAdapter.setData(images);

                    // 设定默认选择
                    if (resultList != null && resultList.size() > 0) {
                        //   mImageAdapter.setDefaultSelected(resultList);
                    }

                    // mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;

                }

                initialCurrentPhotoFragment();
//                Log.e("lonelyship","photoMap大小:"+photoMap.size());

//                Toast.makeText(m_context,photoMap.get("2015-10-23").size(),Toast.LENGTH_LONG).show();
//
//               Log.e("lonelyship","路徑:"+photoMap.get("2015-10-23").toString()+"大小:"+photoMap.get("2015-10-23").size());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    /**
     * initial login data
     */
    private void initialLoginData() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.getString(BUNDEL_KEY_LOGIN_TYPE).equals("Google")) {
            m_sUserTitle = bundle.getString(BUNDEL_KEY_NAME);
            m_sUserMail = bundle.getString(BUNDEL_KEY_EMAIL);
            new LoadProfileImage().execute(bundle.getString(BUNDEL_KEY_URL));
        } else {
            RequestData();
        }
    }


    /**
     * get google plus user icon from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        public LoadProfileImage() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            m_iUserIcon = result;
            initalDrawerLayout();
        }
    }

    /**
     * get user fb data from token
     */
    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        //String text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link");
                        m_sUserTitle = json.getString("name");
                        m_sUserMail = json.getString("email");
                        //Log.w("tonytest", json.getJSONObject("picture").getJSONObject("data").getString("url"));
                        String url = json.getJSONObject("picture").getJSONObject("data").getString("url");
                        new LoadProfileImage().execute("https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large");
                        //details_txt.setText(Html.fromHtml(text));
                        //profile.setProfileId(json.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //點擊相機
         if (item.getItemId() == R.id.action_settings_camera) {
             Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
             startActivityForResult(cameraIntent, RESULT_CAMERA);
        }


        //點擊麥克風
        else if (item.getItemId() == R.id.action_settings_microphone) {
            Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);


            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"說點什麼吧？");
            switch (TheAppInfo.getInstance(m_context).uiGetVoiceLanguagePosition()) {
                case 0:
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.TAIWAN.toString());
                    break;
                case 1:
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString());
                    break;
                case 2:
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPANESE.toString());
                    break;
                default:
                    break;
            }

            try {
                startActivityForResult(intent, RESULT_SPEECH);
                //tv_marquee.setText("");
            } catch (ActivityNotFoundException a) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "Opps! Your device doesn't support Speech to Text",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }

        //點擊日曆
        else if (item.getItemId() == R.id.action_settings_calendar) {

            // Setup caldroid to use as dialog
            m_dialogCaldroidFragment = new CaldroidFragment();
            m_dialogCaldroidFragment.setCaldroidListener(m_CalenderDialogListener);

            // If activity is recovered from rotation

            if (m_saveInstance != null) {
                m_dialogCaldroidFragment.restoreDialogStatesFromKey(
                        getSupportFragmentManager(), m_saveInstance,
                        "DIALOG_CALDROID_SAVED_STATE", dialogTag);
                Bundle args = m_dialogCaldroidFragment.getArguments();
                if (args == null) {
                    args = new Bundle();

                    Date date = new Date(System.currentTimeMillis() + day_from_Today*24*60*60*1000l); //跳轉至當前日期
                    String month = (String) android.text.format.DateFormat.format("MM", date);
                    String year = (String) android.text.format.DateFormat.format("yyyy", date);
                    args.putInt(CaldroidFragment.MONTH, Integer.parseInt(month)); //
                    args.putInt(CaldroidFragment.YEAR, Integer.parseInt(year));
                    m_dialogCaldroidFragment.setArguments(args);
                }
            } else {
                // Setup arguments
                Bundle bundle = new Bundle();
                // Setup dialogTitle
                Date date = new Date(System.currentTimeMillis() + day_from_Today*24*60*60*1000l); //跳轉至當前日期
                String month = (String) android.text.format.DateFormat.format("MM", date);
                String year = (String) android.text.format.DateFormat.format("yyyy", date);
                bundle.putInt(CaldroidFragment.MONTH, Integer.parseInt(month)); //
                bundle.putInt(CaldroidFragment.YEAR, Integer.parseInt(year));

                m_dialogCaldroidFragment.setArguments(bundle);
            }

            setColorForCalendarByDates();


            m_dialogCaldroidFragment.show(getSupportFragmentManager(),
                    dialogTag);

            return true;
        }


        return false;
    }

    /**
     * 設定日曆有照片的日期顏色
     */
    protected void setColorForCalendarByDates() {

        Set<String> alDates = photoMap.keySet();

        String[] dates = alDates.toArray(new String[alDates.size()]);
        Date tempDate;
        int iYear, iMonth, iDay;
        int iSize = dates.length;

        for (int i = 0; i < iSize; i++) {

            iYear = Integer.parseInt(dates[i].substring(0, 4)) - 1900;
            iMonth = Integer.parseInt(dates[i].substring(4, 6)) - 1;
            iDay = Integer.parseInt(dates[i].substring(6, 8));
            tempDate = new Date(iYear, iMonth, iDay);
            //Log.d("tonytest",tempDate.toString());

            if (m_dialogCaldroidFragment != null) {

                if (photoMap.get(dates[i]).size() <= 3) {
                    m_dialogCaldroidFragment.setBackgroundResourceForDate(R.color.colorGray,
                            tempDate);
                    m_dialogCaldroidFragment.setTextColorForDate(R.color.colorWhite, tempDate);
                } else if (photoMap.get(dates[i]).size() >= 7) {
                    m_dialogCaldroidFragment.setBackgroundResourceForDate(R.color.colorBlue,
                            tempDate);
                    m_dialogCaldroidFragment.setTextColorForDate(R.color.colorWhite, tempDate);
                } else if (photoMap.get(dates[i]).size() > 3 && photoMap.get(dates[i]).size() < 7) {
                    m_dialogCaldroidFragment.setBackgroundResourceForDate(R.color.colorGreenDark,
                            tempDate);
                    m_dialogCaldroidFragment.setTextColorForDate(R.color.colorWhite, tempDate);
                }
            }

        }
    }

    /**
     * 日曆Dialog Listener
     */
    private final CaldroidListener m_CalenderDialogListener = new CaldroidListener() {

        @Override
        public void onSelectDate(Date date, View view) {
            //String sToday = m_formatter.format(new Date(System.currentTimeMillis()));

            m_selectedDate = date;

            long lToday = new Date(System.currentTimeMillis()).getTime();

            long lPast = date.getTime();

            long lOneMonth = 1000L * 60L * 60L * 24L * 100L;

            long result = lToday - lPast;


//            if (result > lOneMonth)
//            {
//                Toast.makeText(getApplicationContext(),"不支援點選一百天以前的日期!",Toast.LENGTH_SHORT).show();
//            }
//            else if (result < 0)
//            {
//                Toast.makeText(getApplicationContext(),"不支援點選未來的日期!",Toast.LENGTH_SHORT).show();
//            }
//            else
//            {
            long lDay = -(result / (1000L * 60L * 60L * 24L));

            day_from_Today = lDay;

            if(CurrentPhotoFragment != null)CurrentPhotoFragment.showProgressBar();

            changPhotoFragment();
            initialDiary();

            String sPast = m_formatter.format(new Date(result));
//                Log.d("tonytest",""+lToday);
//                Log.d("tonytest",""+lPast);
//                Log.d("tonytest", "" + lDay);
//
//                m_viewPager_time.setCurrentItem(TimePagerAdapter.Max_Day_To_Show - (int) lDay - 1);
 //           getSupportFragmentManager().beginTransaction().hide(m_dialogCaldroidFragment).commit();
            m_dialogCaldroidFragment.dismiss();
            // }
            //Log.d("tonytest",""+lToday+"\n"+lPast+"\n"+lOneMonth+"\n"+result);

        }

        @Override
        public void onChangeMonth(int month, int year) {
            String text = "month: " + month + " year: " + year;
//            Toast.makeText(getApplicationContext(), text,
//                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLongClickDate(Date date, View view) {
            Toast.makeText(getApplicationContext(),
                    "Long click " + m_formatter.format(date),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCaldroidViewCreated() {
            if (m_dialogCaldroidFragment.getLeftArrowButton() != null) {
//                Toast.makeText(getApplicationContext(),
//                        "Caldroid view is created", Toast.LENGTH_SHORT)
//                        .show();
            }
        }
    };

    private void showProgressDialog(String strMessage) {
        dismissProgressDialog();
        m_progressDialog = new ProgressDialog(this);
        //dialog.setTitle("");
        m_progressDialog.setMessage(strMessage);
        //dialog.setIndeterminate(indeterminate);
        m_progressDialog.setCancelable(false);
        m_progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (null == m_progressDialog) {
            return;
        }

        m_progressDialog.dismiss();
        m_progressDialog = null;

    }

    //搖一搖時
    @Override
    public void onMShakeWork()
    {
        Toast.makeText(this, "切換至圖卡模式!", Toast.LENGTH_SHORT).show();
        Intent it = new Intent(MainActivity.this, CardsSwipedActivity.class);
        it.putExtra("m_strFocusDay", m_strFocusDay);
        startActivity(it);
    }

    /**給使用者選他們講的字*/
    private void vShowSelectWhichWordDialog (final String[] words) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_context);

        alertDialogBuilder.setTitle("請選擇你說了什麼？")

                .setSingleChoiceItems(words, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ((words[which].equals("clear") || words[which].equals("清除") || words[which].equals("くりあ")  ||  words[which].equals("クリア"))) {
                            tv_marquee.setText(m_context.getString(R.string.default_marquee_hint));
                            SaveFile("");
                        } else if (tv_marquee.getText().toString().equals(m_context.getString(R.string.default_marquee_hint))) {
                            tv_marquee.setText(words[which]);
                            SaveFile(words[which]);
                        } else {
                            tv_marquee.setText(tv_marquee.getText() + words[which]);
                            SaveFile(tv_marquee.getText() + words[which]);
                        }
                        tv_marquee.init(getWindowManager());
                        tv_marquee.startScroll();
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}