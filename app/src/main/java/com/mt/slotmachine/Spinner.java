package com.mt.slotmachine;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 * Created by evo on 1/4/2016.
 */
public class Spinner {

    private ViewFlipper mViewFlipper;
    public long mSpinSpeed;
    private boolean mAnimating;
    private boolean mStopped;


    public Spinner(ViewFlipper flip, long speed) {

        mViewFlipper= flip;
        mSpinSpeed = speed;
        mAnimating = false;
        mStopped = true;

    }
    public void Spin () {

        //mAnimating = true;
        mStopped = false;

        Animation outToBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);

        if (mAnimating)
        outToBottom.setInterpolator(new LinearInterpolator());
        else
        outToBottom.setInterpolator(new DecelerateInterpolator());


        outToBottom.setDuration(mSpinSpeed);

        Animation inFromTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        if (mAnimating)
        inFromTop.setInterpolator(new LinearInterpolator());
        else
        inFromTop.setInterpolator(new DecelerateInterpolator());

        inFromTop.setDuration(mSpinSpeed);


        mViewFlipper.clearAnimation();
        mViewFlipper.setInAnimation(inFromTop);
        mViewFlipper.setOutAnimation(outToBottom);
        //outToBottom.setDuration(mSpeed + 1000);
        //inFromTop.setDuration(mSpeed + 1000);

        if (mViewFlipper.getDisplayedChild()==0) {
            mViewFlipper.setDisplayedChild(3);
        } else {
            mViewFlipper.showPrevious();
        }

        //animation finished? /looper?

    }

    public void Start() {
        mAnimating = true;

    }

    public void Stop() {

        mAnimating = false;

        Runnable stop = new Runnable() {
            public void run() {
                mStopped = true;
            }
        };

        new Handler().postDelayed(stop,1000);

    }



    public int GetFruit(){

        return mViewFlipper.getDisplayedChild();

    }




    public boolean isAnimating() {
        return mAnimating;
    }

    public boolean Stopped() {
        return mStopped;
    }

    public long getSpeed () {
        return mSpinSpeed;
    }
}
