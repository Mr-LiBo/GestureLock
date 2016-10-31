package com.cins.gesturelock.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import com.cins.gesturelock.R;
import com.cins.gesturelock.util.GestureLockUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Eric on 2016/10/30.
 */

public class GestureLockView extends View{

    private float movingX, movingY;
    private boolean isActionMove = false;
    private boolean isActionDown = false;//default action down is false
    private boolean isActionUp = true;//default action up is true

    private int width, height;
    private int cellRadius, cellInnerRadius;
    private int cellBoxWidth, cellBoxHeight;
    private boolean mInStealthMode = false;
    //haptic feed back (default is false)
    private boolean mEnableHapticFeedback = false;
    //set delay time
    private long delayTime = 600L;

    private Paint defaultPaint, selectPaint, errorPaint;
    private Path trianglePath;
    private Matrix triangleMatrix;

    private Cell[][] mCells = new Cell[3][3];
    private List<Cell> sCells = new ArrayList<Cell>();
    private OnGestureListener gestureListener;

    //set offset to the boundary
    private int offset = 10;
    private static final double CONSTANT_COS_30 = Math.cos(Math.toRadians(30));

    public GestureLockView(Context context) {
        super(context,null);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    /**
     * initialize
     */
    private void init() {
        this.initCellSize();
        this.init9Cells();
        this.initPaints();
        this.initPaths();
        this.initMatrixs();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawToCanvas(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getMeasuredHeight();
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //Log.e(TAG, "(width: " + width + "  ,  height" + height + ")");
        if (width != height) {
            throw new IllegalArgumentException("the width must be equals height");
        }
        initCellSize();
        set9CellsSize();
        invalidate();
    }

    /**
     * draw the view to canvas
     * @param canvas
     */
    private void drawToCanvas(Canvas canvas){

        for(int i = 0; i< mCells.length; i++){
            for(int j = 0; j < mCells[i].length; j++){
                if(mCells[i][j].getStatus() == Cell.STATE_CHECK){
                    selectPaint.setStyle(Style.STROKE);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.cellRadius, this.selectPaint);
                    selectPaint.setStyle(Style.FILL);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.cellInnerRadius, this.selectPaint);
                } else if(mCells[i][j].getStatus() == Cell.STATE_NORMAL){
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.cellRadius, this.defaultPaint);
                } else if(mCells[i][j].getStatus() == Cell.STATE_CHECK_ERROR){
                    errorPaint.setStyle(Style.STROKE);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.cellRadius, this.errorPaint);
                    errorPaint.setStyle(Style.FILL);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.cellInnerRadius, this.errorPaint);
                }
            }
        }

        if(sCells.size() > 0){
            //temporary cell: at the beginning the cell is the first of sCells
            Cell tempCell = sCells.get(0);

            for(int i = 1; i < sCells.size(); i++ ){
                Cell cell = sCells.get(i);
                if(cell.getStatus() == Cell.STATE_CHECK) {
                    //drawLineIncludeCircle(tempCell, cell, canvas , selectPaint);
                    drawLine(tempCell, cell, canvas, selectPaint);
                    //drawTriangle(tempCell, cell, canvas, selectPaint);
                    drawNewTriangle(tempCell, cell, canvas, selectPaint);
                } else if (cell.getStatus() == Cell.STATE_CHECK_ERROR){
                    //drawLineIncludeCircle(tempCell, cell, canvas, errorPaint);
                    drawLine(tempCell, cell, canvas, errorPaint);
                    //drawTriangle(tempCell, cell, canvas, errorPaint);
                    drawNewTriangle(tempCell, cell, canvas, errorPaint);
                }
                tempCell = cell;
            }

            if(isActionMove  && !isActionUp){
                //canvas.drawLine(tempCell.getX(), tempCell.getY(), movingX, movingY, selectPaint);
                this.drawLineFollowFinger(tempCell, canvas, selectPaint);
            }
        }
    }
    /**
     * initialize cell size (include circle radius, inner circle radius,
     * cell box width, cell box height)
     */
    private void initCellSize() {
        this.cellRadius = (this.width - offset * 2)/4/2;
        this.cellInnerRadius = this.cellRadius/3;
        this.cellBoxWidth = (this.width - offset * 2)/3;
        this.cellBoxHeight = (this.height - offset * 2)/3;
    }

    /**
     * initialize nine cells
     */
    private void init9Cells() {
        //the distance between the center of two circles
        int distance = this.cellBoxWidth + this.cellBoxWidth/2 - this.cellRadius;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mCells[i][j] = new Cell(distance * j + cellRadius + offset,
                        distance * i + cellRadius + offset, i, j, 3 * i + j + 1);
            }
        }
    }

    /**
     * set nine cells size
     */
    private void set9CellsSize() {
        int distance = this.cellBoxWidth + this.cellBoxWidth/2 - this.cellRadius;
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mCells[i][j].setX(distance*j + cellRadius + offset);
                mCells[i][j].setY(distance*i + cellRadius + offset);
            }
        }
    }

    /**
     * initialize paints
     */
    private void initPaints(){
        defaultPaint = new Paint();
        defaultPaint.setColor(getResources().getColor(R.color.blue_78d2f6));
        defaultPaint.setStrokeWidth(2.0f);
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setAntiAlias(true);

        selectPaint = new Paint();
        selectPaint.setColor(getResources().getColor(R.color.blue_00aaee));
        selectPaint.setStrokeWidth(3.0f);
        //selectPaint.setStyle(Style.STROKE);
        selectPaint.setAntiAlias(true);

        errorPaint = new Paint();
        errorPaint.setColor(getResources().getColor(R.color.red_f3323b));
        errorPaint.setStrokeWidth(3.0f);
        //errorPaint.setStyle(Style.STROKE);
        errorPaint.setAntiAlias(true);
    }

    /**
     * initialize paths
     */
    private void initPaths() {
        trianglePath = new Path();
    }

    /**
     * initialize matrixs
     */
    private void initMatrixs() {
        triangleMatrix = new Matrix();
    }

    private void drawLine(Cell preCell, Cell nextCell, Canvas canvas, Paint paint) {
        Cell centerCell = getCellBetweenTwoCells(preCell, nextCell);
        if(centerCell != null && sCells.contains(centerCell)) {
            drawLineNotIncludeCircle(centerCell, preCell, canvas, paint);
            drawLineNotIncludeCircle(centerCell, nextCell, canvas, paint);
        } else {
            drawLineNotIncludeCircle(preCell, nextCell, canvas, paint);
        }
    }



    /**
     * get the cell between two cells (it has the limitation: the gesture must be 3x3)
     *
     * @param preCell
     * @param nextCell
     * @return
     */
    private Cell getCellBetweenTwoCells(Cell preCell, Cell nextCell) {
        //two cells are in the same row
        if(preCell.getRow() == nextCell.getRow()) {
            if(Math.abs(nextCell.getColumn() - preCell.getColumn()) > 1) {
                return mCells[preCell.getRow()][1];
            } else {
                return null;
            }
        }
        //two cells are in the same column
        else if (preCell.getColumn() == nextCell.getColumn()) {
            if(Math.abs(nextCell.getRow() - preCell.getRow()) > 1) {
                return mCells[1][preCell.getColumn()];
            } else {
                return null;
            }
        }
        //opposite angles
        else if (Math.abs(nextCell.getColumn() - preCell.getColumn()) > 1
                && Math.abs(nextCell.getRow() - preCell.getRow()) > 1) {
            return mCells[1][1];
        } else {
            return null;
        }
    }

    /**
     * draw line not include circle (the line do not show inside the circle)
     * @param preCell
     * @param nextCell
     * @param canvas
     * @param paint
     */
    private void drawLineNotIncludeCircle(Cell preCell, Cell nextCell, Canvas canvas, Paint paint) {
        float distance = GestureLockUtil.getDistanceBetweenTwoPoints(
                preCell.getX(), preCell.getY(), nextCell.getX(), nextCell.getY());
        float x1 = this.cellRadius / distance * (nextCell.getX() - preCell.getX()) + preCell.getX();
        float y1 = this.cellRadius / distance * (nextCell.getY() - preCell.getY()) + preCell.getY() ;
        float x2 = (distance - this.cellRadius) / distance *
                (nextCell.getX() - preCell.getX()) + preCell.getX();
        float y2 = (distance - this.cellRadius) / distance *
                (nextCell.getY() - preCell.getY()) + preCell.getY();
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    /**
     * draw line follow finger
     * (do not draw line inside the selected cell,
     * but it is only the starting cell not the other's cell)
     * @param preCell
     * @param canvas
     * @param paint
     */
    private void drawLineFollowFinger(Cell preCell, Canvas canvas, Paint paint) {
        float distance = GestureLockUtil.getDistanceBetweenTwoPoints(
                preCell.getX(),preCell.getY(),movingX,movingY);
        if(distance > this.cellRadius) {
            float x1 = this.cellRadius / distance * (movingX - preCell.getX()) + preCell.getX();
            float y1 = this.cellRadius / distance * (movingY - preCell.getY()) + preCell.getY() ;
            canvas.drawLine(x1, y1, movingX, movingY, paint);
        }
    }

    /**
     * draw new triangle
     * @param preCell
     * @param nextCell
     * @param canvas
     * @param paint
     */
    private void drawNewTriangle(Cell preCell, Cell nextCell, Canvas canvas, Paint paint) {
        float distance = GestureLockUtil.getDistanceBetweenTwoPoints
                (preCell.getX(), preCell.getY(), nextCell.getX(), nextCell.getY());
        float x = preCell.getX();
        float y = preCell.getY() - this.cellInnerRadius * 2;

        float x1 = x - this.cellInnerRadius / 2;
        float y1 = y + (float)(this.cellInnerRadius * CONSTANT_COS_30);
        float x2 = x + this.cellInnerRadius / 2 ;
        float y2 = y1;

        float angleX = GestureLockUtil.getAngleLineIntersectX(
                preCell.getX(), preCell.getY(), nextCell.getX(), nextCell.getY(), distance);
        float angleY = GestureLockUtil.getAngleLineIntersectY(
                preCell.getX(), preCell.getY(), nextCell.getX(), nextCell.getY(), distance);

        trianglePath.reset();
        trianglePath.moveTo(x, y);
        trianglePath.lineTo(x1, y1);
        trianglePath.lineTo(x2, y2);
        trianglePath.close();
        //slide right down and right up
        if (angleX >= 0 && angleX <= 90 ) {
            triangleMatrix.setRotate(180 - angleY, preCell.getX(), preCell.getY());
        }
        //slide left up and left down
        else {
            triangleMatrix.setRotate(angleY - 180, preCell.getX(), preCell.getY());
        }
        trianglePath.transform(triangleMatrix);
        canvas.drawPath(trianglePath, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float ex = event.getX();
        float ey = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(ex, ey);
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(ex, ey);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp();
                break;
        }
        return true;
    }

    /**
     * handle action down
     * @param ex
     * @param ey
     */
    private void handleActionDown(float ex, float ey) {
        isActionMove = false;
        isActionDown = true;
        isActionUp = false;

        setGesture(DisplayMode.DEFAULT);

        if(this.gestureListener != null) {
            this.gestureListener.onGestureStart();
        }

        Cell cell = checkSelectCell(ex, ey);
        if(cell != null) {
            addSelectedCell(cell);
        }
    }

    /**
     * handle action move
     * @param ex
     * @param ey
     */
    private void handleActionMove(float ex, float ey) {
        isActionMove = true;
        movingX = ex;
        movingY = ey;
        Cell cell = checkSelectCell(ex, ey);
        if(cell != null ){
            addSelectedCell(cell);
        }
        this.setGesture(DisplayMode.NORMAL);
    }

    /**
     * handle action up
     */
    private void handleActionUp() {
        isActionMove = false;
        isActionUp = true;
        isActionDown = false;

        this.setGesture(DisplayMode.NORMAL);

        if(this.gestureListener != null) {
            this.gestureListener.onGestureComplete(sCells);
        }
    }

    /**
     * check user's touch moving is or not in the area of cells
     * @param x
     * @param y
     * @return
     */
    private Cell checkSelectCell(float x, float y) {
        for (int i = 0; i < mCells.length; i++) {
            for (int j = 0; j < mCells[i].length; j++) {
                Cell cell = mCells[i][j];
                if (GestureLockUtil.checkInRound(cell.x, cell.y, 80, x, y, this.cellRadius/4)) {
                    return cell;
                }
            }
        }
        return null;
    }

    /**
     * add selected cell
     * @param cell
     */
    private void addSelectedCell(Cell cell){
        if(!sCells.contains(cell)){
            cell.setStatus(Cell.STATE_CHECK);
            handleHapticFeedback();
            sCells.add(cell);
        }
        setGesture(DisplayMode.NORMAL);
    }

    /**
     * handle haptic feedback
     * (if mEnableHapticFeedback true: has haptic else not have haptic)
     */
    private void handleHapticFeedback() {
        if(mEnableHapticFeedback) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING |
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        }
    }

    /**
     * the display mode of the gesture
     */
    public enum DisplayMode {
        //show default pattern (the default pattern is initialize status)
        DEFAULT,
        //show selected pattern normal
        NORMAL,
        //show selected pattern error
        ERROR;
    }
    /**
     * set gesture
     * @param mode (details see the DisplayMode)
     */
    public void setGesture(DisplayMode mode) {
        switch (mode) {
            case DEFAULT:
                for(Cell cell: sCells) {
                    cell.setStatus(Cell.STATE_NORMAL);
                }
                sCells.clear();
                break;
            case NORMAL:
                break;
            case ERROR:
                for(Cell cell : sCells) {
                    cell.setStatus(Cell.STATE_CHECK_ERROR);
                }
                break;
        }
        this.handleStealthMode();
    }
    /**
     * handle the stealth mode (if true: do not post invalidate; false: post invalidate)
     */
    private void handleStealthMode() {
        if(!mInStealthMode) {
            this.postInvalidate();
        }
    }

    /**
     * remove the post delay clear pattern
     */
    public void removePostClearPatternRunnable(){
        this.removeCallbacks(mClearPatternRunnable);
    }

    /**
     * delay clear gesture
     * @param delay the delay time (if delay less than 0, it will be 600L)
     */
    public void postClearPatternRunnable(long delay) {
        if(delay >= 0L){
            delayTime = delay;
        }
        this.removeCallbacks(mClearPatternRunnable);
        this.postDelayed(mClearPatternRunnable, delayTime);
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            GestureLockView.this.setGesture(DisplayMode.DEFAULT);
        }
    };

    /**
     * @return Whether the view is in stealth mode.
     */
    public boolean isInStealthMode() {
        return mInStealthMode;
    }

    /**
     * Set whether the view is in stealth mode.  If true, there will be no
     * visible feedback as the user enters the pattern.
     * @param inStealthMode Whether in stealth mode.
     */
    public void setInStealthMode(boolean inStealthMode) {
        mInStealthMode = inStealthMode;
    }

    /**
     * @return Whether the view has tactile feedback enabled.
     */
    public boolean isTactileFeedbackEnabled() {
        return mEnableHapticFeedback;
    }

    /**
     * Set whether the view will use tactile feedback.  If true, there will be
     * tactile feedback as the user enters the pattern.
     * @param tactileFeedbackEnabled Whether tactile feedback is enabled
     */
    public void setTactileFeedbackEnabled(boolean tactileFeedbackEnabled) {
        mEnableHapticFeedback = tactileFeedbackEnabled;
    }

    public void setOnGestureListener(OnGestureListener gestureListener){
        this.gestureListener = gestureListener;
    }

    public static interface OnGestureListener {
        public void onGestureStart();
        public void onGestureComplete(List<Cell> cells);
    }

    public class Cell{

        private int x;// the x position of circle's center point
        private int y;// the y position of circle's center point
        private int row;// the cell in which row
        private int column;// the cell in which column
        private int index;// the cell value
        private int status = 0;//default status

        //default status
        public static final int STATE_NORMAL = 0;
        //checked status
        public static final int STATE_CHECK = 1;
        //checked error status
        public static final int STATE_CHECK_ERROR = 2;

        public Cell(){}



        public Cell(int x, int y, int row, int column, int index){
            this.x = x;
            this.y = y;
            this.row = row;
            this.column = column;
            this.index = index;
        }

        public int getX(){
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY(){
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getRow() {
            return this.row;
        }

        public int getColumn() {
            return this.column;
        }

        public int getIndex(){
            return this.index;
        }

        public int getStatus(){
            return this.status;
        }

        public void setStatus(int status){
            this.status = status;
        }
    }
}
