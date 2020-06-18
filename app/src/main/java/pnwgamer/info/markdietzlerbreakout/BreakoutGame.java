package pnwgamer.info.markdietzlerbreakout;

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Random;
import java.util.Set;

public class BreakoutGame extends View implements TimeAnimator.TimeListener {

    //static variables
    private final static int DEFAULT_BALLS = 4;
    private final static int DEFAULT_HITS = 4;
    private final static int DEFAULT_BRICKS = 9;
    private final static int MAX_BRICKS = 100;
    private final static int MAX_BRICKS_INDEX = 99;
    private final static int MIN_BRICKS_INDEX = 0;
    private final static long START_FPS = 120;
    private final static long LEVEL_SPEED_INCREASE = 30;
    private final static float ASPECT_RATIO = 1f;
    private final static String PADDLE_COLOR ="#B5E0A3";
    private final static String BALL_COLOR ="#f59ee3";

    private TimeAnimator mTimer = new TimeAnimator();
    protected LevelBallsScoreBricks mBreakoutGameObserver = new LevelBallsScoreBricks();
    private SoundPool soundPool;
    protected Paddle mPlayerPaddle;
    private Ball mPlayerBall;
    private Brick[] mBrickArray = new Brick[MAX_BRICKS];
    private boolean[] mBrick_Draw_Array = new boolean[MAX_BRICKS];

    private Paint mNo_Hit_Brick = new Paint();
    private Paint mOne_Hit_Brick = new Paint();
    private Paint mTwo_Hit_Brick = new Paint();
    private Paint mThree_Hit_Brick = new Paint();
    private Paint mPaddle_attributes = new Paint();
    private Paint mBall_attributes = new Paint();
    private Paint mText = new Paint();

    //colors for the bricks, red -> yellow -> green -> blue for hits of count 0,1,2,3
    private final int no_hit_color = Color.RED;
    private final int one_hit_color = Color.YELLOW;
    private final int two_hit_color = Color.GREEN;
    private final int three_hit_color = Color.BLUE;

    //variables to accept preference values, set here with default preferences.xml values
    private int mPreferences_Number_of_Bricks;
    private int mPreferences_Number_of_Balls;
    private int mPreferences_Hits_To_Destroy_Brick;

    //operating variables
    private long mFps;               // This variable tracks the game frame rate
    private int mWidth;              // view width, set with onViewMeasured, which runs AFTER constructor
    private int mHeight;             // view height, set with onViewMeasured, which runs AFTER constructor
    private int mScoreboard_Bricks;  // running bricks count in level
    private int mScoreboard_Balls;   // running count of balls remaining
    private int mScoreboard_Level;   // running level state
    private int mScoreboard_Score;   // running game score
    private int mBricks_Remaining;   // counts active bricks, when 0 starts new level

    //sound effects
    private int mMediumBeep = -1;
    private int mHighBeep = -1;
    private int mLowBeep = -1;
    private int mPassed_ball = -1;
    private int mDestroyed_Brick = -1;

    //booleans
    private boolean mPaused = true;     // Game is paused at the start
    private boolean mNewGame = false;   // set to true to restart game
    private boolean mLevelUp = false;   // we've cleared the board

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

    public void PausedToggle() {
        if(mPaused) {
            mPaused = false;
        } else {
            mPaused = true;
        }
    }

    public boolean getPaused() {
        return mPaused;
    }

    public int GetDefaultBricks() {
        return DEFAULT_BRICKS;
    }

    public int GetDefaultBalls() {
        return DEFAULT_BALLS;
    }

    public int GetDefaultHits() {
        return DEFAULT_HITS;
    }

    public void setPaused(boolean newState) {
        mPaused = newState;
    }

    public void setNewGame(boolean newGame) {
        mNewGame = newGame;
    }

    public int getPreferences_Bricks() {
        return mPreferences_Number_of_Bricks;
    }

    public int getPreferences_Balls() {
        return mPreferences_Number_of_Balls;
    }

    public int getPreferences_Hits() {
        return mPreferences_Hits_To_Destroy_Brick;
    }

    public void setPreferences_Bricks(int mBricks) {
        this.mPreferences_Number_of_Bricks = mBricks;
    }

    public void setPreferences_Balls(int mBalls) {
        this.mPreferences_Number_of_Balls = mBalls;
    }

    public void setPreferences_Hits(int mHits) {
        this.mPreferences_Hits_To_Destroy_Brick = mHits;
    }

    public void set_Game_Score(int newScore) {
        mScoreboard_Score = newScore;
    }

    public void set_Game_Balls(int newBalls) {
        mScoreboard_Balls = newBalls;
    }

    public void set_Game_Level(int newLevel) {
        mScoreboard_Level = newLevel;
    }

    public void set_Game_Bricks(int oldBricks) {
        mScoreboard_Bricks = oldBricks;
    }

    public boolean[] getGamestate_booleans() {
        return mBrick_Draw_Array;
    }

    public void setGamestate_booleans(boolean[] oldState) {
        mBrick_Draw_Array = oldState;
    }

    public Brick[] getGamestate_Brickarray() {
        return mBrickArray;
    }

    public void setGamestate_Brickarray(Brick[] oldBricks) {
        mBrickArray = oldBricks;
    }

    //draw the game after updates are done
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // background color for game view
        canvas.drawColor(Color.BLACK);

        //set up the canvas for use
        float height = getHeight();
        canvas.scale(ASPECT_RATIO , -ASPECT_RATIO);
        canvas.save();
        canvas.translate( 0.0f, -height);

        //draw the paddle
        canvas.drawRect(mPlayerPaddle.getPaddleRectangle(), mPaddle_attributes);

        //draw the ball
        canvas.drawRect(mPlayerBall.getBallRectangle(), mBall_attributes);

        //draw the bricks array
        for(int i = 0 ; i < MAX_BRICKS_INDEX ; i++) {
            if(mBrickArray[i] == null) {
                //skip drawing the brick
            } else {

                //if brick is "visible", draw it with the appropriate brick color
                if(mBrickArray[i].getVisibility()) {
                    if(mBrickArray[i].Get_Hits() == 0) {
                        canvas.drawRect(mBrickArray[i].getGameBrick(),mNo_Hit_Brick);
                    }
                    if(mBrickArray[i].Get_Hits() == 1) {
                        canvas.drawRect(mBrickArray[i].getGameBrick(),mOne_Hit_Brick);
                    }
                    if(mBrickArray[i].Get_Hits() == 2) {
                        canvas.drawRect(mBrickArray[i].getGameBrick(),mTwo_Hit_Brick);
                    }
                    if(mBrickArray[i].Get_Hits() == 3) {
                        canvas.drawRect(mBrickArray[i].getGameBrick(),mThree_Hit_Brick);
                    }

                } //end of drawing visible bricks

            } //end of non-null bricks

        } //end of loop

        if(mPaused) {
            canvas.drawText("PAUSED",mWidth / 2 - 100, mHeight/2, mText);
        }

        if(mPaused && mNewGame) {
            canvas.drawText("PAUSED",mWidth / 2 - 100, mHeight/2, mText);
            canvas.drawText("GAME OVER",mWidth / 2 - 100, mHeight/2 + 100, mText);
        }

        if(mPaused && mLevelUp) {
            canvas.drawText("PAUSED",mWidth / 2 - 100, mHeight/2, mText);
            canvas.drawText("LEVEL UP!",mWidth / 2 - 100, mHeight/2 + 100, mText);
        }
    }

    //runs AFTER constructor
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        mWidth = w;
        mHeight = h;
        BallAndPaddleInit(mWidth,mHeight);
        NewGame(mWidth,mHeight);
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

        //set colors
        mNo_Hit_Brick.setColor(no_hit_color);
        mOne_Hit_Brick.setColor(one_hit_color);
        mTwo_Hit_Brick.setColor(two_hit_color);
        mThree_Hit_Brick.setColor(three_hit_color);
        mPaddle_attributes.setColor(Color.parseColor(PADDLE_COLOR));
        mBall_attributes.setColor(Color.parseColor(BALL_COLOR));
        mText.setColor(Color.RED);
        mText.setTextSize(60);

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
            mPassed_ball = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("killbrick.ogg");
            mDestroyed_Brick = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("error","failed to load sound files");
        }

        //generate the random indices where bricks will be in the array
        GenerateBrickIndexes();

        //set defaults for balls, brick hits, # of bricks
        // and running score, balls, bricks for INITIAL run
        mPreferences_Hits_To_Destroy_Brick = DEFAULT_HITS;
        mPreferences_Number_of_Bricks = DEFAULT_BRICKS;
        mPreferences_Number_of_Balls = DEFAULT_BALLS;
        mScoreboard_Balls = mPreferences_Number_of_Balls;
        mScoreboard_Bricks = mPreferences_Number_of_Bricks;
        mBricks_Remaining = mPreferences_Number_of_Bricks;
        mScoreboard_Level = 0;
        mScoreboard_Score = 0;
        mFps = START_FPS;
        startTimer();
    }

    private void BallAndPaddleInit(int boardWidth, int boardHeight) {
        mPlayerPaddle = new Paddle(boardWidth , boardHeight);
        mPlayerBall = new Ball(boardWidth , boardHeight);
    }

    public void startTimer() {
        mTimer.start();
    }

    //does all "movement" position updates, collision checking, and game state
    //checking, i.e. game over, new level
    public void update() {

        //update ball
        mPlayerBall.UpdateBall(mFps);

        //update paddle
        mPlayerPaddle.PaddleUpdate(mFps);

        //bricks and ball collision check
        for (int brickArrayWalker = 0; brickArrayWalker < MAX_BRICKS_INDEX; brickArrayWalker++) {

            //if brick array says there is a brick at the index
            if(mBrick_Draw_Array[brickArrayWalker]) {

                //if brick is visible
                if(mBrickArray[brickArrayWalker].getVisibility()) {

                    //if brick and ball intersect
                    if(RectF.intersects(mBrickArray[brickArrayWalker].getGameBrick(),mPlayerBall.getBallRectangle())) {

                        //if brick is at max hits
                        if(mBrickArray[brickArrayWalker].Get_Hits() == mPreferences_Hits_To_Destroy_Brick) {

                            //"destroy" the brick
                            mBrickArray[brickArrayWalker].setInvisible();

                            //decrement bricks
                            mScoreboard_Bricks--;

                            //increment score
                            mScoreboard_Score = mScoreboard_Score + 50;

                            //play destroyed brick sound
                            soundPool.play(mDestroyed_Brick,1,1,0,0,1);

                        } else { //brick NOT destroyed

                            //increment score
                            mScoreboard_Score++;

                            //increment hits
                            mBrickArray[brickArrayWalker].Increment_Hits();

                            //play brick bounce sound
                            soundPool.play(mHighBeep,1,1,0,0,1);
                        }

                        //because we hit a brick, reverse ball Y velocity (bounce)
                        mPlayerBall.reverseYVelocity();

                    }
                }

                //brick wasn't visible, can't collision check an invisible brick
            }

        } //end of brick and ball collision check

        //ball and paddle collision check
        if(RectF.intersects(mPlayerBall.getBallRectangle(), mPlayerPaddle.getPaddleRectangle())) {

            //simulate paddle physics
            if(mPlayerBall.getBallRectangle().left > mPlayerPaddle.getPaddleRectangle().left + 30) {
                mPlayerBall.addLeft(40.0f);
            }
            if(mPlayerBall.getBallRectangle().left > mPlayerPaddle.getPaddleRectangle().left + 60) {
                mPlayerBall.addLeft(20.0f);
            }
            if(mPlayerBall.getBallRectangle().right < mPlayerPaddle.getPaddleRectangle().right - 30) {
                mPlayerBall.addRight(-40.0f);
            }
            if(mPlayerBall.getBallRectangle().right < mPlayerPaddle.getPaddleRectangle().right - 60) {
                mPlayerBall.addRight(-20.0f);
            }

            mPlayerBall.reverseYVelocity();
            //mPlayerBall.setRandomXVelocity();
            //ball.clearObstacleY(paddle.getRect().top -2);
            soundPool.play(mMediumBeep,1,1,0,0,1);
        }

        //ball and top of screen
        if(mPlayerBall.getBallRectangle().top > mHeight) {
            mPlayerBall.reverseYVelocity();
            //ball.clearObstacleY(12);
            soundPool.play(mLowBeep,1,1,0,0,1);
        }

        //ball and bottom of screen collision
        if(mPlayerBall.getBallRectangle().top < 0) {

            //play passed ball sound
            soundPool.play(mPassed_ball,1,1,0,0,1);

            //if there are no more spare player balls
            if(mScoreboard_Balls == 0) {
                GameOver();
            } else {
                //deduct ball
                mScoreboard_Balls--;
            }
            mPlayerBall.reset(mWidth,mHeight);
            setPaused(true);
        }

        //ball and left of screen
        if(mPlayerBall.getBallRectangle().left < 0) {
            mPlayerBall.reverseXVelocity();
            //ball.clearObstacleX(2);
            soundPool.play(mLowBeep,1,1,0,0,1);
        }

        //ball and right of screen
        if(mPlayerBall.getBallRectangle().right > (mWidth - 1)) {
            mPlayerBall.reverseXVelocity();
            //ball.clearObstacleX(mWidth - 12);
            soundPool.play(mLowBeep,1,1,0,0,1);
        }

        //if all bricks destroyed, level up
        if(mScoreboard_Bricks == 0) {
            mFps = mFps + LEVEL_SPEED_INCREASE;
            NextLevel();
            setPaused(true);
        }

        //send updated data to score board
        mBreakoutGameObserver.setScoreboard(mScoreboard_Level, mScoreboard_Balls, mScoreboard_Score, mScoreboard_Bricks);
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        //do all position updates, collision detection, and variable updates

        //if we are paused, no updates are required, so skip updating
        if(!mPaused) {
            update();
        }

        //prompt onDraw by invalidating the current canvas
        invalidate();
    }

    //observer class for updating main activities scoreboard text view
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

    public void NewGame(int boardWidth, int boardLength) {

        float brickWidth = mWidth/10.0f;
        float brickHeight = mHeight/27.15f;

        //build bricks
        int buildBrickIndex = 0;
        for (int column = 0; column < 10 ; column++) {

            for (int row = 0 ; row < 10 ; row++) {
                if(mBrick_Draw_Array[buildBrickIndex]) {
                    //create brick
                    mBrickArray[buildBrickIndex] = new Brick(row , column , brickWidth , brickHeight, boardWidth, boardLength);
                    //set allowed brick hits
                    mBrickArray[buildBrickIndex].SetMaxHits(mPreferences_Hits_To_Destroy_Brick);
                    //set brick to visible
                    mBrickArray[buildBrickIndex].SetVisible();
                } else {
                    //DONT build a brick
                }

                //increment drawn brick count
                buildBrickIndex++;
            }
        }

        mScoreboard_Score = 0;
        mScoreboard_Level = 1;
    }

    private void NextLevel() {
        mScoreboard_Level++;
        mScoreboard_Bricks = mPreferences_Number_of_Bricks + mScoreboard_Level;

        //reset all brick boolean arrays to false
        ResetBrickBooleanArray();

        //reset brick array to all null
        ResetBricksArray();

        //generate new random brick indexes to true
        GenerateBrickIndexes();

        //restart the game board
        NewGame(mWidth, mHeight);
        mLevelUp = true;

    }

    private void GameOver() {

        mScoreboard_Bricks = mPreferences_Number_of_Bricks;
        mScoreboard_Balls = mPreferences_Number_of_Balls;
        //reset all brick boolean arrays to false
        ResetBrickBooleanArray();

        //reset brick array to all null
        ResetBricksArray();

        //generate new random brick indexes to true
        GenerateBrickIndexes();

        //restart the game board
        NewGame(mWidth, mHeight);

        mNewGame = true;
    }

    private void GenerateBrickIndexes() {

        final Random rn = new Random();
        int numberOfBrickIndexesToEnable;

        //because of that pesky array start at 0 thing, we need to make this check
        //to make sure we aren't starting with +1 bricks over our current max brick setting
        if(mScoreboard_Level == 1) {
            numberOfBrickIndexesToEnable = mPreferences_Number_of_Bricks;
        } else {
            numberOfBrickIndexesToEnable = mScoreboard_Level + mPreferences_Number_of_Bricks;
        }

        //make sure we can have no more than 100 bricks
        if(numberOfBrickIndexesToEnable > MAX_BRICKS) {
            numberOfBrickIndexesToEnable = MAX_BRICKS;
        }

        //generate specified number of random brick indices
        final Set<Integer> distinctIndexes = new HashSet<Integer>();
        while (distinctIndexes.size() < numberOfBrickIndexesToEnable) {
            distinctIndexes.add(rn.nextInt(MAX_BRICKS) + MIN_BRICKS_INDEX);
        }

        //set generated brick indices to true
        for (int g : distinctIndexes) {
            mBrick_Draw_Array[g] = true;
        }
    }

    //reset the bricks boolean array to all false
    private void ResetBrickBooleanArray() {
        for(int c = 0 ; c < mBrick_Draw_Array.length ; c++) {
            mBrick_Draw_Array[c] = false;
        }
    }

    //reset the bricks array to all null
    private void ResetBricksArray() {
        for(int d = 0 ; d < mBrickArray.length ; d++) {
            mBrickArray[d] = null;
        }
    }
}
