/**
 * AndTinder v0.1 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.lopez-manas.com
 *
 * TAndTinder is a native library for Android that provide a
 * Tinder card like effect. A card can be constructed using an
 * image and displayed with animation effects, dismiss-to-like
 * and dismiss-to-unlike, and use different sorting mechanisms.
 *
 * AndTinder is compatible with API Level 13 and upwards
 *
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */

package com.lonelyship.CardsSwiped;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lonelyship.Fragment.PhotoFragment;
import com.lonelyship.Main.MainActivity;
import com.lonelyship.Main.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import object.ShakeWorker;
import object.TheAppInfo;


public class CardsSwipedActivity extends Activity implements ShakeWorker.OnShakeWorkerListener{

    /**
     * This variable is the container that will host our cards
     */
	private CardContainer mCardContainer;
    private ProgressBar   m_ProgressBar;
    Activity thisActivity = null;
    private ShakeWorker m_shakeWorker = null;
    String m_date = "";

   // int iTotalCounts = 0;

     SimpleCardStackAdapter adapter = null;

    protected ArrayList<Bitmap> m_alImgs = new ArrayList<Bitmap>(); //讀出來的圖片
    protected ArrayList<String> m_alDescription = new ArrayList<String>(); //讀出來的圖片的時間

    private  static  final  int  AddCardModel_MSG = 0x0001;
    private  static  final  int  ShowProgressBar_MSG = 0x0002;

    ImageView Left_Arrow = null;
    ImageView Right_Arrow = null;

    public  Handler m_handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case AddCardModel_MSG:

                    AddCardModel();

                    if(m_alImgs.size() == 0 || m_alImgs == null)
                    {
                        thisActivity.finish();
                    }

                    mCardContainer.setAdapter(adapter);

                    hideProgressBar();
                    break;

                case ShowProgressBar_MSG:

                    if(MainActivity.CurrentPhotoFragment != null) {

                        MainActivity.CurrentPhotoFragment.isShowLeftArrow  = true;
                        // changeViewByReplace(Flag_Fragment_Photo);

                        MainActivity.CurrentPhotoFragment.showProgressBar();
                        Log.e("lonelyship", "轉圈");
                    }
                    break;
            }

        }
    } ;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.card_swiped_activity);

        overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);

        m_shakeWorker = new ShakeWorker(this, this);

        m_date = getIntent().getStringExtra("m_strFocusDay");

		mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        m_ProgressBar = (ProgressBar) findViewById(R.id.progressBar_card_swiped);

        Right_Arrow  = (ImageView)findViewById(R.id.r_arrow_card_swiped);
        Right_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoFragment.iIndex += 10;
               // Right_Arrow.setVisibility(View.GONE);
                //Left_Arrow.setVisibility(View.VISIBLE);

                adapter = null;
                for(Bitmap bm : m_alImgs)
                {
                    bm.recycle();
                }
                m_alImgs.clear();
                m_alImgs = null;
                System.gc();
               thisActivity.finish();
                Intent it = new Intent(CardsSwipedActivity.this, CardsSwipedActivity.class);
                it.putExtra("m_strFocusDay", m_date);
                startActivity(it);

                Log.e("lonelyship","iIndex"+PhotoFragment.iIndex);
            }
        });
        Left_Arrow  = (ImageView) findViewById(R.id.l_arrow_card_swiped);
        Left_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoFragment.iIndex -= 10;
                //Left_Arrow.setVisibility(View.GONE);
                adapter = null;
                for(Bitmap bm : m_alImgs)
                {
                    bm.recycle();
                }
                m_alImgs.clear();
                m_alImgs = null;
                System.gc();
                thisActivity.finish();

                Intent it = new Intent(CardsSwipedActivity.this, CardsSwipedActivity.class);
                it.putExtra("m_strFocusDay", m_date);
                startActivity(it);


            }
        });

        thisActivity = this;
		Resources r = getResources();

        adapter = new SimpleCardStackAdapter(this);

        showProgressBar();
        initialCardModel();


	}

    Thread CardSwiped_Thread = null;
    static boolean isRunning = true;
    public void initialCardModel() {
        if(CardSwiped_Thread != null)
        {
            isRunning = false;
            CardSwiped_Thread.interrupt();
            CardSwiped_Thread = null;
            Log.e("lonelyship","執行序終止");
        }

         Thread CardSwiped_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (isRunning) {
                        initPhotos(m_date);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        isRunning = true;
        CardSwiped_Thread.start();

    }


    synchronized public void initPhotos(String strFocusDay) throws IOException {

        m_alImgs.clear();
        m_alDescription.clear();
        m_date = strFocusDay;

        if(MainActivity.photoMap.get(strFocusDay) == null)
        {
            Message msg = new Message();
            msg.what = AddCardModel_MSG;
            m_handler.sendMessage(msg);
            return;
        }

//        LinkedList link = new LinkedList();


        //     File file = new File(TheAppInfo.getInstance(getActivity()).getUserFilesPath(strFocusDay)); //從那個目錄下開始查詢
       // Log.e("lonelyship", TheAppInfo.getInstance(getActivity()).getUserFilesPath(strFocusDay));

        //   File[] dir = file.listFiles(); //獲取目錄下的所有檔(包括資料夾和檔)
        for (int i = PhotoFragment.iIndex; i < MainActivity.photoMap.get(strFocusDay).size(); i++) {
            if((i - PhotoFragment.iIndex) >= 10)break;

//                System.out.println(dir[i].getAbsolutePath()); //列印出來
            Log.e("lonelyship", MainActivity.photoMap.get(strFocusDay).get(i));

          //  File file_photo = new File(MainActivity.photoMap.get(strFocusDay).get(i));

          //  if (file_photo.exists()) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                option.inPurgeable = true;
                Bitmap bm = BitmapFactory.decodeFile(MainActivity.photoMap.get(strFocusDay).get(i), option);
                int yRatio = (int) Math.ceil(option.outHeight / 500);
                int xRatio = (int) Math.ceil(option.outWidth / 500);
                if (yRatio > 1 || xRatio > 1) {
                    if (yRatio > xRatio) {
                        option.inSampleSize = yRatio;
//                        Toast.makeText(MainActivity.this,
//                                "yRatio = " + String.valueOf(yRatio),
//                                Toast.LENGTH_LONG).show();
                    } else {
                        option.inSampleSize = xRatio;
//                        Toast.makeText(MainActivity.this,
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
                  bm = BitmapFactory.decodeFile(MainActivity.photoMap.get(strFocusDay).get(i), option);

                ExifInterface exif = new ExifInterface(MainActivity.photoMap.get(strFocusDay).get(i));
                int exifOrientation =  exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
            String  exifDateTime =  exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            if(exifDateTime == null)exifDateTime = "";

             try
             {
                 bm = BitmapFactory.decodeFile(MainActivity.photoMap.get(strFocusDay).get(i), option);
             }catch (Exception e)
             {
                 bm.recycle();
                 bm = null;
             }

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

                if(bm == null || m_alImgs == null) return;

                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

                     //   bm = zoomBitmap(bm,bm.getWidth()*5,bm.getHeight()*5);


//                if(bm.getWidth()<bm.getHeight())
//                {
//                    Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//                }

                m_alImgs.add(bm);
                m_alDescription.add(exifDateTime);

            }

        //}
             Message msg = new Message();
             msg.what = AddCardModel_MSG;
             m_handler.sendMessage(msg);
    }

    int iTotalCounts = 0;
    public void AddCardModel()
    {
        if(m_alImgs == null)return;
         iTotalCounts = 0;
        if(m_alImgs.size()>0)
        {
            for (int i = m_alImgs.size()-1; i > 0; i--)
            {
//               Bitmap bm = m_alImgs.get(i);
//
                String m_strDescription = "";
                if(m_alDescription.get(i) != null)m_strDescription = m_alDescription.get(i);
                adapter.add(new CardModel("相片" + (PhotoFragment.iIndex+i+1), m_strDescription,m_alImgs.get(i) ));

                Log.e("lonelyship", "寬:"+m_alImgs.get(i).getWidth()+"高:"+m_alImgs.get(i).getHeight());

            }
            //Bitmap bm = m_alImgs.get(m_alImgs.size()-1);
//
//            adapter.add(new CardModel("Title" + (m_alImgs.size()), "Description goes here",bm ));
            Log.e("lonelyship", "寬:"+m_alImgs.get(0).getWidth()+"高:"+m_alImgs.get(0).getHeight());

            String m_strDescription = "";
            if(m_alDescription.get(0) != null)m_strDescription = m_alDescription.get(0);

            CardModel cardModel = new CardModel("相片"+(PhotoFragment.iIndex+0+1), m_strDescription, m_alImgs.get(0));
            cardModel.setOnClickListener(new CardModel.OnClickListener() {
                @Override
                public void OnClickListener() {
                    Log.i("Swipeable Cards", "I am pressing the card");
                    // Toast.makeText(getApplicationContext(),"OnClick",Toast.LENGTH_SHORT).show();
                }
            });

            cardModel.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
                @Override
                public void onLike() {
                    Log.i("Swipeable Cards", "I like the card");

                    iTotalCounts++;

                  if(iTotalCounts == m_alImgs.size() && PhotoFragment.iIndex + 10 > MainActivity.photoMap.get(m_date).size()-1)
                  {
                      adapter = null;
                      for(Bitmap bm : m_alImgs)
                      {
                          bm.recycle();
                      }
                      m_alImgs.clear();
                      m_alImgs = null;
                      System.gc();
                      thisActivity.finish();

                      Message msg = new Message();
                      msg.what = ShowProgressBar_MSG;
                      m_handler.sendMessage(msg);

                      MainActivity.initialCurrentPhotoFragment();
                  }
                    else if(iTotalCounts == m_alImgs.size())
                  {
                      Right_Arrow.callOnClick();
                  }
                }

                @Override
                public void onDislike() {
                    Log.i("Swipeable Cards", "I dislike the card");

                    iTotalCounts++;
                    if(iTotalCounts == m_alImgs.size() && PhotoFragment.iIndex + 10 > MainActivity.photoMap.get(m_date).size()-1)
                    {
                        adapter = null;
                        for(Bitmap bm : m_alImgs)
                        {
                            bm.recycle();
                        }
                        m_alImgs.clear();
                        m_alImgs = null;
                        System.gc();
                        thisActivity.finish();

                        Message msg = new Message();
                        msg.what = ShowProgressBar_MSG;
                        m_handler.sendMessage(msg);

                        MainActivity.initialCurrentPhotoFragment();
                    }
                    else if(iTotalCounts == m_alImgs.size())
                    {
                        Right_Arrow.callOnClick();
                    }
                }
            });

            adapter.add(cardModel);
        }

    }

    @Override
    public void onMShakeWork() {
        adapter = null;
        if(m_alImgs == null)return;
        for(Bitmap bm : m_alImgs)
        {
            bm.recycle();
        }
        m_alImgs.clear();
        m_alImgs = null;
        System.gc();
        thisActivity.finish();

        Message msg = new Message();
        msg.what = ShowProgressBar_MSG;
        m_handler.sendMessage(msg);

        MainActivity.initialCurrentPhotoFragment();

    }

    public void showProgressBar() {
        m_ProgressBar.setVisibility(View.VISIBLE);
        mCardContainer.setVisibility(View.INVISIBLE);
        Right_Arrow.setVisibility(View.GONE);
        Left_Arrow.setVisibility(View.GONE);
    }

    public void hideProgressBar() {
        m_ProgressBar.setVisibility(View.GONE);
        mCardContainer.setVisibility(View.VISIBLE);
        checkToShowArrow();
    }

    public void  checkToShowArrow()
    {
        if(MainActivity.photoMap.get(m_date) != null)
        {
            if(PhotoFragment.iIndex + 10 > MainActivity.photoMap.get(m_date).size()-1)
            {
                Right_Arrow.setVisibility(View.GONE);
            }
            else
            {
                    Right_Arrow.setVisibility(View.VISIBLE);
            }

            if(PhotoFragment.iIndex - 10 < 0)
            {
                Left_Arrow.setVisibility(View.GONE);
            }
            else
            {
                   Left_Arrow.setVisibility(View.VISIBLE);
            }
        }

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

    @Override
    public void onBackPressed() {
        adapter = null;
        if(m_alImgs == null)return;
        for(Bitmap bm : m_alImgs)
        {
            bm.recycle();
        }
        m_alImgs.clear();
        m_alImgs = null;
        System.gc();
        super.onBackPressed();

        Message msg = new Message();
        msg.what = ShowProgressBar_MSG;
        m_handler.sendMessage(msg);

        MainActivity.initialCurrentPhotoFragment();
    }
}
