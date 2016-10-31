package com.cins.gesturelockdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cins.gesturelock.util.GestureLockUtil;
import com.cins.gesturelock.widget.GestureLockView;
import com.cins.gesturelockdemo.R;
import com.cins.gesturelockdemo.util.cache.ACache;
import com.cins.gesturelockdemo.util.constant.Constant;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eric on 2016/10/31.
 */

public class GestureLoginActivity extends BaseActivity {

    @Bind(R.id.GestureLockView)
    GestureLockView mGestureLockView;
    @Bind(R.id.messageTv)
    TextView messageTv;
    @Bind(R.id.forgetGestureBtn)
    Button forgetGestureBtn;

    private ACache aCache;
    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_login);
        ButterKnife.bind(this);
        this.init();
    }


    private void init() {
        aCache = ACache.get(GestureLoginActivity.this);
        //得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
        mGestureLockView.setOnGestureListener(patternListener);
        updateStatus(Status.DEFAULT);
    }

    private GestureLockView.OnGestureListener patternListener = new GestureLockView.OnGestureListener() {

        @Override
        public void onGestureStart() {
            mGestureLockView.removePostClearPatternRunnable();
        }

        @Override
        public void onGestureComplete(List<GestureLockView.Cell> cells) {
            if(cells != null){
                if(GestureLockUtil.checkGesture(cells, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };


    /**
     * 更新状态
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                mGestureLockView.setGesture(GestureLockView.DisplayMode.ERROR);
                mGestureLockView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                mGestureLockView.setGesture(GestureLockView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
        Toast.makeText(GestureLoginActivity.this, "success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    /**
     * 忘记手势密码（去账号登录界面）
     */
    @OnClick(R.id.forgetGestureBtn)
    void forgetGesturePasswrod() {
        Intent intent = new Intent(GestureLoginActivity.this, CreateGestureActivity.class);
        startActivity(intent);
        this.finish();
    }

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }
        private int strId;
        private int colorId;
    }

    @Override
    protected boolean shouldCheckGesturePass() {
        return false;
    }
}
