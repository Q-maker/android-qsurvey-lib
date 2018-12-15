package com.android.qmaker.survey.core.utils.displayers;

import android.app.Activity;

import com.android.qmaker.survey.core.engines.UIHandler;
import com.qmaker.survey.core.utils.PayLoad;

public class NotificationUIDisplayer implements UIHandler.Displayer{
    @Override
    public boolean onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad) {
        if (currentActivity == null || currentActivity.isFinishing()) {
            return false;
        }
        return false;
    }
}
