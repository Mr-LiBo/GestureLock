package com.cins.gesturelock.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.cins.gesturelock.R;

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

    private Paint defaultPaint, selectPaint, errorPaint;
    private Path trianglePath;
    private Matrix triangleMatrix;

    private Cell[][] mCells = new Cell[3][3];
    private List<Cell> sCells = new ArrayList<Cell>();

    //set offset to the boundary
    private int offset = 10;


    public GestureLockView(Context context) {
        super(context);
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
    }

    /**
     * draw the view to canvas
     * @param canvas
     */
    private void drawToCanvas(Canvas canvas) {

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
                mCells[i][j].setX(distance * j + cellRadius + offset);
                mCells[i][j].setY(distance * i + cellRadius + offset);
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
