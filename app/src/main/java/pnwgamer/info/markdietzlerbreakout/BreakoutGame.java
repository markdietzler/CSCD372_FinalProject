package pnwgamer.info.markdietzlerbreakout;

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Observable;

public class BreakoutGame extends View implements TimeAnimator.TimeListener {

    TimeAnimator mTimer = new TimeAnimator();
    LevelBallsScoreBricks mObserver = new LevelBallsScoreBricks();
    SoundPool soundPool;
    protected Paddle paddle;
    private Ball ball;
    private Brick[] bricks = new Brick[100];

    Paint mNo_Hit_Brick = new Paint();
    Paint mOne_Hit_Brick = new Paint();
    Paint mTwo_Hit_Brick = new Paint();
    Paint mThree_Hit_Brick = new Paint();



    //static variables
    final static float OVERALL_WIDTH = 100.0f;
    final static float OVERALL_HEIGHT = 100.0f;
    final static float ASPECT_RATIO = 1f;

    //variables to accept preference values, set here with default preferences.xml values
    private int mBricks = 5, mBalls = 3, mHits = 2, mPaddle = 15;

    //colors for the bricks, red -> yellow -> green -> blue for hits of count 0,1,2,3
    private final int no_hit_color = Color.RED;
    private final int one_hit_color = Color.YELLOW;
    private final int two_hit_color = Color.GREEN;
    private final int three_hit_color = Color.BLUE;

    //operating variables
    private long mFps;                 // This variable tracks the game frame rate
    private long timeThisFrame;        // This is used to help calculate the fps
    private int mWidth;                // view width
    private int mHeight;               // view height
    private int mNumber_of_bricks = 0; // running bricks count, reduced when brick is destroyed
    private int mCurrentBalls;         // running balls left count
    private int mCurrentLevel;         // running level state
    private int mScore = 0;            // game score

    //sound effects
    private int mMediumBeep = -1;
    private int mHighBeep = -1;
    private int mLowBeep = -1;
    private int passedball = -1;
    private int mKillBrick = -1;

    //booleans
    boolean mPaused = true;     // Game is paused at the start
    boolean mNewGame = false;   // set to true to restart game

    //constructors
    public BreakoutGame(Context context) {
        super(context);
        InitializeGameInstance(context);
    }

    public BreakoutGame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        InitializeGameInstance(context);
    }

    public BreakoutGame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitializeGameInstance(context);
    }

    //setters and getters
    public void setPaused(){
        mPaused = true;
    }

    public void unPause(){
        mPaused = false;
    }

    public int getBricks() {
        return mBricks;
    }

    public int getBalls() {
        return mBalls;
    }

    public int getHits() {
        return mHits;
    }

    public int getPaddle() {
        return mPaddle;
    }

    public void setBricks(int mBricks) {
        this.mBricks = mBricks;
    }

    public void setBalls(int mBalls) {
        this.mBalls = mBalls;
    }

    public void setHits(int mHits) {
        this.mHits = mHits;
    }

    public void setPaddle(int mPaddle) {
        this.mPaddle = mPaddle;
    }

    //draw the game after updates are done
    @Override
    public void onDraw(Canvas canvas) {
        // background color for game view
        canvas.drawColor(Color.argb(255,26,128,182));

        //draw the paddle
        //canvas.drawRect(paddle.getRect(), paint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        float wSize = View.MeasureSpec.getSize(widthMeasureSpec);
        float hSize = View.MeasureSpec.getSize(heightMeasureSpec);
        float width, height;
        height = wSize / ASPECT_RATIO;
        width = hSize * ASPECT_RATIO;
        if(height > hSize) {
            this.setMeasuredDimension((int)width, (int)hSize);
        } else {
            this.setMeasuredDimension((int)wSize,(int)height);
        }
    }

    //initialize the game when instantiated
    private void InitializeGameInstance(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mTimer.setTimeListener(this);
        paddle = new Paddle(mWidth, mHeight);
        ball = new Ball(mWidth,mHeight);
        mNo_Hit_Brick.setColor(no_hit_color);
        mOne_Hit_Brick.setColor(one_hit_color);
        mTwo_Hit_Brick.setColor(two_hit_color);
        mThree_Hit_Brick.setColor(three_hit_color);

        //load the sound effects
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("medbeep.ogg");
            mMediumBeep = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("highbeep.ogg");
            mHighBeep = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("lowbeep.ogg");
            mLowBeep = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("passedball.ogg");
            passedball = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("killbrick.ogg");
            mKillBrick = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("error","failed to load sound files");
        }

        //load the bricks

        startTimer();
    }

    public void startTimer() {
        mTimer.start();
    }

    public void update() {
        //update ball

        //update paddle
        paddle.update(mFps);

        //update bricks

    }

    public void Game_Init_Or_Restart() {
        //put the ball back to the start position
        ball.reset(mWidth, mHeight);

        int brickWidth = mWidth/10;
        int brickHeight = mHeight/11;

        //build bricks
        mNumber_of_bricks = 0;
        for (int column = 0; column < 10 ; column++) {

            for (int row = 0 ; row < 3 ; row++) {

                bricks[mNumber_of_bricks] = new Brick(row, column, brickWidth,brickHeight);
                mNumber_of_bricks++;

            }
        }

        mCurrentBalls = mBalls;
        mScore = 0;
        mCurrentLevel = 1;
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        //do all position updates, collision detection, and variable updates
        long startUpdate = System.currentTimeMillis();

        //if we are paused, no updates are required, so skip updating
        if(!mPaused) {
            update();
        }

        //draw the frame
        invalidate();

        //calculate the fps for this frame
        timeThisFrame = System.currentTimeMillis() - startUpdate;

        if (timeThisFrame >= 1) {
            mFps = 1000 / timeThisFrame;
        }


    }

    public class LevelBallsScoreBricks extends Observable {
        int level, balls, score, bricks;
        public int getLevel(){
            return level;
        }
        public int getBalls() {
            return balls;
        }
        public int getScore() {
            return score;
        }
        public int getBricks() {
            return bricks;
        }
        public void setScoreboard(int newLevel, int newBalls, int newScore, int newBricks) {
            level = newLevel;
            balls = newBalls;
            score = newScore;
            bricks = newBricks;
            setChanged();
            notifyObservers("Game");
        }
    }
}
