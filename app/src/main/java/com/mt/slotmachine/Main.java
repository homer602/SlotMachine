package com.mt.slotmachine;

//import android.app.Activity;
//import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mt.common.activities.SampleActivityBase;
import com.mt.common.logger.Log;
import com.mt.common.logger.LogFragment;
import com.mt.common.logger.LogWrapper;
import com.mt.common.logger.MessageOnlyLogFilter;


public class Main extends SampleActivityBase {

    private boolean mLogShown;

	private ViewFlipper[] mViewFlipper = new ViewFlipper[4];

	private Spinner[] Spinner = new Spinner[4];
	private int BASE_SPEED = 350;
	//private long spinSpeed[] = new long[4];
    ImageProvider.ImagePair[] ip;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.commit();
        }

        findViewById(R.id.button).setEnabled(false);
        findViewById(R.id.button2).setEnabled(false);

        mViewFlipper[1] = (ViewFlipper) findViewById(R.id.FLIPPER_1);
        mViewFlipper[2] = (ViewFlipper) findViewById(R.id.FLIPPER_2);
        mViewFlipper[3] = (ViewFlipper) findViewById(R.id.FLIPPER_3);

        Spinner[1] = new Spinner((ViewFlipper) findViewById(R.id.FLIPPER_1),BASE_SPEED);
        Spinner[2] = new Spinner((ViewFlipper) findViewById(R.id.FLIPPER_2),BASE_SPEED);
        Spinner[3] = new Spinner((ViewFlipper) findViewById(R.id.FLIPPER_3),BASE_SPEED);


        final AsyncTask<Void, Void, ImageProvider.ImagePair[]> execute = new AsyncTask<Void, Void, ImageProvider.ImagePair[]>(

        ) {
            @Override
            protected ImageProvider.ImagePair[] doInBackground(Void... params) {

                try {
                    ip = ImageProvider.parseURL(getString(R.string.image_source));

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                return  null;

              //  return ip;
            }

            @Override
            protected void onPostExecute(ImageProvider.ImagePair[] ip) {

                if (ip==null) {

                    Toast.makeText(Main.this, R.string.download_error, Toast.LENGTH_LONG).show();
                    Log.d ("mich","Connection error");
                    return;
                }
                else {
                    Toast.makeText(Main.this, R.string.download_success, Toast.LENGTH_SHORT).show();
                }

                LinearLayout tmpLayout;
                ImageView miv;

                for (int i=0;i<4;i++) {
                    tmpLayout = (LinearLayout) mViewFlipper[1].getChildAt(i);
                    miv = (ImageView) tmpLayout.getChildAt(0);
                    miv.setImageBitmap(ip[i].bm);
                    tmpLayout = (LinearLayout) mViewFlipper[2].getChildAt(i);
                    miv = (ImageView) tmpLayout.getChildAt(0);
                    miv.setImageBitmap(ip[i].bm);
                    tmpLayout = (LinearLayout) mViewFlipper[3].getChildAt(i);
                    miv = (ImageView) tmpLayout.getChildAt(0);
                    miv.setImageBitmap(ip[i].bm);
                }

                findViewById(R.id.button).setEnabled(true);
                findViewById(R.id.button2).setEnabled(true);

            }


        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

    public class SpinRunner implements Runnable {
        private int val;

        public SpinRunner(int val) {
            this.val = val;
        }
        public void run() {
            Spinner spindle = Spinner[val];
            spindle.Spin();
            if (spindle.isAnimating()) {
                Handler h = new Handler();
                h.postDelayed(this, spindle.getSpeed());
            }

        }

    }

    private  Runnable r1;
    private  Runnable r2;
    private  Runnable r3;


	public void SpinUp(View view) {


        if (Spinner[1].Stopped()) {

            findViewById(R.id.button).setEnabled(false);
            ((TextView)findViewById(R.id.sometext)).setText("");

            r1 = new SpinRunner(1);

            Spinner[1].mSpinSpeed = (long) (BASE_SPEED/3.5); //2.5?
            Spinner[1].Start();

            r1.run();

            r2 = new SpinRunner(2);
            Spinner[2].mSpinSpeed = BASE_SPEED/2;
            Spinner[2].Start();
            r2.run();
            //new Handler().postDelayed(r2, (long) (BASE_SPEED*1.5));

            r3 = new SpinRunner(3);
            Spinner[3].mSpinSpeed = BASE_SPEED/3;
            Spinner[3].Start();
            r3.run();
            //new Handler().postDelayed(r3, (long) (BASE_SPEED*2.5));
        }
	}


    public void SpinDown(View view) {

        if (Spinner[1].Stopped()) return;


        new Handler().post(new Runnable() {
            @Override
            public void run() {

                Spinner[1].mSpinSpeed = BASE_SPEED;
                Spinner[1].Stop();

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Spinner[2].mSpinSpeed = BASE_SPEED;
                Spinner[2].Stop();

            }
        }, BASE_SPEED);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Spinner[3].mSpinSpeed = BASE_SPEED;
                Spinner[3].Stop();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        EvaluateResults();

                        findViewById(R.id.button).setEnabled(true);

                    }
                }, 500);

            }
        }, BASE_SPEED * 2);

    }

    private void EvaluateResults() {

        String tmp = String.valueOf(Spinner[1].GetFruit())
                   + String.valueOf(Spinner[2].GetFruit())
                   + String.valueOf(Spinner[3].GetFruit());
        boolean result = CheckScore(tmp);

        //((TextView)findViewById(R.id.sometext)).setText("" + tmp + result);


        String resultString = ip[Spinner[1].GetFruit()].name+" " +ip[Spinner[2].GetFruit()].name+" "+ip[Spinner[3].GetFruit()].name;
        if (result) {
            resultString += " VYHRA!"; // dat text ze string resources?
            ((TextView)findViewById(R.id.sometext)).setText(R.string.win);
        }
        PublishResults(resultString);


    }

    private boolean CheckScore(String result) {

        List gamerules = new ArrayList();
        String[] grules = getResources().getStringArray(R.array.game_rules);
        Collections.addAll(gamerules, grules);

//        gamerules.add("000");
//        gamerules.add("111");
//        gamerules.add("222");
//        gamerules.add("333");

        return gamerules.contains(result);

    }


    private void PublishResults(String resultString) {

        //Log.println(0,"VYSLEDEK:", resultString);
        Log.d("VYSLEDEK", resultString);

    }

}