package pnwgamer.info.markdietzlerbreakout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, Observer {

    private BreakoutGame mBreakoutGame;
    private TextView mScoreBoard;
    private Button mLeftPaddle;
    private Button mRightPaddle;

    private static final int PADDLE_STOP = 0;
    private static final int PADDLE_LEFT = 1;
    private static final int PADDLE_RIGHT = 2;

    //this is fine
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBreakoutGame = (BreakoutGame)findViewById(R.id.game_view);
        mScoreBoard = (TextView)findViewById(R.id.score_board);
        mLeftPaddle = (Button)findViewById(R.id.left_paddle);
        mRightPaddle = (Button)findViewById(R.id.right_paddle);
        mBreakoutGame.mBreakoutGameObserver.addObserver(this);

        mBreakoutGame.setOnClickListener(this);
        mLeftPaddle.setOnTouchListener(this);
        mRightPaddle.setOnTouchListener(this);

        if(savedInstanceState != null) {
            getGameStateFromBundle(savedInstanceState);
        }
    }

    //this is fine
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // this adds items to the action bar if it is present
        final int landscape = getResources().getConfiguration().ORIENTATION_LANDSCAPE;
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

    @Override
    public void update(Observable observableInput, Object obsTimeTransfer) {
        String scoreBoard = new String();
        if(observableInput != null) {
            int level, balls, score, bricks;
            level = mBreakoutGame.mBreakoutGameObserver.getLevel();
            balls = mBreakoutGame.mBreakoutGameObserver.getBalls();
            score = mBreakoutGame.mBreakoutGameObserver.getScore();
            bricks = mBreakoutGame.mBreakoutGameObserver.getBricks();
            scoreBoard = String.format("Level: %02d Balls: %02d Score:%06d Bricks:%03d",level,balls,score,bricks );
            mScoreBoard.setText(scoreBoard);
        } else {

        }
    }

    @Override
    public boolean onTouch(View viewBeingTouched, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            if(viewBeingTouched == findViewById(R.id.left_paddle)){
                mBreakoutGame.mPlayerPaddle.setMovementState(PADDLE_LEFT);
            }

            if(viewBeingTouched == findViewById(R.id.right_paddle)) {
                mBreakoutGame.mPlayerPaddle.setMovementState(PADDLE_RIGHT);
            }
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {

            if(viewBeingTouched == findViewById(R.id.left_paddle)){
                mBreakoutGame.mPlayerPaddle.setMovementState(PADDLE_STOP);
            }

            if(viewBeingTouched == findViewById(R.id.right_paddle)) {
                mBreakoutGame.mPlayerPaddle.setMovementState(PADDLE_STOP);
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefs_bricks;
        String prefs_hits;
        String prefs_ball_count;

        prefs_bricks = prefs.getString("init_brick_count", Integer.toString(mBreakoutGame.getPreferences_Bricks()));
        prefs_hits = prefs.getString("init_brick_hits", Integer.toString(mBreakoutGame.getPreferences_Hits()));
        prefs_ball_count = prefs.getString("init_balls_level", Integer.toString(mBreakoutGame.getPreferences_Balls()));

        mBreakoutGame.setPreferences_Bricks(Integer.parseInt(prefs_bricks));
        mBreakoutGame.setPreferences_Hits(Integer.parseInt(prefs_hits));
        mBreakoutGame.setPreferences_Balls(Integer.parseInt(prefs_ball_count));
        mBreakoutGame.invalidate();
    }

    @Override
    public void onClick(View viewClicked) {
        if(viewClicked == findViewById(R.id.game_view)) {
            mBreakoutGame.PausedToggle();
            mBreakoutGame.setNewGame(false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        putGameStateInBundle(state);
    }

    private void putGameStateInBundle(Bundle state) {
        state.putInt("score", mBreakoutGame.mBreakoutGameObserver.getScore());
        state.putInt("balls", mBreakoutGame.mBreakoutGameObserver.getBalls());
        state.putInt("bricks", mBreakoutGame.mBreakoutGameObserver.getBricks());
        state.putInt("level", mBreakoutGame.mBreakoutGameObserver.getLevel());
        mBreakoutGame.setPaused(true);

        //brick state
        //brick boolean array
    }

    private void getGameStateFromBundle(Bundle state) {
        mBreakoutGame.set_Game_Balls(state.getInt("balls"));
        mBreakoutGame.set_Game_Score(state.getInt("score"));
        mBreakoutGame.set_Game_Bricks(state.getInt("bricks"));
        mBreakoutGame.set_Game_Level(state.getInt("level"));

        //brick state
        //brick boolean array
    }
}
