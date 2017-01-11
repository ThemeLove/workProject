package cc.ak.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.ak.sdk.util.BackgroundUtil;
import cc.ak.sdk.util.ImageBase64Util;
import cc.ak.sdk.util.ScreenUtil;

/**
 * Loading对话框
 */
public class AkLoadingDialog extends Dialog {

	private Activity mActivity;
	private static float[] radii = { 15.0F, 15.0F, 15.0F, 15.0F, 15.0F, 15.0F, 15.0F, 15.0F };
	private static final String IMG_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA3QAAAN0BcFOiBwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAXCSURBVHic3ZtdiFVVFMd/c2ac8SOdCiYdR5SyIKzMMgPJIIMoP3KyNCsdLOsli4IeigIrekh7iLCHIDIlJS1DU6kpqLTILMiEkoSSTPMjKwsbZ8oZHf89rHP1zmnunbP3nHPmXv+wuPfcuz/WWmftvddee+0KSaSMamAScDFQH6HhwCngV+Bw+Jmjn4DPgY40matISQHnAVOBGcAtwBDPdlqAD4ANQHP4nCiSVEAVMB+Yh73xqqQaDtEBbAFWA28CnYm0KikJmiXpB2WH7yXNSIL33lrAjcASYEL43A58BHwHHArpYPgZACOAhpBGAFcAk4F+nv1vBZ4AtnnW97aAOknN4ds4LmmTpCZJtR5tnS9pgaQPJXV4WsT6kCdnWXyEHydpn6RWSYs8hS6mjEWS2jyUsFfS2LQVMCcUfKWkhgQFj9JISWs9lNAq6fY0FBBIel7SdknXpih4lCbLJjwXnJL0tKSKOH3EnQTfAAYDc4F/vSccPwwC3gGmONZbhS3LRQUMYjS0CPgLmEX2wgO0YQ7Vcsd6TRjvxdGDicyW9HCGJt8TPesxHIrOCcU6myhpYQkIHaUnHZXQqiKrQ6FORklaWgLCFqJ3HZWwVwX8hEIdPCdpUAkIWohqJe12VML67trqbhUYBxzDtqOljLHAV8AAhzrXEXGbo6tAAPSn9IUH22887ljnhegPUQu4AfgCOOHNVraoBnYDIx3qNAKbcg/5FjAI25WVi/BgMYLFjnUWA5W5h3wFTAA2J8BU1lgO/OJQfgzm0QJdFbCLpKIs2cLHCu7JfcnNAQMx0y8n889HDfAnNozjoAOoA1pyFpBTQLmiHfjEoXw1FrQ9PQSSDmD2Bd53LH8b2BCoxWLzxxJnKVuMAPY7lG8B6gLM9MtdeIADmHMUF0OA6wNs/OTQhAUf9oS0Frg7KQ4zwBbH8qOrsIjJUCzqc3OkwIXAbGzZuA840lsOU8YBx/L1uUlwFf8XPh/TgZVeLGWLw47l6wPszd4Uo/AU4F5XjjKGlwIaHSrMdOwga7gqYFgAXO1QYbxjB1nDywJqHSo0EN/d7AscAY46lG8LsINLFwxzLJ81vnEou+NsVICLS9x8NirgZeDrGOWagTU+Cqh3ZilbdAJzKK6EzcACsF2gqwIa/PjKFD8DE4FHgWnY6tUB7ADWAcswD7iyQlIjloQUF9uw8HI5oYLuD0kHVEgajC0f1TEb6wQuwA5Myxm1wMkA2wq77KIqiec6lzr6EfoBABsdK09NmJm+QDucCYo24LaV/A1bDVJPM00JQUgncxZwEDcPaiiWMFGuqAFOQtdzgbccG3mGeBkmpYZKLD8R6CrAK7j5BJcBdybEVJaYDOzMPeQr4B/srbqg3KygBpv8Tuc6RZlfgR2RxcWlWCC1XDAeS689jagCOrHcWxcsBUb3gqmscAnm83RZuboz3/eAzxwarsVC6TXerKWPc7AT4Z3RPwqN3/nAHw4dXIVZQqliCfB6t/8USUSaJKndMRFpXgIJUEnTI7KUv27/LzaDbwUedNT0CsJ9dongIcxr/bJQgZ6WsOXASw4dVmGm5rqcJo0AeBHbtb5dtGQME6qUtMFxKEjSMklVfWDyAyStk7RGMTLG4zgxncAduFkCwP1Y0sLljvV6gyuxFWwfdnmr582ao3YXyH1iPCFLuz03xbdeJ+lVWV5wk0tdn84mSfrdUQkK6zwgqSZBwftLekzSUUn7JV3j2oZvx6MkfeqhBIXMrpR0q6cy+kuaKWm1pJawzY8lDfWRpbfX5qZj6adjPOv/jXmeu7CYxIG8zwC7WjscC9gMxxyuaZhnB/At8BQW4/eDpwXkU6XMtA96WoQP9kiaK7vL1Cv+k7w6OxBYCNxFOqfIArZjmSyvkdCl6rQuTzdg93wasQBE3JB7FMexU5yN2FBxPcTpEWkpIB+DsSu2F3FmTOeTOHNl/lDe9x8xP6ItTeb+A2jNMu0Rqw8OAAAAAElFTkSuQmCC"; 
	private TextView mTvMsg;
	private ImageView mIvLoading;
	private RotateAnimation mLoadingAnimation;

	public AkLoadingDialog(Activity activity, String msg) {
		super(activity);
		this.mActivity = activity;

		int dip_5 = ScreenUtil.toSp(mActivity, 5);
		int dip_10 = ScreenUtil.toSp(mActivity, 10);
		int dip_15 = ScreenUtil.toDip(mActivity, 15);
		int dip_64 = ScreenUtil.toDip(mActivity, 64);
		int dip_150 = ScreenUtil.toDip(mActivity, 150);
		setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题
        setCanceledOnTouchOutside(false); // 禁止点击外部取消
        // 设置背景
		ShapeDrawable localLayerDrawable = BackgroundUtil.createRoundRect(
			mActivity, radii, Color.parseColor("#88000000")
		);
		getWindow().setBackgroundDrawable(localLayerDrawable);
        // 主布局
		LinearLayout mainLayout = new LinearLayout(mActivity);
		mainLayout.setGravity(Gravity.CENTER);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(dip_150, dip_150);
		//Loading
		mIvLoading = new ImageView(mActivity);
		mIvLoading.setImageBitmap(ImageBase64Util.decode(IMG_BASE64));
		LinearLayout.LayoutParams loadingLayoutParams = new LinearLayout.LayoutParams(dip_64, dip_64);
		mainLayout.addView(mIvLoading, loadingLayoutParams);
		
        // 消息
		mTvMsg = new TextView(mActivity);
		mTvMsg.setSingleLine(true);
		mTvMsg.setGravity(Gravity.CENTER);
		mTvMsg.setText(msg);
		mTvMsg.setTextColor(Color.WHITE);
		mTvMsg.setTextSize(dip_5);
		LinearLayout.LayoutParams msgLayoutParams = new LinearLayout.LayoutParams(
    		LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
		);
		msgLayoutParams.topMargin = dip_15;
		msgLayoutParams.leftMargin = dip_10;
		msgLayoutParams.rightMargin = dip_10;
		mainLayout.addView(mTvMsg, msgLayoutParams);
		setContentView(mainLayout, mainLayoutParams);
		initAnimation();
		
	}
	
	private void initAnimation() {
		mLoadingAnimation = new RotateAnimation(0.0F, 360F, 1, 0.5F, 1, 0.5F);
		mLoadingAnimation.setInterpolator(new AccelerateInterpolator());
		mLoadingAnimation.setDuration(2000L);
		mLoadingAnimation.setRepeatMode(Animation.RESTART);
		mLoadingAnimation.setRepeatCount(Animation.INFINITE);
		mLoadingAnimation.setInterpolator(new LinearInterpolator());
	}

	@Override
	public void show() {
		if(mIvLoading != null) {
			mIvLoading.startAnimation(mLoadingAnimation);
        }
		super.show();
	}

	@Override
	public void hide() {
		if(mIvLoading != null) {
			mIvLoading.clearAnimation();
        }
		super.hide();
	}
	@Override
	public void dismiss() {
		if(mIvLoading != null) {
			mIvLoading.clearAnimation();
        }
		super.dismiss();
	}

	public void setMsg(String msg) {
		if(mTvMsg != null) {
			mTvMsg.setText(msg);
		}
	}
}
