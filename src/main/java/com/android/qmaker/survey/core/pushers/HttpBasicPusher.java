package com.android.qmaker.survey.core.pushers;

import com.android.qmaker.survey.core.process.AsyncHttpPushProcess;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.entities.Repository;
import com.qmaker.survey.core.interfaces.PushProcess;
import com.qmaker.survey.core.interfaces.Pusher;

import istat.android.network.http.AsyncHttp;
import istat.android.network.http.BodyPartHttpQuery;

/**
 * @author Toukea Tatsi J
 */
public class HttpBasicPusher implements Pusher {
    @Override
    public PushProcess push(PushOrder order, Callback callback) throws Exception {
        return null;
    }

    @Override
    public String getSupportedGrandType() {
        return Repository.GRAND_TYPE_HTTP_BASIC;
    }


    class Process extends AsyncHttpPushProcess {

        @Override
        protected AsyncHttp onCreateAsyncHttp(PushOrder order) {
            BodyPartHttpQuery http = new BodyPartHttpQuery();
            http.setBody(order);
            return AsyncHttp.from(http);
        }
    }
}
