
package cc.ak.sdk.update;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import cc.ak.sdk.AkSDKConfig;
import cc.ak.sdk.util.VerifyUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class CxAPPDownloadModel extends AsyncHttp {

    private static final String KEY_TASK = "appDownloadModel";

    public static final int DOWNLOAD_SUCCESS = 1;
    public static final int DOWNLOAD_FIALED = -1;
    public static final int NET_CONNECT_FALSE = 0;
    public static final int NET_SHUT_DOWN = 10;

    private Handler handler;
    private Context context;

    public CxAPPDownloadModel(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    public void startDownload(
            final HttpResultAsyncCallback<Integer> viewUpdateCallback,
            final String url, final String fileName, final long size) {
        doAsyncTask(KEY_TASK, viewUpdateCallback,
                new DoAsyncTaskCallback<Void, Integer>() {
                    @Override
                    public Integer doAsyncTask(Void... params) throws Exception {
                        return doDownloadTheFile(url, fileName, size);
                    }
                });
    }

    public void cancleDownload() {
        super.cancel(KEY_TASK);
    }

    public int doDownloadTheFile(String url, String filename, long size)
            throws Exception {

        if (!VerifyUtil.isNetworkAvailable(context)) {
            return NET_CONNECT_FALSE;
        }
        HttpResponse response = null;
        HttpResponse response_test = null;
        InputStream is = null;

        HttpClient client = new DefaultHttpClient();
        HttpClient client_test = new DefaultHttpClient();

        HttpParams parms = client.getParams();
        HttpConnectionParams.setConnectionTimeout(parms, 60 * 1000);
        HttpConnectionParams.setSoTimeout(parms, 60 * 1000);

        HttpParams parms_test = client_test.getParams();
        HttpConnectionParams.setConnectionTimeout(parms_test, 60 * 1000);
        HttpConnectionParams.setSoTimeout(parms, 60 * 1000);

        HttpGet request = new HttpGet(url);
        HttpGet request_test = new HttpGet(url);
        response_test = client_test.execute(request_test);
        long fileSize = response_test.getEntity().getContentLength();
        if (fileSize != 0 && fileSize == size) {
            return DOWNLOAD_SUCCESS;
        }
        Header header_size = new BasicHeader("Range", "bytes=" + size + "-"
                + fileSize);
        request.addHeader(header_size);
        response = client.execute(request);
        is = response.getEntity().getContent();
        if (is == null) {
            throw new RuntimeException("stream is null");
        }

        File myTempFile = createFile(filename);
        RandomAccessFile fos = new RandomAccessFile(myTempFile, "rw");
        fos.seek(size);
        byte buf[] = new byte[1024];
        long downloadfilesize = 0;
        int numFlag = 0;
        Object obj = new Object();
        do {
            int numread = is.read(buf);
            if (numread > 0) {
                numFlag = 0;
                fos.write(buf, 0, numread);
                if (handler != null) {
                    Message msg = new Message();
                    downloadfilesize += numread;
                    double percent = (double) (downloadfilesize + size)
                            / fileSize;
                    msg.obj = String.valueOf((int) (percent * 100));
                    handler.sendMessage(msg);
                }
            } else {
                numFlag++;
                if (numFlag > 20) {
                    return DOWNLOAD_FIALED;
                }
            }
        } while (downloadfilesize < fileSize);
        is.close();
        if (downloadfilesize == fileSize) {
            return DOWNLOAD_SUCCESS;
        }
        return DOWNLOAD_FIALED;

    }

    private File createFile(String fileName) throws IOException {
        File file = new File(AkSDKConfig.UPDATE_APK_PATH, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
