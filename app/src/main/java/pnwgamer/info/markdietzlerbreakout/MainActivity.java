package pnwgamer.info.markdietzlerbreakout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, Observer {

    BreakoutGame mBreakoutGame;
    TextView mScoreBoard;
    Button mLeftPaddle;
    Button mRightPaddle;

    private static final int PADDLE_STOP = 0;
    private static final int PADDLE_LEFT = 1;
    private static final int PADDLE_RIGHT = 2;

    //this is fine
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBreakoutGame = (BreakoutGame)findViewById(R.id.view);
        mScoreBoard = (TextView)findViewById(R.id.score_board);
        mLeftPaddle = (Button)findViewById(R.id.left_paddle);
        mRightPaddle = (Button)findViewById(R.id.right_paddle);
        mBreakoutGame.mObserver.addObserver(this);

        mBreakoutGame.setOnTouchListener(this);
        mLeftPaddle.setOnTouchListener(this);
        mRightPaddle.setOnTouchListener(this);
    }

    //this is fine
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //this is fine
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        if(id == R.id.action_about) {
            Toast.makeText(this, "Breakout, Spring 2020, Mark Dietzler",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

        if(id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAbout(){
        Toast.makeText(this, "Breakout, Spring 2020, Mark Dietzler",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void update(Observable observableInput, Object obsTimeTransfer) {
        String scoreBoard = new String();
        if(observableInput != null) {
            int level, balls, score, bricks;
            level = mBreakoutGame.mObserver.getLevel();
            balls = mBreakoutGame.mObserver.getBalls();
            score = mBreakoutGame.mObserver.getScore();
            bricks = mBreakoutGame.mObserver.getBricks();
            scoreBoard = String.format("Level: %03d Balls: %02d Score:%08d Bricks:%03d",level,balls,score,bricks );
            mScoreBoard.setText(scoreBoard);
        } else {

        }
    }

    @Override
    public boolean onTouch(View viewBeingTouched, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            if(viewBeingTouched == findViewById(R.id.view)) {
                mBreakoutGame.setPaused();
            }

            if(viewBeingTouched == findViewById(R.id.left_paddle)){
                mBreakoutGame.paddle.setMovementState(PADDLE_LEFT);
            }

            if(viewBeingTouched == findViewById(R.id.right_paddle)) {
                mBreakoutGame.paddle.setMovementState(PADDLE_RIGHT);
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {

            if(viewBeingTouched == findViewById(R.id.view)) {
                mBreakoutGame.unPause();
            }

            if(viewBeingTouched == findViewById(R.id.left_paddle)){
                mBreakoutGame.paddle.setMovementState(PADDLE_STOP);
            }

            if(viewBeingTouched == findViewById(R.id.right_paddle)) {
                mBreakoutGame.paddle.setMovementState(PADDLE_STOP);
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int prefs_bricks = 0;
        int prefs_hits = 0;
        int prefs_ball_count = 0;
        int prefs_paddle = 0;

        prefs_bricks = prefs.getInt("", mBreakoutGame.getBricks());
        prefs_hits = prefs.getInt("", mBreakoutGame.getHits());
        prefs_ball_count = prefs.getInt("", mBreakoutGame.getBalls());
        prefs_paddle = prefs.getInt("", mBreakoutGame.getPaddle());

        mBreakoutGame.setBricks(prefs_bricks);
        mBreakoutGame.setHits(prefs_hits);
        mBreakoutGame.setBalls(prefs_ball_count);
        mBreakoutGame.setPaddle(prefs_paddle);
    }
}
