
package cc.ak.sdk.http;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

public class RestClient {

    public final static int SUCCESSFUL = 10;
    public final static int FAILED = 9;
    protected final static int CONNECTION_TIMEOUT = 150000;
    public static final String CHARSET = HTTP.UTF_8;
    private static String TAG = "RestClient";
    private static Object lock = new Object();

    private static HttpClient client;

    public static HttpClient getHttpClient() {
        if (client != null)
            return client;
        synchronized (lock) {
            if (client == null) {
                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, true);

                ConnManagerParams.setTimeout(params, 1000);
                HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, 4000);

                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

                ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
                client = new DefaultHttpClient(conMgr, params);
            }
        }
        return client;
    }

    public static String get(String url) throws Exception {
        String result = null;
        HttpClient httpClient = getHttpClient();
        HttpGet getMethod = new HttpGet(url);
        HttpResponse response = httpClient.execute(getMethod);
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity, RestClient.CHARSET);
        }
        return result;
    }

    public static HttpResponse post(String url, String in) throws Exception {
        HttpResponse result = null;
        HttpPost request = new HttpPost(url);
        if (!TextUtils.isEmpty(in)) {
            StringEntity entity = new StringEntity(in);
            request.setEntity(entity);
            Log.d(TAG, "entity===" + entity.toString());
        }
        Log.d(TAG, "==== Call Service ====");
        Log.d(TAG, "=EndPoint is:" + url);
        Log.d(TAG, "=Headers=");

        HttpClient client = getHttpClient();
        HttpResponse response = client.execute(request);
        result = response;
        return result;
    }

    public static HttpResponse post(String url, List<NameValuePair> params) throws Exception {
        HttpResponse result = null;
        HttpPost request = new HttpPost(url);
        if (params != null) {
            HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            request.setEntity(entity);
        }
        HttpClient client = getHttpClient();
        HttpResponse response = client.execute(request);
        result = response;
        return result;
    }

}
