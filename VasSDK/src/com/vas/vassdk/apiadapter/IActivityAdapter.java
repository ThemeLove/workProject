package com.vas.vassdk.apiadapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract interface IActivityAdapter
{
  public abstract void onApplicationInit(Context paramContext);
  
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
