package com.display.layout.scrolllayoutsample;


import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.view.LayoutInflater;
import android.util.Log;
public class MainActivity extends AppCompatActivity {

    private static final String BLURRED_IMG_PATH = "blurred_image.png";
    private static final int TOP_HEIGHT = 800;
    private ListView mList;
    private ImageView mBlurredImage;
    private View headerButtonView;
    private View headerView;
    private View stickView;
    private View stickTopView;
    private ImageView mNormalImage;
    //private ScrollableImageView mBlurredImageHeader;
    //private Switch mSwitch;
    private float alpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);//FEATURE_INDETERMINATE_PROGRESS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int screenWidth = ImageUtils.getScreenWidth(this);

        // Find the view
        mBlurredImage = (ImageView) findViewById(R.id.blurred_image);
        mNormalImage = (ImageView) findViewById(R.id.normal_image);
        stickTopView= (View) findViewById(R.id.layout_stick);

        stickTopView.setVisibility(View.INVISIBLE);
        mList = (ListView) findViewById(R.id.list);
        InitBackgroundView(screenWidth);

         // Try to find the blurred image
        final File blurredImage = new File(getFilesDir() + BLURRED_IMG_PATH);
        if (!blurredImage.exists()) {

            // launch the progressbar in ActionBar
            setProgressBarIndeterminateVisibility(true);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    // No image found => let's generate it!
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.image, options);
                    Bitmap newImg = Blur.fastblur(MainActivity.this, image, 12);
                    ImageUtils.storeImage(newImg, blurredImage);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            updateView(screenWidth);

                            // And finally stop the progressbar
                            setProgressBarIndeterminateVisibility(false);
                        }
                    });

                }
            }).start();

        } else {

            // The image has been found. Let's update the view
            updateView(screenWidth);

        }

        String[] strings = getResources().getStringArray(R.array.list_content);


        LayoutInflater inflater = (LayoutInflater)this.getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE);
       // headerView = inflater.inflate(R.layout.header_view, null);

        //stickView=(View) headerView.findViewById(R.id.layout_stick);
        stickView=inflater.inflate(R.layout.stickview, null);
        // Prepare the header view for our list
         headerView = new View(this);//header_view
         headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, TOP_HEIGHT));


        mList.addHeaderView(headerView);
        mList.addHeaderView(stickView);
       // stickView.setVisibility(View.INVISIBLE);
        //mList.addHeaderView(headerButtonView);


        mList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, strings));
        mList.setOnScrollListener(new OnScrollListener() {




            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * Listen to the list scroll. This is where magic happens ;)
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                alpha = (float) -headerView.getTop() / (float) TOP_HEIGHT;
                // Apply a ceil
                Log.v("stickView top", String.valueOf(stickView.getTop()));
                Log.v("firstVisibleItem", String.valueOf(firstVisibleItem));
                if (stickView.getTop()>0 && firstVisibleItem<2)
                {
                    //下
                    stickView.setVisibility(View.VISIBLE);
                    stickTopView.setVisibility(View.INVISIBLE);
                }else
                {
                    //上
                    stickView.setVisibility(View.INVISIBLE);
                    stickTopView.setVisibility(View.VISIBLE);

                }
                alpha*=2;
                if (alpha > 1) {
                    alpha = 1;
                }
                mBlurredImage.setAlpha(alpha);

                // Log.v("item", String.valueOf(firstVisibleItem));
                //Log.v("item", "jjjj");

                // Parallax effect : we apply half the scroll amount to our
                // three views
                mBlurredImage.setTop(headerView.getTop() / 2);
                mNormalImage.setTop(headerView.getTop() / 2);
                //mBlurredImageHeader.handleScroll(headerView.getTop() / 2);

            }
        });






    }

    private void InitBackgroundView(final int screenWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bmpNormal = BitmapFactory.decodeResource(getResources(), R.drawable.image, options);

        bmpNormal = Bitmap.createScaledBitmap(bmpNormal, screenWidth, (int) (bmpNormal.getHeight()
                * ((float) screenWidth) / (float) bmpNormal.getWidth()), false);
        mNormalImage.setImageBitmap(bmpNormal);

    }

    private void updateView(final int screenWidth) {
        Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir() + BLURRED_IMG_PATH);
        bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, screenWidth, (int) (bmpBlurred.getHeight()
                * ((float) screenWidth) / (float) bmpBlurred.getWidth()), false);
        //mNormalImage.setImageBitmap(bmpBlurred);
        mBlurredImage.setImageBitmap(bmpBlurred);

        //mBlurredImageHeader.setoriginalImage(bmpBlurred);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
