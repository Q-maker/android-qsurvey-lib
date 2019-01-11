package com.android.qmaker.survey.core.utils.displayers;

import android.app.Activity;

import com.android.qmaker.survey.core.engines.UIHandler;
import com.qmaker.survey.core.entities.Survey;
import com.qmaker.survey.core.utils.PayLoad;

/**
 * @author Toukea Tatsi J
 */
public class NotificationUIDisplayer implements UIHandler.Displayer {
    @Override
    public boolean onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad) {
        if (currentActivity == null || currentActivity.isFinishing()) {
            return false;
        }
        Survey.Result result = payLoad.getVariable(0);
        if (result == null && result.getOrigin() == null || result.getOrigin().isBlockingPublisherNeeded()) {
            return false;
        }
        return false;
    }
}
