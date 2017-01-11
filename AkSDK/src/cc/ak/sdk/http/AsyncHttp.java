
package cc.ak.sdk.http;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.os.AsyncTask;

public class AsyncHttp {

    private ConcurrentHashMap<String, AsyncTask> asyncTaskMap = new ConcurrentHashMap<String, AsyncTask>();

    protected <Param, Progress, Result> void doAsyncTask(final String keyTask,
            final HttpResultAsyncCallback<Result> updateViewAsyncCallback,
            final DoAsyncTaskCallback<Param, Result> doAsyncTaskCallback,
            Param... param) {
        if (null == updateViewAsyncCallback) {
            return;
        }

        AsyncTask<Param, Void, Result> asyncTask = new AsyncTask<Param, Void, Result>() {
            private Exception ie = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                updateViewAsyncCallback.onPreExecute();
            }

            @Override
            protected Result doInBackground(Param... params) {
                Result result = null;
                try {
                    result = doAsyncTaskCallback.doAsyncTask(params);
                } catch (Exception ie) {
                    this.ie = ie;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Result result) {
                super.onPostExecute(result);
                if (null == ie) {
                    updateViewAsyncCallback.onPostExecute(result);
                } else {
                    updateViewAsyncCallback.onException(ie);
                    ie = null;
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                updateViewAsyncCallback.onCancelled();
            }

        };
        cancel(keyTask);
        asyncTaskMap.put(String.valueOf(keyTask), asyncTask);
        asyncTask.execute(param);
    }

    protected void cancel(String keyTask) {
        if (asyncTaskMap.containsKey(keyTask)) {
            asyncTaskMap.get(keyTask).cancel(true);
            asyncTaskMap.remove(keyTask);
        }
    }

    public void cancelAllTasks() {
        Set<Entry<String, AsyncTask>> entrySet = asyncTaskMap.entrySet();
        Iterator<Entry<String, AsyncTask>> it = entrySet.iterator();
        while (it.hasNext())
        {
            Entry<String, AsyncTask> entry = it.next();
            AsyncTask task = entry.getValue();
            if (task != null)
            {
                task.cancel(true);
            }
            it.remove();
        }
    }

    public interface HttpResultAsyncCallback<Result> {
        public abstract void onPreExecute();

        public abstract void onPostExecute(Result result);

        public abstract void onCancelled();

        public abstract void onException(Exception ie);
    }

    public interface DoAsyncTaskCallback<Param, Result> {
        public abstract Result doAsyncTask(Param... params) throws Exception;
    }

    public abstract static class CommonHttpResultAsyncCallback<Result>
            implements HttpResultAsyncCallback<Result> {

        @Override
        public void onPreExecute() {
        }
    }
}
