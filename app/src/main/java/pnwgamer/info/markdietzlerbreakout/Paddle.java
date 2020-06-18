package pnwgamer.info.markdietzlerbreakout;

import android.graphics.RectF;

public class Paddle {

    private RectF player_paddle;
    private float mPaddleLength;
    private float mPaddleHeight;
    private float topLeftXCoordinate;
    private float topLeftYCoordinate;
    private float mPaddleSpeed;
    private static final int STOPPED = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private int mPaddleMoving = STOPPED;


    public Paddle(float givenScreenWidth, float givenScreenHeight){
        mPaddleLength = 150;
        mPaddleHeight = 20;
        topLeftXCoordinate = (givenScreenWidth / 2) - (mPaddleLength / 2);
        topLeftYCoordinate = 20;
        player_paddle = new RectF(topLeftXCoordinate, topLeftYCoordinate, topLeftXCoordinate + mPaddleLength, topLeftYCoordinate + mPaddleHeight);
        mPaddleSpeed = 350;
    }


    public RectF getPaddleRectangle(){
        return player_paddle;
    }

    public void setPaddleSpeed(int newPaddleSpeed) {
        mPaddleSpeed = 300 + ( 25 * newPaddleSpeed);
    }


    public void setMovementState(int state){
        mPaddleMoving = state;
    }

    public void PaddleUpdate(long fps){
        if(mPaddleMoving == LEFT){
            topLeftXCoordinate = topLeftXCoordinate - mPaddleSpeed / fps;
        }

        if(mPaddleMoving == RIGHT){
            topLeftXCoordinate = topLeftXCoordinate + mPaddleSpeed / fps;
        }
        player_paddle.left = topLeftXCoordinate;
        player_paddle.right = topLeftXCoordinate + mPaddleLength;
    }
}
