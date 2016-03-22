package com.lonelyship.Main;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.lonelyship.Dialog.AboutMeDialog;
import com.roomorama.caldroid.CaldroidFragment;

import object.ShakeWorker;


public class BaseActivity extends ActionBarActivity implements ShakeWorker.OnShakeWorkerListener {

//    protected android.support.v7.app.ActionBar m_actionBar = null;
    protected Toolbar m_ToolBar;
    protected CaldroidFragment m_dialogCaldroidFragment; //顯示日曆的Dialog
    private ShakeWorker m_shakeWorker = null;
    private AboutMeDialog m_aboutDialog = null; //顯示文字檔的視窗


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        m_shakeWorker = new ShakeWorker(this, this);
        m_aboutDialog = new AboutMeDialog(this);
    }

    /**
     * 初始化，Action Bar
     * 將ActionBar預設定義在BaseActivity
     */
    protected void initialActionBar()
    {
         /* Assinging the m_ToolBar object ot the view
    and setting the the Action bar to our m_ToolBar
     */
        m_ToolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(m_ToolBar);


//        m_actionBar = getSupportActionBar();
//        m_actionBar.setHomeButtonEnabled(true);
//        m_actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.app_color_Red)); //lonelyship 2015/5/7 設定ActionBar顏色
        //m_actionBar.setDisplayHomeAsUpEnabled(true);				//已由 style.xml 定義
        //m_actionBar.setIcon(R.drawable.mbkui_color_Transparent);	//已由 style.xml 定義
        //SetActionBarTitleClick();

//        try
//        {//若手機有實體 Menu 鍵，則不會出現 Overflow 鍵，為了讓 ActionBar 強制顯示 Overflow 選單，須有以下設定。
//            ViewConfiguration config = ViewConfiguration.get(this);
//            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
//            if(menuKeyField != null) {
//                menuKeyField.setAccessible(true);
//                menuKeyField.setBoolean(config, false);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    /**
     * 讓4.3以下的手機也可按字觸發側選單
     * */
//    public void SetActionBarTitleClick() {
//        String sVer = Build.VERSION.RELEASE;
//        if (sVer.compareTo("4.2") > 0)
//        {
//            return;
//        }
//        Window window = getWindow();
//        View v = window.getDecorView();
//        final int resId = getResources().getIdentifier("action_bar_title", "id", "android");
//        View actionBarView = v.findViewById(resId);
//        if (actionBarView != null)
//        {
//            //actionBarView.findViewById(resId).setOnClickListener(new OnClickListener() {
//            actionBarView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arv0) {
//                    if (arv0.getId() == resId)
//                    {
//                        onBackPressed();
//                    }
//                }
//            });
//        }
//    }

    long currentBackTime = 0;
    long lastBackTime = 0;
    @Override
    public void onBackPressed()
    {
        if(isTaskRoot())
        {
            currentBackTime = System.currentTimeMillis();
            //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
            if(currentBackTime - lastBackTime > 2 * 1000){
                Toast.makeText(this, "再按一次返回鍵退出", Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            }else{ //如果两次按下的时间差小于2秒，则退出程序
                System.exit(0);
                this.finish();
            }
//            System.exit(0);
//            this.finish();
        }
      //  super.onBackPressed();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        m_shakeWorker.uiOnResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        m_shakeWorker.uiOnPause();
    }

    //搖一搖時
    @Override
    public void onMShakeWork()
    {

        Toast.makeText(this, "搖一搖ing", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
