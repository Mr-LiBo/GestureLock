package com.cins.gesturelock.widget;

import java.util.List;

/**
 * Created by Eric on 2016/10/30.
 */

public class GestureLockView {


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
