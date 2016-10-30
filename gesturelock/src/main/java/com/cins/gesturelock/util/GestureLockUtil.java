package com.cins.gesturelock.util;

import android.content.Context;

import com.cins.gesturelock.widget.GestureLockView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eric on 2016/10/30.
 */

public class GestureLockUtil {

    /**
     * change string value (ex: 10.0dip => 20) to int value
     *
     * @param context
     * @param value
     * @return
     */
    @Deprecated
    public static int changeSize(Context context, String value) {
        if (value.contains("dip")) {
            float dip = Float.valueOf(value.substring(0, value.indexOf("dip")));
            return GestureLockUtil.dip2px(context, dip);
        } else if (value.contains("px")) {
            float px = Float.valueOf(value.substring(0, value.indexOf("px")));
            return (int) px;
        } else if (value.contains("@")) {
            float px = context.getResources().getDimension(Integer.valueOf(value.replace("@", "")));
            return (int) px;
        } else {
            throw new IllegalArgumentException("can not use wrap_content " +
                    "or match_parent or fill_parent or others' illegal parameter");
        }
    }

    /**
     * dip to px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * check the touch cell is or not in the circle
     *
     * @param sx
     * @param sy
     * @param r      the radius of circle
     * @param x      the x position of circle's center point
     * @param y      the y position of circle's center point
     * @param offset the offset to the frame of the circle
     *               (if offset > 0 : the offset is inside the circle; if offset < 0 : the offset is outside the circle)
     * @return
     */
    public static boolean checkInRound(float sx, float sy, float r, float x, float y, float offset) {
        return Math.sqrt((sx - x + offset) * (sx - x + offset) + (sy - y + offset) * (sy - y + offset)) < r;
    }


    /**
     * get distance between two points
     *
     * @param fpX
     * @param fpY
     * @param spX
     * @param spY
     * @return
     */
    public static float getDistanceBetweenTwoPoints(float fpX, float fpY, float spX, float spY) {
        return (float) Math.sqrt((spX - fpX) * (spX - fpX) + (spY - fpY) * (spY - fpY));
    }

    /**
     * get the angle which the line intersect x axis
     * @param fpX
     * @param fpY
     * @param spX
     * @param spY
     * @param distance
     * @return
     */
    public static float getAngleLineIntersectX(float fpX, float fpY, float spX, float spY, float distance) {
        return (float) Math.toDegrees(Math.acos((spX - fpX) / distance));
    }
    /**
     * get the angle which the line intersect y axis
     *
     * @param fpX
     * @param fpY
     * @param spX
     * @param spY
     * @param distance
     * @return
     */
    public static float getAngleLineIntersectY(float fpX, float fpY, float spX, float spY, float distance) {
        return (float) Math.toDegrees(Math.acos((spY - fpY) / distance));
    }

    /**
     * Generate an SHA-1 hash for the gesture. Not the most secure, but it is at
     * least a second level of protection. First level is that the file is in a
     * location only readable by the system process.
     * @param cells
     * @return
     */
    public static byte[] gestureToHash(List<GestureLockView.Cell> cells) {
        if (cells == null) {
            return null;
        } else {
            int size = cells.size();
            byte[] res = new byte[size];
            for (int i = 0; i < size; i++) {
                GestureLockView.Cell cell = cells.get(i);
                res[i] = (byte) cell.getIndex();
            }
            MessageDigest messageDigest = null;

            try {
                messageDigest = MessageDigest.getInstance("SHA-1");
                return messageDigest.digest(res);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return res;
            }
        }
    }

    /**
     * Check to see if a gesture matches the saved gesture. If no gesture
     * exists, always returns true.
     * @param cellList
     * @param bytes
     * @return
     */
    public static boolean checkGesture(List<GestureLockView.Cell> cellList, byte[] bytes) {
        if (cellList == null || bytes == null) {
            return false;
        } else {
            byte[] bytes1 = gestureToHash(cellList);
            return Arrays.equals(bytes,bytes1);
        }
    }
}
