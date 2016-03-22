package com.lonelyship.Fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lonelyship.Main.MainActivity;
import com.lonelyship.Main.R;
import com.lonelyship.transforms.ABaseTransformer;
import com.lonelyship.transforms.AccordionTransformer;
import com.lonelyship.transforms.BackgroundToForegroundTransformer;
import com.lonelyship.transforms.CubeInTransformer;
import com.lonelyship.transforms.CubeOutTransformer;
import com.lonelyship.transforms.DefaultTransformer;
import com.lonelyship.transforms.DepthPageTransformer;
import com.lonelyship.transforms.FlipHorizontalTransformer;
import com.lonelyship.transforms.FlipVerticalTransformer;
import com.lonelyship.transforms.ForegroundToBackgroundTransformer;
import com.lonelyship.transforms.RotateDownTransformer;
import com.lonelyship.transforms.RotateUpTransformer;
import com.lonelyship.transforms.ScaleInOutTransformer;
import com.lonelyship.transforms.StackTransformer;
import com.lonelyship.transforms.TabletTransformer;
import com.lonelyship.transforms.ZoomInTransformer;
import com.lonelyship.transforms.ZoomOutSlideTransformer;
import com.lonelyship.transforms.ZoomOutTranformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import object.TheAppInfo;

/**
 * Created by lonelyship on 15/10/6.
 * 相片頁面
 */
public class PhotoFragment extends Fragment {

    String m_date = "";
    /**
     * 動畫幾秒換一張
     */
    int m_iPlayDuration = 0;
    private ViewPager autoChangeViewPager;

    //用来指示当前显示图片所在位置
    private LinearLayout viewIndicator;

    private ProgressDialog m_progressDialog = null;

    protected ArrayList<Bitmap> m_alImgs = new ArrayList<Bitmap>(); //讀出來的圖片

    //包含要在ViewPager中显示的图片
    private List<View> pagers = new ArrayList<View>();
    ViewPageChangeListener viewPageChangeListener;
    ViewPagerAdapter m_ViewPagerAdapter = null;

    ProgressBar progressBar = null;

    ImageView Left_Arrow = null;
    ImageView Right_Arrow = null;

    boolean isShowRightArrow = false;
    public boolean isShowLeftArrow = false;

    public static int  iIndex = 0;



    public PhotoFragment() {

    }
    public PhotoFragment(String _date) {

        m_date = _date;
    }

    public interface OnPhotoFragmentListener {
        public void selectPhoto(String path);
    }

    public interface OnPhotoFragmentParameter {

    }

    private OnPhotoFragmentParameter m_onParameter = null;
    private OnPhotoFragmentListener m_onListener = null;

    /**
     * 設定「Input、Output」介面
     *
     * @param _onParameter Input介面
     */
    public void uiSetOnParameterAndListener(OnPhotoFragmentParameter _onParameter, OnPhotoFragmentListener _onListener) {
        m_onParameter = _onParameter;
        m_onListener = _onListener;
    }


    protected LayoutInflater m_layoutInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_layoutInflater = inflater;
        return inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("lonelyship", "[PhotoFragment][onViewCreated]");
        initialLayoutComponent(m_layoutInflater, view);

        m_ViewPagerAdapter = new ViewPagerAdapter(pagers);
        autoChangeViewPager.setAdapter(m_ViewPagerAdapter);

            this.showProgressBar();

            Thread td  = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        initPhotos(m_date);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            td.start();

       // initAdapter();

        //监听页面改变事件来改变viewIndicator中的指示图片
        viewPageChangeListener = new ViewPageChangeListener();
        autoChangeViewPager.setOnPageChangeListener(viewPageChangeListener);


    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        autoChangeViewPager.setVisibility(View.INVISIBLE);
        viewIndicator.setVisibility(View.INVISIBLE);
        Left_Arrow.setVisibility(View.GONE);
        Right_Arrow.setVisibility(View.GONE);

    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        autoChangeViewPager.setVisibility(View.VISIBLE);
        viewIndicator.setVisibility(View.VISIBLE);

        checkToShowArrow();
    }

    public void  checkToShowArrow()
    {
        if(MainActivity.photoMap.get(m_date) != null)
        {
            if(iIndex + 10 > MainActivity.photoMap.get(m_date).size()-1)
            {
                Right_Arrow.setVisibility(View.GONE);
            }
            else
            {
                if(isShowRightArrow)
                {
                    Right_Arrow.setVisibility(View.VISIBLE);
                    isShowRightArrow = false;
                }
                else
                {
                    Right_Arrow.setVisibility(View.GONE);

                }
            }

            if(iIndex - 10 < 0)
            {
                Left_Arrow.setVisibility(View.GONE);
            }
            else
            {
                if(isShowLeftArrow)
                {
                    Left_Arrow.setVisibility(View.VISIBLE);
                    //isShowLeftArrow = false;
                }
                else
                    Left_Arrow.setVisibility(View.GONE);
            }
        }

    }


    synchronized public void initPhotos(final String strFocusDay) throws IOException {

        m_alImgs.clear();

        m_date = strFocusDay;

        if (MainActivity.photoMap.get(strFocusDay) == null) {
            Message msg = new Message();
            msg.what = 1;
            MainActivity.m_handler.sendMessage(msg);

            return;
        }

//        LinkedList link = new LinkedList();
//        showProgressBar();

        //     File file = new File(TheAppInfo.getInstance(getActivity()).getUserFilesPath(strFocusDay)); //從那個目錄下開始查詢
       // Log.e("lonelyship", TheAppInfo.getInstance(getActivity()).getUserFilesPath(strFocusDay));

        //Log.e("lonelyship", "開始時間1:"+System.currentTimeMillis());

        //   File[] dir = file.listFiles(); //獲取目錄下的所有檔(包括資料夾和檔)

        int size = MainActivity.photoMap.get(strFocusDay).size();
        Log.e("lonelyship", "張數:" + size);

        for (int i = iIndex; i < size; i++) {

            if(MainActivity.m_strFocusDay != strFocusDay)return;

            if ((i - iIndex) >= 10) break;   //一頁顯示10張

            String strPath = MainActivity.photoMap.get(strFocusDay).get(i);

//                System.out.println(dir[i].getAbsolutePath()); //列印出來
            //Log.e("lonelyship", strPath);

            //Log.e("lonelyship", "開始時間2:"+System.currentTimeMillis());
            // File file_photo = new File(MainActivity.photoMap.get(strFocusDay).get(i));

            //if (file_photo.exists()) {

            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inJustDecodeBounds = true;
            option.inPurgeable = true;
            Bitmap bm = BitmapFactory.decodeFile(strPath, option);
            int yRatio = (int) Math.ceil(option.outHeight / 500);
            int xRatio = (int) Math.ceil(option.outWidth / 500);
            if (yRatio > 1 || xRatio > 1) {
                if (yRatio > xRatio) {
                    option.inSampleSize = yRatio;
//                        Toast.makeText(getActivity(),
//                                "yRatio = " + String.valueOf(yRatio),
//                                Toast.LENGTH_LONG).show();
                } else {
                    option.inSampleSize = xRatio;
//                        Toast.makeText(getActivity(),
//                                "xRatio = " + String.valueOf(xRatio),
//                                Toast.LENGTH_LONG).show();
                }
            }
//  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            option.inJustDecodeBounds = false;

//                Matrix matrix = new Matrix();
//
//                matrix.postRotate(90);
//

            ExifInterface exif = new ExifInterface(strPath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            //Log.e("lonelyship", "開始時間3:"+System.currentTimeMillis());
            bm = BitmapFactory.decodeFile(strPath, option);
            //Log.e("lonelyship", "開始時間4:"+System.currentTimeMillis());

            Matrix matrix = new Matrix();

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    // orientation = 270;
                    matrix.postRotate(270);

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);

                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    matrix.postRotate(0);

                    break;
                default:
                    break;
            }
            //Log.e("lonelyship", "開始時間5:"+System.currentTimeMillis());
            if (bm == null) return;
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

            //Log.e("lonelyship", "開始時間6:"+System.currentTimeMillis());
//                if(bm.getWidth()<bm.getHeight())
//                {
//                    Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//                }
            m_alImgs.add(bm);
            //
            // Log.e("lonelyship", "開始時間7:"+System.currentTimeMillis());

            //     }

        }

        Message msg = new Message();
        msg.what = 1;
        MainActivity.m_handler.sendMessage(msg);

        Log.e("lonelyship", " m_alImgs的size" + m_alImgs.size());
        //Log.e("lonelyship", "結束時間:"+System.currentTimeMillis());

    }

    protected int getLayoutResourceId() {
        return R.layout.fragment_photo;
    }

    long currentBackTime = 0;
    long lastBackTime = 0;
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        autoChangeViewPager = (ViewPager) view.findViewById(R.id.autoVP);
        viewIndicator = (LinearLayout) view.findViewById(R.id.vpindicator);
        viewIndicator.setOnTouchListener(new View.OnTouchListener() {
            float x = 0, y= 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.e("lonelyship", " [PhotoFragment][OnTouch])");
                currentBackTime = System.currentTimeMillis();
                //比较上次觸摸和当前觸摸的时间差，如果大于1秒，则不做事
                if(currentBackTime - lastBackTime > 0.5 * 1000){

                    lastBackTime = currentBackTime;
                }else{ //如果两次按下的时间差小于2秒，则顯示/隱藏懸浮按鈕
                    x = event.getX();
                    y = event.getY();
                    Log.e("lonelyship", " [PhotoFragment][OnDoubleTouch]("+x+","+y+")");
                    Message msg = new Message();
                    msg.what = 2;
                    Bundle bundle = new Bundle();
                    bundle.putFloat("X",x);  //往Bundle中存放数据
                    bundle.putFloat("Y",y);  //往Bundle中put数据
                    msg.setData(bundle);
                    MainActivity.m_handler.sendMessage(msg);

                     currentBackTime = 0;
                     lastBackTime = 0;
                }
                return false;
            }
        });

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Right_Arrow  = (ImageView) view.findViewById(R.id.r_arrow);
        Right_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iIndex += 10;
                Right_Arrow.setVisibility(View.GONE);
                Left_Arrow.setVisibility(View.GONE);
                //Left_Arrow.setVisibility(View.VISIBLE);
                isShowLeftArrow  = true;
                showProgressBar();
                for(Bitmap bm : m_alImgs)
                {
                    bm.recycle();
                }
                m_alImgs.clear();

                System.gc();
                MainActivity.initialCurrentPhotoFragment();
                Log.e("lonelyship","iIndex"+iIndex);
            }
        });
        Left_Arrow  = (ImageView) view.findViewById(R.id.l_arrow);
        Left_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iIndex -= 10;
                Left_Arrow.setVisibility(View.GONE);
                showProgressBar();
                for(Bitmap bm : m_alImgs)
                {
                    bm.recycle();
                }
                m_alImgs.clear();
                MainActivity.initialCurrentPhotoFragment();

            }
        });
    }

    synchronized  public void initAdapter() {
        //即将在viewPager中展示的图片资源
        //int[] imgs = {R.drawable.img1, R.drawable.img2, R.drawable.img3};
        //init pagers;
        if(m_alImgs == null)return;
        pagers.clear();
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        for (int i = 0; i < m_alImgs.size(); ++i) {
            ImageView iv = new ImageView(getActivity());
            //iv.setBackgroundResource(imgs[i]);
            iv.setImageBitmap(m_alImgs.get(i));
            iv.setLayoutParams(img_params);
            iv.setTag(i);
            // final int index = i;
            iv.setOnClickListener(new View.OnClickListener() {
                //当viewPager中的图片被点击后，跳转到新的activity
                @Override
                public void onClick(View v) {

                    Toast.makeText(getActivity(), MainActivity.photoMap.get(m_date).get(Integer.parseInt((String) v.getTag())), Toast.LENGTH_LONG).show();

                }
            });
            pagers.add(iv);
        }
        //autoChangeViewPager.setAdapter(new ViewPagerAdapter(pagers));

        //init indicator
        viewIndicator.removeAllViews();
        LinearLayout.LayoutParams ind_params = new LinearLayout.LayoutParams(
                50, 50
        );
        ind_params.setMargins(5, 5, 5, 5);
        for (int i = 0; i < m_alImgs.size(); ++i) {
            ImageView iv = new ImageView(getActivity());
            if (i == 0)
                iv.setBackgroundResource(R.drawable.btn2);
            else
                iv.setBackgroundResource(R.drawable.btn1);
            iv.setLayoutParams(ind_params);
            viewIndicator.addView(iv);
            iv.setTag(i);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    autoChangeViewPager.setCurrentItem(pos, true);
                    //Toast.makeText(getActivity(), "按了第" + pos + "個圈圈", Toast.LENGTH_SHORT).show();
                }
            });
        }
        mData = pagers;
        m_ViewPagerAdapter.notifyDataSetChanged();

        if (pagers.size() == 0) {
            ImageView iv = new ImageView(getActivity());
            iv.setBackgroundResource(R.drawable.no_photo);  //沒照片時給張預設的
            iv.setLayoutParams(img_params);

            pagers.add(iv);
        }
        //  else {
        autoChangeViewPager.setAdapter(new ViewPagerAdapter(pagers));

        Log.e("lonelyship", "畫面上張數" + pagers.size());
//========================================================

       hideProgressBar();

//=======================================================================
        //  }
    }

    @Override
    public void onResume() {
        super.onResume();
        /**哪種動畫的位置*/
        int iAnimationPosition = TheAppInfo.getInstance(getActivity()).uiGetAnimationPosition();
        autoChangeViewPager.setPageTransformer(true, getTransFormer(iAnimationPosition));
        /**是否自動播放*/
        boolean bIsAutoPlay = TheAppInfo.getInstance(getActivity()).uiGetAnimationIsAutoPlay();
        /**動畫幾秒換一張*/m_iPlayDuration = TheAppInfo.getInstance(getActivity()).uiGetAnimationDuration();
        //activity启动两秒钟后，发送一个message，用来将viewPager中的图片切换到下一个
        if (bIsAutoPlay) {
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, m_iPlayDuration * 1000);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /**是否自動播放*/
        boolean bIsAutoPlay = TheAppInfo.getInstance(getActivity()).uiGetAnimationIsAutoPlay();
        //停止viewPager中图片的自动切换
        if (bIsAutoPlay) {
            mHandler.removeMessages(1);
        }
    }

    private List<View> mData;

    // Adapter
    public class ViewPagerAdapter extends PagerAdapter {

        public ViewPagerAdapter(List<View> Data) {
            mData = Data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mData.get(position);
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//          super.destroyItem(container, position, object);
            if (position < mData.size())    //沒加這行會掛 ＝＝
                container.removeView(mData.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            int index = mData.indexOf(object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

    }

    private class ViewPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        //监听页面改变事件来改变viewIndicator中的指示图片
        @Override
        public void onPageSelected(int arg0) {

             currentBackTime = 0;
             lastBackTime = 0;

            int len = viewIndicator.getChildCount();
            for (int i = 0; i < len; ++i)
                viewIndicator.getChildAt(i).setBackgroundResource(R.drawable.btn1);
            viewIndicator.getChildAt(arg0).setBackgroundResource(R.drawable.btn2);

            if(arg0 == (len-1))
            {
                isShowRightArrow = true;
                isShowLeftArrow = false;
                checkToShowArrow();
            }
            else if(arg0 == (0))
            {
                isShowLeftArrow = true;
                isShowRightArrow = false;
                checkToShowArrow();
            }
            else
            {
                isShowRightArrow = false;
                isShowLeftArrow = false;
                checkToShowArrow();
            }
        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    int totalcount = pagers.size();//autoChangeViewPager.getChildCount();
                    int currentItem = autoChangeViewPager.getCurrentItem();

                    int toItem = currentItem + 1 == totalcount ? 0 : currentItem + 1;

                    autoChangeViewPager.setCurrentItem(toItem, true);

                    //每两秒钟发送一个message，用于切换viewPager中的图片
                    this.sendEmptyMessageDelayed(1, m_iPlayDuration * 1000);
            }
        }
    };


    private ABaseTransformer getTransFormer(int iPosition)  //ViewPager動畫效果
    {
        switch (iPosition) {
            case 0:
                return new AccordionTransformer();
            case 1:
                return new BackgroundToForegroundTransformer();
            case 2:
                return new CubeInTransformer();
            case 3:
                return new CubeOutTransformer();
            case 4:
                return new DefaultTransformer();
            case 5:
                return new DepthPageTransformer();
            case 6:
                return new FlipHorizontalTransformer();
            case 7:
                return new FlipVerticalTransformer();
            case 8:
                return new ForegroundToBackgroundTransformer();
            case 9:
                return new RotateDownTransformer();
            case 10:
                return new RotateUpTransformer();
            case 11:
                return new ScaleInOutTransformer();
            case 12:
                return new StackTransformer();
            case 13:
                return new TabletTransformer();
            case 14:
                return new ZoomInTransformer();
            case 15:
                return new ZoomOutSlideTransformer();
            case 16:
                return new ZoomOutTranformer();
        }

        return new DefaultTransformer();
    }

    private void showProgressDialog(String strMessage) {
        dismissProgressDialog();
        m_progressDialog = new ProgressDialog(getActivity());
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

}
