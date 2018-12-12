package com.android.qmaker.survey.core;

import android.app.Activity;

import com.qmaker.survey.core.utils.PayLoad;

public class UIHandler {
    public interface Displayer {
        int STATE_STARTED = 0, STATE_SUCCESS = 1, STATE_FAILED = 2;

        boolean onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad);
    }
}
