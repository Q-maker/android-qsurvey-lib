package com.android.qmaker.survey.core.pushers;

import com.android.qmaker.survey.core.process.AsyncHttpPushProcess;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.entities.Repository;
import com.qmaker.survey.core.interfaces.PushProcess;
import com.qmaker.survey.core.interfaces.Pusher;

import istat.android.network.http.AsyncHttp;
/**
 * @author Toukea Tatsi J
 */
public class WssePusher implements Pusher {
    @Override
    public PushProcess push(PushOrder order, Callback callback) throws Exception {
        return null;
    }

    @Override
    public String getSupportedAccessType() {
        return Repository.ACCESS_TYPE_HTTP_WSSE;
    }

    class Process extends AsyncHttpPushProcess {

        @Override
        protected AsyncHttp onCreateAsyncHttp(PushOrder order) {
            return null;
        }
    }
}
