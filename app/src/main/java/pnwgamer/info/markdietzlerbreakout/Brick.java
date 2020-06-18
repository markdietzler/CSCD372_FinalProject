package pnwgamer.info.markdietzlerbreakout;

import android.graphics.RectF;

public class Brick {

    private int mHit_Count;
    private int mMax_Hits;
    private RectF gameBrick;
    private boolean mIsBrickVisible; //true for visible, false for not visible
    private int padding = 1;
    private int topMargin = 20;

    public Brick(final int row, final int column, final float brickWidth, final float brickHeight, final float boardWidth, final float boardHeight){

        //boardHeight - topMargin - (row * brickHeight + padding)  -- old top

        //boardHeight - topMargin - (row * brickHeight + brickHeight - padding) --old bottom

        gameBrick = new RectF(column * brickWidth + padding,
                boardHeight - topMargin - (row * brickHeight + brickHeight - padding),
                column * brickWidth + brickWidth - padding,
                boardHeight - topMargin - (row * brickHeight + padding));
    }

    public int Get_Hits() {
        return mHit_Count;
    }

    public void SetMaxHits(int hitCountToSet) {
        mMax_Hits = hitCountToSet;
    }

    public void Increment_Hits() {
        if(mHit_Count == mMax_Hits) {

        } else {
            mHit_Count++;
        }
    }

    public RectF getGameBrick(){
        return this.gameBrick;
    }

    public void setInvisible(){
        mIsBrickVisible = false;
    }

    public void SetVisible() {
        mIsBrickVisible = true;
    }

    public boolean getVisibility(){
        return mIsBrickVisible;
    }

}
