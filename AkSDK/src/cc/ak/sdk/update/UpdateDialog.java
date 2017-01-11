
package cc.ak.sdk.update;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.update.AsyncHttp.HttpResultAsyncCallback;
import cc.ak.sdk.util.ScreenUtil;

import java.io.File;

public class UpdateDialog extends Dialog {

    private CheckUpdateBean mUpdateBean;
    private TextView mContentTextView;
    private TextView mTitleTextView;
    private Button mUpdateConfimButton;
    private Button mUpdateCancelButton;
    private Button mCancleConfimButton;
    private ProgressBar mDownloadProgress;
    private LinearLayout mLinearLayout;
    private RelativeLayout mParentRelativelayout;
    private boolean mForceUpdate = false;
    private Activity mContext;
    private CxAPPDownloadModel mDownloadModel;
    private CxCheckUpdateListener mCheckUpdateListener;

    public UpdateDialog(Activity context, CheckUpdateBean bean, CxCheckUpdateListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mUpdateBean = bean;
        this.mContext = context;
        this.mCheckUpdateListener = listener;
        initDialog();
    }

    private void initDialog() {

        mParentRelativelayout = new RelativeLayout(mContext);
        mParentRelativelayout.setBackgroundColor(Color.parseColor("#E6E6E6"));
        android.widget.RelativeLayout.LayoutParams parentLayoutparam = new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
        mParentRelativelayout.setLayoutParams(parentLayoutparam);

        mTitleTextView = new TextView(mContext);
        android.widget.RelativeLayout.LayoutParams titleParam = new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.toDip(mContext, 50));
        titleParam.addRule(RelativeLayout.CENTER_VERTICAL);
        titleParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mTitleTextView.setLayoutParams(titleParam);
        mTitleTextView.setTextColor(Color.WHITE);
        mTitleTextView.setTextSize(ScreenUtil.toSp(mContext, 12));
        mTitleTextView.setBackgroundColor(Color.parseColor("#00BAFF"));
        mTitleTextView.setPadding(ScreenUtil.toDip(mContext, 10), 0, ScreenUtil.toDip(mContext, 10), 0);
        mTitleTextView.setGravity(Gravity.CENTER_VERTICAL);
        mTitleTextView.setId(0x20000);
        mParentRelativelayout.addView(mTitleTextView);

        mDownloadProgress = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
        android.widget.RelativeLayout.LayoutParams progressParam = new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressParam.leftMargin = ScreenUtil.toDip(mContext, 20);
        progressParam.rightMargin = ScreenUtil.toDip(mContext, 20);
        progressParam.topMargin = ScreenUtil.toDip(mContext, 20);
        progressParam.bottomMargin = ScreenUtil.toDip(mContext, 20);
        progressParam.addRule(RelativeLayout.BELOW, 0x20000);
        mDownloadProgress.setLayoutParams(progressParam);
        mDownloadProgress.setVisibility(View.GONE);
        mDownloadProgress.setId(0x20001);
        mParentRelativelayout.addView(mDownloadProgress);

        mContentTextView = new TextView(mContext);
        android.widget.RelativeLayout.LayoutParams contentTextParam = new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
        contentTextParam.leftMargin = ScreenUtil.toDip(mContext, 20);
        contentTextParam.rightMargin = ScreenUtil.toDip(mContext, 20);
        contentTextParam.bottomMargin = ScreenUtil.toDip(mContext, 20);
        contentTextParam.addRule(RelativeLayout.BELOW, 0x20001);
        mContentTextView.setLayoutParams(contentTextParam);
        mContentTextView.setTextColor(Color.BLACK);
        mContentTextView.setMaxLines(10);
        mContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mContentTextView.setId(0x20002);
        mParentRelativelayout.addView(mContentTextView);

        mLinearLayout = new LinearLayout(mContext);//"update_linearlayout"
        android.widget.RelativeLayout.LayoutParams updateLinearLayoutparam = new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
        updateLinearLayoutparam.leftMargin = ScreenUtil.toDip(mContext, 5);
        updateLinearLayoutparam.rightMargin = ScreenUtil.toDip(mContext, 20);
        updateLinearLayoutparam.addRule(RelativeLayout.BELOW, 0x20002);
        updateLinearLayoutparam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM | RelativeLayout.CENTER_HORIZONTAL);
        mLinearLayout.setLayoutParams(updateLinearLayoutparam);
        mLinearLayout.setId(0x20003);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        FrameLayout frameLayout = new FrameLayout(mContext);
        LayoutParams frameparam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
        frameparam.leftMargin = ScreenUtil.toDip(mContext, 20);
        frameparam.rightMargin = ScreenUtil.toDip(mContext, 20);
        frameLayout.setLayoutParams(frameparam);

        mUpdateConfimButton = new Button(mContext);
        android.widget.FrameLayout.LayoutParams updateConfimbtnparam = new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mUpdateConfimButton.setLayoutParams(updateConfimbtnparam);
        mUpdateConfimButton.setTextColor(Color.parseColor("#00BAFF"));
        mUpdateConfimButton.setTextSize(ScreenUtil.toSp(mContext, 10));
        mUpdateConfimButton.setText("确认");
        frameLayout.addView(mUpdateConfimButton);

        mCancleConfimButton = new Button(mContext);
        android.widget.FrameLayout.LayoutParams cancleConfimbtnparam = new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mCancleConfimButton.setLayoutParams(cancleConfimbtnparam);
        mCancleConfimButton.setTextColor(Color.parseColor("#00BAFF"));
        mCancleConfimButton.setTextSize(ScreenUtil.toSp(mContext, 10));
        mCancleConfimButton.setText("取消");
        mCancleConfimButton.setVisibility(View.GONE);
        frameLayout.addView(mCancleConfimButton);
        mLinearLayout.addView(frameLayout);

        mUpdateCancelButton = new Button(mContext);
        LayoutParams updateCancelparam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
        updateCancelparam.leftMargin = ScreenUtil.toDip(mContext, 10);
        mUpdateCancelButton.setLayoutParams(updateCancelparam);
        mUpdateCancelButton.setTextColor(Color.parseColor("#00BAFF"));
        mUpdateCancelButton.setTextSize(ScreenUtil.toSp(mContext, 10));
        mUpdateCancelButton.setText("取消");
        mLinearLayout.addView(mUpdateCancelButton);

        mParentRelativelayout.addView(mLinearLayout);
        this.setContentView(mParentRelativelayout);

        mDownloadProgress.setMax(100);
        mDownloadModel = new CxAPPDownloadModel(handler, mContext);
        mTitleTextView.setText("版本更新");
        mContentTextView.setText(mUpdateBean.getUpdateTip());
        if (mUpdateBean != null && mUpdateBean.getUpdateType() == 1) {
            mForceUpdate = true;
            mUpdateCancelButton.setVisibility(View.GONE);
        }
        setLinstener();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String value = (String) msg.obj;
            mContentTextView.setText(value + " %");
            mDownloadProgress.setProgress(Integer.valueOf(value));
        }
    };

    private HttpResultAsyncCallback<Integer> viewUpdaAsyncCallback = new HttpResultAsyncCallback<Integer>() {

        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(Integer result) {
            switch (result) {
                case CxAPPDownloadModel.DOWNLOAD_SUCCESS:
                    // 下载成功
                    mContentTextView.setText("100%");
                    if (checkFile(AkSDKConfig.UPDATE_APKFILE_NAME)) {
                        dismiss();
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                AkSDKConfig.UPDATE_APK_PATH,
                                AkSDKConfig.UPDATE_APKFILE_NAME)),
                                "application/vnd.android.package-archive");
                        mContext.startActivity(intent);
                        mContext.finish();
                    } else {
                        mLinearLayout.setVisibility(View.VISIBLE);
                        mCancleConfimButton.setVisibility(View.VISIBLE);
                        mUpdateConfimButton.setVisibility(View.GONE);
                        mUpdateCancelButton.setVisibility(View.GONE);
                        mContentTextView.setText("安装包异常，请重新尝试");
                    }
                    break;

                case CxAPPDownloadModel.DOWNLOAD_FIALED:
                    // 下载失败
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mCancleConfimButton.setVisibility(View.VISIBLE);
                    mUpdateConfimButton.setVisibility(View.GONE);
                    mUpdateCancelButton.setVisibility(View.GONE);
                    mContentTextView.setText("下载失败，请重新尝试!!");
                    if (mCheckUpdateListener != null) {
                        mCheckUpdateListener.updateVersionFailed("下载失败");
                    }
                    break;

                case CxAPPDownloadModel.NET_CONNECT_FALSE:

                    // 网络未连接
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mCancleConfimButton.setVisibility(View.VISIBLE);
                    mUpdateConfimButton.setVisibility(View.GONE);
                    mUpdateCancelButton.setVisibility(View.GONE);
                    mContentTextView.setText("您的网络未连接，请连接网络后尝试!!");
                    if (mCheckUpdateListener != null) {
                        mCheckUpdateListener.updateVersionFailed("您的网络未连接");
                    }
                    break;

                case CxAPPDownloadModel.NET_SHUT_DOWN:
                    if (mCheckUpdateListener != null) {
                        mCheckUpdateListener.updateVersionFailed("网络异常断开");
                    }
                    // 网络异常断开
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onException(Exception ie) {
            mLinearLayout.setVisibility(View.VISIBLE);
            mCancleConfimButton.setVisibility(View.VISIBLE);
            mUpdateConfimButton.setVisibility(View.GONE);
            mUpdateCancelButton.setVisibility(View.GONE);
            mContentTextView.setText("下载出现异常，请重新尝试!!");
            if (mCheckUpdateListener != null) {
                mCheckUpdateListener.updateVersionFailed("下载出现异常");
            }
        }

        @Override
        public void onCancelled() {

        }
    };

    private void setLinstener() {
        mUpdateConfimButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mTitleTextView.setText("正在下载");
                mDownloadProgress.setVisibility(View.VISIBLE);
                mContentTextView.setVisibility(View.VISIBLE);
                mContentTextView.setText("0%");
                mUpdateCancelButton.setVisibility(View.GONE);
                mUpdateConfimButton.setVisibility(View.GONE);
                mCancleConfimButton.setVisibility(View.VISIBLE);
                deleteApkFile(AkSDKConfig.UPDATE_APKFILE_NAME);
                mDownloadModel.startDownload(viewUpdaAsyncCallback, mUpdateBean.getDownloadUrl(),
                        AkSDKConfig.UPDATE_APKFILE_NAME, 0l);
            }
        });

        mUpdateCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mCheckUpdateListener != null) {
                    mCheckUpdateListener.cancelUpdateVersion();
                }
            }
        });
        // 取消下载
        mCancleConfimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dismiss();
                if (mDownloadModel != null) {
                    mDownloadModel.cancleDownload();
                }
                if (mCheckUpdateListener != null) {
                    mCheckUpdateListener.cancelUpdateVersion();
                }

                if (mContext != null) {
                    mContext.finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }

            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mForceUpdate;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean checkFile(String fileName) {
        File file = new File(AkSDKConfig.UPDATE_APK_PATH, fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private void deleteApkFile(String fileName) {
        File file = new File(AkSDKConfig.UPDATE_APK_PATH, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
