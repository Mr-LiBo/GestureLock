package com.cins.gesturelockdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cins.gesturelock.util.GestureLockUtil;
import com.cins.gesturelock.widget.GestureLockIndicator;
import com.cins.gesturelock.widget.GestureLockView;
import com.cins.gesturelockdemo.R;
import com.cins.gesturelockdemo.util.cache.ACache;
import com.cins.gesturelockdemo.util.constant.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * create gesture activity
 * Created by Eric on 2016/10/31.
 */

public class CreateGestureActivity extends Activity {

    @Bind(R.id.GestureLockIndicator)
    GestureLockIndicator mGestureLockIndicator;
    @Bind(R.id.GestureLockView)
    GestureLockView mGestureLockView;
    @Bind(R.id.resetBtn)
    Button resetBtn;
    @Bind(R.id.messageTv)
    TextView messageTv;


    private List<GestureLockView.Cell> mChosenGesture = null;
    private ACache aCache;
    private static final long DELAYTIME = 600L;
    private static final String TAG = "CreateGestureActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gesture);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        aCache = ACache.get(CreateGestureActivity.this);
        mGestureLockView.setOnGestureListener( gestureListener);
    }

    /**
     * 手势监听
     */
    private GestureLockView.OnGestureListener gestureListener = new GestureLockView.OnGestureListener() {
        @Override
        public void onGestureStart() {
            mGestureLockView.removePostClearPatternRunnable();
            mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
        }

        @Override
        public void onGestureComplete(List<GestureLockView.Cell> cells) {
            if (mChosenGesture == null && cells.size() >= 4) {
                mChosenGesture = new ArrayList<GestureLockView.Cell>(cells);
                updateStatus(Status.CORRECT, cells);
            } else if (mChosenGesture == null && cells.size() < 4) {
                updateStatus(Status.LESSERROR, cells);
            } else if (mChosenGesture != null) {
                if (mChosenGesture.equals(cells)) {
                    updateStatus(Status.CONFIRMCORRECT, cells);
                } else {
                    updateStatus(Status.CONFIRMERROR, cells);
                }
            }
        }
    };

    /**
     * 更新状态
     * @param status
     * @param cells
     */
    private void updateStatus(Status status, List<GestureLockView.Cell> cells) {
        messageTv.setTextColor(getResources().getColor(status.colorId));
        messageTv.setText(status.strId);
        switch (status) {
            case DEFAULT:
                mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
                break;
            case CORRECT:
                updateLockGestureIndicator();
                mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
                break;
            case LESSERROR:
                mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
                break;
            case CONFIRMERROR:
                mGestureLockView.setGesture(GestureLockView.DisplayMode.ERROR);
                mGestureLockView.postClearPatternRunnable(DELAYTIME);
                break;
            case CONFIRMCORRECT:
                saveChosenGesture(cells);
                mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
                setLockGestureSuccess();
                break;
        }
    }

    /**
     * 更新 Indicator
     */
    private void updateLockGestureIndicator() {
        if (mChosenGesture== null)
            return;
        mGestureLockIndicator.setIndicator(mChosenGesture);
    }

    /**
     * 重新设置手势
     */
    @OnClick(R.id.resetBtn)
    void resetLockGesture() {
        mChosenGesture = null;
        mGestureLockIndicator.setDefaultIndicator();
        updateStatus(Status.DEFAULT, null);
        mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
    }
    /**
     * 成功设置了手势密码(跳到首页)
     */
    private void setLockGestureSuccess() {
        Toast.makeText(this, "create gesture success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    /**
     * 保存手势密码
     */
    private void saveChosenGesture(List<GestureLockView.Cell> cells) {
        byte[] bytes = GestureLockUtil.gestureToHash(cells);
        aCache.put(Constant.GESTURE_PASSWORD, bytes);
    }

    private enum Status {
        //默认的状态，刚开始的时候（初始化状态）
        DEFAULT(R.string.create_gesture_default, R.color.grey_a5a5a5),
        //第一次记录成功
        CORRECT(R.string.create_gesture_correct, R.color.grey_a5a5a5),
        //连接的点数小于4（二次确认的时候就不再提示连接的点数小于4，而是提示确认错误）
        LESSERROR(R.string.create_gesture_less_error, R.color.red_f4333c),
        //二次确认错误
        CONFIRMERROR(R.string.create_gesture_confirm_error, R.color.red_f4333c),
        //二次确认正确
        CONFIRMCORRECT(R.string.create_gesture_confirm_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }
        private int strId;
        private int colorId;
    }

}
