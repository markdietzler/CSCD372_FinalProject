package com.example.cscd372_breakout_game;

import android.graphics.RectF;

public class Brick {
    private RectF rect;

    private boolean isVisible;

    private int HitCount;

    public Brick(int row, int column, int width, int height){

        isVisible = true;
        HitCount = 0;

        int padding = 1;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public void BrickIsHit() {
        if(HitCount < 3) {
            HitCount++;
        } else {
            setInvisible();
        }
    }

    public boolean getVisibility(){
        return isVisible;
    }
}
