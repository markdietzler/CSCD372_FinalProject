package com.example.cscd372_breakout_game;

import android.graphics.RectF;

public class Paddle {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    private static final int PADDLE_WIDTH = 130;
    private static final int PADDLE_HEIGHT = 20;

    private int screenLeftEdge = 0 + PADDLE_WIDTH;
    private int screenRightEdge = PADDLE_WIDTH;
    private int screenWidth;
    private int screenHeight;

    // How long and high our playerPaddle will be
    private float length;
    private float height;

    // This is the top left co-ordinate in the x plane of the rectangle which forms our player Paddle
    private float x;

    // This is the top left co-ordinate in the Y plane of the rectangle which forms our player paddle
    private float y;

    // This will hold the pixels per second speed that the playerPaddle will move
    private float paddleSpeed;

    // Which ways can the playerPaddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the playerPaddle moving and in which direction
    private int paddleMoving = STOPPED;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int incomingScreenWidth, int incomingScreenHeight){

        this.screenWidth = incomingScreenWidth;
        this.screenHeight = incomingScreenHeight;

        // 130 pixels wide and 20 pixels high
        length = PADDLE_WIDTH;
        height = PADDLE_HEIGHT;

        // Start playerPaddle in roughly the screen centre
        x = (incomingScreenWidth / 2)-65;
        y = incomingScreenHeight - 20;

        rect = new RectF(x, y, x + length, y + height);

        // How fast is the playerPaddle in pixels per second
        paddleSpeed = 350;
    }

    // This is a getter method to make the rectangle that
    // defines our playerPaddle available in BreakoutView class
    public RectF getRect(){
        return rect;
    }

    // This method will be used to change/set if the playerPaddle is going left, right or nowhere
    public void setMovementState(int state){
        paddleMoving = state;
    }

    // This update method will be called from update in BreakoutView
    // It determines if the playerPaddle needs to move and changes the coordinates
    // contained in rect if necessary
    public void update(long fps){
        if(paddleMoving == LEFT && !IsPaddleAtLeftEdge()){

            x = x - paddleSpeed / fps;
        }

        if(paddleMoving == RIGHT && !IsPaddleAtRightEdge()){
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

    /**
     * Checks if the top left coordinate of the paddle is off the screen
     * @return True if it is, false if it is not
     */

    public boolean IsPaddleAtRightEdge() {
        if(x >= (screenWidth-PADDLE_WIDTH))
            return true;
        return false;
    }

    /**
     * Checks if the top left coordinate of the paddle is at the calculated right screen edge
     * @return True if it is, false if it is not
     */
    public boolean IsPaddleAtLeftEdge() {
        if(x <= 0)
            return true;
        return false;
    }

}
