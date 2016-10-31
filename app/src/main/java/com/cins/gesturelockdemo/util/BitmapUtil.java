package com.cins.gesturelockdemo.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Eric on 2016/10/31.
 */

public class BitmapUtil {
    public static Bitmap resizeBitmap(int boxWidth, int boxHeight, Bitmap bitmap) {

        float scaleX = ((float) boxWidth) / ((float) bitmap.getWidth());
        float scaleY = ((float) boxHeight) / ((float) bitmap.getHeight());
        float scale = 1.0f;

        if ((scaleX >= scaleY && scaleY >= 1.0f) || (scaleX > scaleY && scaleX < 1.0f) || (scaleX >= 1.0f && scaleY < 1.0f)) {
            scale = scaleX;
        }
        if ((scaleY > scaleX && scaleX >= 1.0f) || (scaleY > scaleX && scaleY < 1.0f) || (scaleX < 1.0f && scaleY >= 1.0f)) {
            scale = scaleY;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Bitmap alterBitmap = Bitmap.createBitmap(newBitmap, 0, 0, boxWidth, boxHeight);
        newBitmap = null;
        return alterBitmap;
    }
}
