
package cc.ak.sdk.update;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.view.WindowManager;

import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.http.RequestParams;
import cc.ak.sdk.http.RestClient;
import cc.ak.sdk.util.CheckUpdateUtil;
import cc.ak.sdk.util.AKHttpUtil;
import cc.ak.sdk.util.AKLogUtil;
import cc.ak.sdk.util.ScreenUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 检测更新
 * 
 */
public class CxCheckAppUpdateModel extends AsyncHttp {

    private static final String Key_Task = "CheckAppUpdateModel";

    public void checkAppUpdate(final Activity activity, final CxCheckUpdateListener listener) {
        requestCheckUpdate(new HttpResultAsyncCallback<String>() {

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.getInt("code");
                        if (code == 1) {
                            JSONObject updateValue = obj.getJSONObject("data");
                            CheckUpdateBean checkUpdate = new CheckUpdateBean();
                            checkUpdate.setGameVersion(updateValue.getString("game_version"));
                            checkUpdate.setDownloadUrl(updateValue.getString("update_url"));
                            checkUpdate.setUpdateTip(updateValue.getString("update_content"));
                            checkUpdate.setUpdateType(updateValue.getInt("update_mode"));

                            String oldVersion = CheckUpdateUtil.getPackageVersion(activity);
                            AKLogUtil.d("oldVersion = " + oldVersion);
                            AKLogUtil.d("game_version = " + checkUpdate.getGameVersion());
                            // 判断线上配置版本是否高于目前版本号
                            if (CheckUpdateUtil.checkVersion(oldVersion,
                                    checkUpdate.getGameVersion())) {
                                UpdateDialog dialog = new UpdateDialog(activity, checkUpdate,
                                        listener);
                                dialog.setCancelable(false); // 触摸其他地方dialog不消失
                                dialog.show();
                                WindowManager.LayoutParams params =
                                        dialog.getWindow().getAttributes();
                                params.width = ScreenUtil.toDip(activity, 400);
                                params.height = ScreenUtil.toDip(activity, 320);
                                dialog.getWindow().setAttributes(params);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    AKLogUtil.d("检测更新：" + result);
                }
            }

            @Override
            public void onException(Exception ie) {
                ie.printStackTrace();
            }

            @Override
            public void onCancelled() {
                AKLogUtil.d("检测更新：onCancelled");
            }
        }, createCheckUpdateUrl());
    }

    private void requestCheckUpdate(
            final HttpResultAsyncCallback<String> viewUpdateCallback, final String url) {
        doAsyncTask(Key_Task, viewUpdateCallback,
                new DoAsyncTaskCallback<Void, String>() {
                    @Override
                    public String doAsyncTask(Void... params) throws Exception {
                        AKLogUtil.d("检测更新：" + url);
                        return RestClient.get(url);
                    }
                });
    }

    public String createCheckUpdateUrl() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(AkSDKConfig.AK_URL);
        buffer.append("?");
        RequestParams params = AKHttpUtil.creatCheckUpdateParams("game_update");
        params.put("gameId", AkSDKConfig.AK_GAMEID);
        buffer.append(params.toString());
        return buffer.toString();
    }
}
