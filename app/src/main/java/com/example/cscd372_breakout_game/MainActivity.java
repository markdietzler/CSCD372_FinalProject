package com.example.cscd372_breakout_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //from break out game on web

        // Initialize gameView and set it as the view
        //breakoutView = new BreakoutView(this);
        //setContentView(breakoutView);

        //inflate activity main
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle action bar item clicks here
        int id = item.getItemId();
        if(id == R.id.action_about) {
            Toast.makeText(this,"Final Project, Winter 2019, Mark Dietzler", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class BreakoutView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // Game is gameIsPaused at the start
        boolean gameIsPaused = true;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;

        // The size of the screen in pixels
        int screenWidthInPixels;
        int screenHeightInPixels;

        // The player's playerPaddle
        Paddle playerPaddle;

        // A gameBall
        Ball gameBall;

        // Up to 200 arrayOfBricks
        Brick[] arrayOfBricks = new Brick[200];
        int numBricks = 0;

        // For sound FX
        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;

        // The score
        int score = 0;

        // Lives
        int lives = 3;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public BreakoutView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            screenWidthInPixels = size.x;
            screenHeightInPixels = size.y;

            playerPaddle = new Paddle(screenWidthInPixels, screenHeightInPixels);
            gameBall = new Ball(screenWidthInPixels, screenHeightInPixels);

            // Load the sounds

            // This SoundPool is deprecated but don't worry
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try{
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            }catch(IOException e){
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }

            createBricksAndRestart();
        }

        public void createBricksAndRestart() {

            // Put the gameBall back to the start
            gameBall.reset(screenWidthInPixels, screenHeightInPixels);

            int brickWidth = screenWidthInPixels / 10;
            int brickHeight = screenHeightInPixels / 15;

            // Build a wall of arrayOfBricks
            numBricks = 0;

            for(int column = 0; column < 10; column ++ ){
                for(int row = 0; row < 3; row ++ ){
                    arrayOfBricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks ++;
                }
            }

            //reset scores and lives
            score = 0;
            lives = 3;
        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                if(!gameIsPaused){
                    update();
                }

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }
        }

        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {

            // Move the playerPaddle if required
            playerPaddle.update(fps);

            gameBall.update(fps);

            // check for gameBall colliding with a brick
            for(int i = 0; i < numBricks; i++) {
                if(arrayOfBricks[i].getVisibility()) {
                    if (RectF.intersects(arrayOfBricks[i].getRect(), gameBall.getRect())) {
                        arrayOfBricks[i].setInvisible();
                        gameBall.reverseYVelocity();
                        score = score + 10;
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }

            // Check for gameBall colliding with playerPaddle
            if (RectF.intersects(playerPaddle.getRect(), gameBall.getRect())) {
                gameBall.setRandomXVelocity();
                gameBall.reverseYVelocity();
                gameBall.clearObstacleY(playerPaddle.getRect().top - 2);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }
            // Bounce the gameBall back when it hits the bottom of screen
            if (gameBall.getRect().bottom > screenHeightInPixels) {
                gameBall.reverseYVelocity();
                gameBall.clearObstacleY(screenHeightInPixels - 2);

                // Lose a life
                lives--;
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);

                if (lives == 0) {
                    gameIsPaused = true;
                    createBricksAndRestart();
                }
            }

            // Bounce the gameBall back when it hits the top of screen
            if (gameBall.getRect().top < 0)

            {
                gameBall.reverseYVelocity();
                gameBall.clearObstacleY(12);

                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            // If the gameBall hits left wall bounce
            if (gameBall.getRect().left < 0)

            {
                gameBall.reverseXVelocity();
                gameBall.clearObstacleX(2);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // If the gameBall hits right wall bounce
            if (gameBall.getRect().right > screenWidthInPixels - 10) {

                gameBall.reverseXVelocity();
                gameBall.clearObstacleX(screenWidthInPixels - 22);

                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // Pause if cleared screen
            if (score == numBricks * 10)

            {
                gameIsPaused = true;
                createBricksAndRestart();
            }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255,  26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw the playerPaddle
                canvas.drawRect(playerPaddle.getRect(), paint);

                // Draw the gameBall
                canvas.drawRect(gameBall.getRect(), paint);

                // Draw the arrayOfBricks
                // Change the brush color for drawing
                paint.setColor(Color.argb(255,  249, 129, 0));

                // Draw the arrayOfBricks if visible
                for(int i = 0; i < numBricks; i++){
                    if(arrayOfBricks[i].getVisibility()) {
                        canvas.drawRect(arrayOfBricks[i].getRect(), paint);
                    }
                }

                // Draw the HUD
                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);

                // Has the player cleared the screen?
                if(score == numBricks * 10){
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenHeightInPixels /2, paint);
                }

                // Has the player lost?
                if(lives <= 0){
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenHeightInPixels /2, paint);
                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // If SimpleGameEngine Activity is gameIsPaused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started theb
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:

                    gameIsPaused = false;

                    if(motionEvent.getX() > screenWidthInPixels / 2) {
                        playerPaddle.setMovementState(playerPaddle.RIGHT);
                    } else {
                        playerPaddle.setMovementState(playerPaddle.LEFT);
                    }

                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:

                    playerPaddle.setMovementState(playerPaddle.STOPPED);
                    break;
            }
            return true;
        }
    }// This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        breakoutView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        breakoutView.pause();
    }
}
