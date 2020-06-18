package pnwgamer.info.markdietzlerbreakout;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    private RectF ball_rectangle;
    private float xVelocity;
    private float yVelocity;
    private float defaultStartHeight = 40.0f;
    private float ballWidth = 10;
    private float ballHeight = 10;

    public Ball(int screenX, int screenY){

        // Start the ball travelling straight up at 100 pixels per second
        xVelocity = 200;
        yVelocity = -400;

        // Place the ball in the centre of the screen at the bottom
        // Make it a 10 pixel x 10 pixel square
        ball_rectangle = new RectF(screenX/2 -5 , defaultStartHeight ,screenX/2 - 5 + ballWidth,defaultStartHeight + ballHeight);

    }

    public RectF getBallRectangle(){
        return ball_rectangle;
    }

    public void UpdateBall(long fps){
        ball_rectangle.left = ball_rectangle.left + (xVelocity / fps);
        ball_rectangle.top = ball_rectangle.top + (yVelocity / fps);
        ball_rectangle.right = ball_rectangle.left + ballWidth;
        ball_rectangle.bottom = ball_rectangle.top - ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = yVelocity * -1;
    }

    public void reverseXVelocity(){
        xVelocity = xVelocity * -1;
    }

    public void addRight(float speed){
        xVelocity = xVelocity + speed;
    }

    public void addLeft(float speed){
        xVelocity = xVelocity = speed;
    }

    public void reset(int screenX, int screenY){
        xVelocity = new Random().nextInt(200 + 200) - 200;
        yVelocity = 400;
        ball_rectangle.left = screenX / 2 - 5;
        ball_rectangle.top = defaultStartHeight + 20;
        ball_rectangle.right = screenX / 2 - 5 + ballWidth;
        ball_rectangle.bottom = defaultStartHeight + ballHeight + 20;
    }
}
