
package cc.ak.sdk.listener;

import android.content.Intent;
import android.os.Bundle;

/**
 *	Activity监听器
 *	监控Activity的生命周期 
 */
public interface IActivityListener {

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onCreate(Bundle savedInstanceState);

    public void onStart();

    public void onPause();

    public void onResume();

    public void onNewIntent(Intent newIntent);

    public void onStop();

    public void onDestroy();

    public void onRestart();

    public boolean onBackPressed();

}
