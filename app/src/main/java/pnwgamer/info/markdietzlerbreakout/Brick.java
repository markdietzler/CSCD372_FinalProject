package pnwgamer.info.markdietzlerbreakout;

import android.graphics.RectF;

public class Brick {

    private int hit_count;
    private int max_hits;
    private RectF rect;
    private boolean isVisible;

    public Brick(int row, int column, int width, int height){

        isVisible = true;
        int padding = 1;
        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    public int Get_Hits() {
        return hit_count;
    }

    public void SetHits(int hitCount) {
        max_hits = hitCount;
    }

    public void Increment_Hits() {
        if(hit_count == max_hits) {

        } else {
            hit_count++;
        }
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }

}
