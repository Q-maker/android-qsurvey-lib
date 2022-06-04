package com.android.qmaker.survey.core.process;


import com.istat.freedev.processor.Process;
import com.istat.freedev.processor.ProcessManager;
import com.istat.freedev.processor.Processor;
import com.istat.freedev.processor.interfaces.ProcessCallback;
import com.qmaker.survey.core.engines.Response;
import com.qmaker.survey.core.engines.PushResult;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.entities.Repository;
import com.qmaker.survey.core.interfaces.PushProcess;
import com.qmaker.survey.core.interfaces.Pusher;

import java.io.InputStream;
import java.net.HttpURLConnection;

import istat.android.network.http.AsyncHttp;
import istat.android.network.http.HttpAsyncQuery;
import istat.android.network.http.HttpQueryError;
import istat.android.network.http.HttpQueryResponse;
import istat.android.network.http.HttpQueryResult;
import istat.android.network.http.interfaces.DownloadHandler;
/**
 * @author Toukea Tatsi J
 */
public abstract class AsyncHttpPushProcess extends AsycHttpProcess<PushResult, PushProcess.Error> implements PushProcess {
    PushOrder order;
    ProcessManager processManager;
    HttpAsyncQuery asyncQuery;

    public AsyncHttpPushProcess() {
        this(Processor.getDefaultProcessManager());
    }

    public AsyncHttpPushProcess(ProcessManager manager) {
        this.processManager = manager;
    }

    @Override
    protected void onCancel() {
        if (asyncQuery != null) {
            asyncQuery.cancel();
        }
    }

    @Override
    protected void onExecute(ExecutionVariables executionVariables) throws Exception {
        PushOrder order = executionVariables.getVariable(0, PushOrder.class);
        AsyncHttp asyncHttp = onCreateAsyncHttp(order);
        Repository repository = order.getRepository();
        asyncHttp.useDownloader(mSuccessDownloader, DownloadHandler.WHEN.SUCCESS);
        asyncHttp.useDownloader(mErrorDownloader, DownloadHandler.WHEN.ERROR);
        this.asyncQuery = asyncHttp.doPost(repository.getUri(), mHttpCallback);
    }

    protected abstract AsyncHttp onCreateAsyncHttp(PushOrder order);

    @Override
    public boolean proceed(PushOrder order, Pusher.Callback callback) {
        this.order = order;
        this.addCallback(createProcessCallback(callback));
        try {
            processManager.execute(order.getId(), this, order);
        } catch (ProcessManager.ProcessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Response getResponse() {
        if (getState() == Process.STATE_SUCCESS) {
            return getResult();
        }
        return getError();
    }

    static ProcessCallback<PushResult, Error> createProcessCallback(final Pusher.Callback callback) {
        return new ProcessCallback<PushResult, Error>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinished(int finishState) {
                if (callback != null) {
                    callback.onFinish(finishState);
                }
            }

            @Override
            public void onSuccess(PushResult result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(Error pushError) {
                if (callback != null) {
                    callback.onError(pushError);
                }
            }

            @Override
            public void onFail(Throwable e) {
                if (callback != null) {
                    callback.onFailed(e);
                }
            }

            @Override
            public void onAborted() {
                if (callback != null) {
                    callback.onFailed(new AbortionException());
                }
            }
        };
    }

    HttpAsyncQuery.Callback mHttpCallback = new HttpAsyncQuery.Callback() {

        @Override
        public void onHttpSuccess(HttpQueryResult resp) {
            notifySucceed(resp.getBodyAs(PushResult.class));
        }

        @Override
        public void onHttpError(HttpQueryError e) {
            notifyError(e.getBodyAs(Error.class));
        }

        @Override
        public void onHttpFailure(Exception e) {
            notifyFailed(e);
        }

        @Override
        public void onHttComplete(HttpQueryResponse resp) {

        }

        @Override
        public void onHttpAborted() {
            notifyAborted();
        }
    };

    DownloadHandler<PushResult> mSuccessDownloader = new DownloadHandler<PushResult>() {
        @Override
        public PushResult onBuildResponseBody(HttpURLConnection connexion, InputStream stream) throws Exception {
            return null;
        }
    };

    DownloadHandler<Error> mErrorDownloader = new DownloadHandler<Error>() {
        @Override
        public Error onBuildResponseBody(HttpURLConnection connexion, InputStream stream) throws Exception {
            return null;
        }
    };

}
