package com.example.cscd372_breakout_game;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Observable;

public class BreakOutView extends View implements TimeAnimator.TimeListener {

    TimeAnimator mTimer = new TimeAnimator();

    public BreakOutView(Context context) {
        super(context);
        initializeMe();
    }

    public BreakOutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeMe();
    }

    public BreakOutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeMe();
    }

    private void initializeMe() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        startTimer();
    }

    @Override
    public void onDraw(Canvas canvas) {
        //draw objects here
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        float widthDimension = View.MeasureSpec.getSize(widthMeasureSpec);
        float heightDimension = View.MeasureSpec.getSize(heightMeasureSpec);
        float width,height;

    }

    public void startTimer() {
        mTimer.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                invalidate();
            }
        });
        mTimer.start();
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        invalidate();
    }

    class FrontPanelStats extends Observable {
        int balls, score, level;

        public int getBalls() {
            return balls;
        }

        public int getScore() {
            return score;
        }

        public int getLevel() {
            return level;
        }

        public void setPanel(int b,int s, int l) {
            balls = b;
            score = s;
            level = l;
            setChanged();
            notifyObservers("BreakOutView");
        }
    }
}
