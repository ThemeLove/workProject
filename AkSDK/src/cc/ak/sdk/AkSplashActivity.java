
package cc.ak.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import cc.ak.sdk.util.AkSDKUtil;
import cc.ak.sdk.util.AKLogUtil;

public class AkSplashActivity extends Activity {

    private RelativeLayout mainLayout;
    private ImageView splashIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutParams mainLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
                );
        mainLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams splashIvLayoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
                );

        splashIv = new ImageView(this);
        splashIv.setImageResource(
                AkSDKUtil.getDrawableId(this, AkSDKConfig.SPLASH_DRAWABLE_NAME)
                );
        splashIv.setScaleType(ScaleType.CENTER_CROP);
        mainLayout.addView(splashIv, splashIvLayoutParams);
        setContentView(mainLayout, mainLayoutParams);
        AkSDKConfig.getInstance().loadConfig(this);
        appendAnimation();
    }

    private void appendAnimation() {
        AlphaAnimation ani = new AlphaAnimation(0.0f, 1.0f);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setRepeatCount(0);
        ani.setDuration(2000); //2s
        splashIv.setAnimation(ani);
        ani.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AkSplashActivity.this.startGameActivity();
            }
        });
    }

    private void startGameActivity() {
        try {

            AKLogUtil.d("startGameActivity : " + AkSDKConfig.MAIN_ACTIVITY_NAME);
            Class<?> mainClass = Class.forName(AkSDKConfig.MAIN_ACTIVITY_NAME);
            Intent intent = new Intent(this, mainClass);
            startActivity(intent);
            finish();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
