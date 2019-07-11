package com.android.qmaker.survey.core.engines;

import android.app.Activity;

import com.qmaker.survey.core.engines.PushExecutor;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.entities.Survey;
import com.qmaker.survey.core.utils.PayLoad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Toukea Tatsi J
 */
public final class UIHandler implements PushExecutor.ExecutionStateChangeListener {
    Survey.Result result;
    Activity activity;
    List<PushOrder> pushOrders;
    final List<PushOrder> completedOrders = Collections.synchronizedList(new ArrayList());
    AndroidQSurvey androidQSurvey;

    UIHandler(Survey.Result result, List<PushOrder> pushOrders, Activity activity) {
        this.result = result;
        this.activity = activity;
        this.pushOrders = Collections.synchronizedList(new ArrayList(pushOrders));
    }

    void startHandling(AndroidQSurvey androidQSurvey) {
        this.androidQSurvey = androidQSurvey;
        this.androidQSurvey.dispatchDisplayerSurveyResultPublishStateChanged(Displayer.STATE_STARTED, result, pushOrders);
        androidQSurvey
                .getPushExecutor()
                .registerExecutionStateChangeListener(this);
    }

    public boolean cancel() {
        boolean out = this.androidQSurvey
                .getPushExecutor()
                .unregisterExecutionStateChangeListener(this);
        out &= androidQSurvey.detachUIHandler(result.getSource());
        this.androidQSurvey.dispatchDisplayerSurveyResultPublishStateChanged(Displayer.STATE_CANCELED, result, pushOrders, completedOrders);
        this.androidQSurvey = null;
        return out;
    }

    @Override
    public void onTaskStateChanged(PushExecutor.Task task) {//89072240
        synchronized ((pushOrders)) {
            if (pushOrders.contains(task.getOrder())) {
                this.androidQSurvey.dispatchDisplayerSurveyResultPublishStateChanged(Displayer.STATE_PROGRESS, result, task.getOrder(), pushOrders, completedOrders);
                int taskState = task.getState();
                int bitAnd = PushOrder.FLAG_STATE_FINISHED & taskState;
                if (bitAnd == PushOrder.FLAG_STATE_FINISHED) {
                    pushOrders.remove(task.getOrder());
                    completedOrders.add(task.getOrder());
                }
                if (pushOrders.isEmpty()) {
                    this.androidQSurvey.dispatchDisplayerSurveyResultPublishStateChanged(Displayer.STATE_FINISH, result, completedOrders);
                    if (this.androidQSurvey.currentShowingActivity != null) {
                        //TODO verifier que il sagit bien d'un testRunner et procéder a l'évaluation.
                    }
                }
            }
        }
    }

    public interface Displayer {
        int STATE_STARTED = 0, STATE_PROGRESS = 3, STATE_FINISH = 4, STATE_CANCELED = 5;

        boolean onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad);
    }
}
