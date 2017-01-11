
package cc.ak.sdk.listener.adapter;

import android.content.Intent;
import android.os.Bundle;

import cc.ak.sdk.listener.IActivityListener;

/**
 * Activity监听适配器
 */
public class ActivityAdapter implements IActivityListener {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onNewIntent(Intent newIntent) {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onRestart() {
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

}
