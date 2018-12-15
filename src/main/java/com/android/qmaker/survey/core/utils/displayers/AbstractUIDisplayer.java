package com.android.qmaker.survey.core.utils.displayers;

import android.app.Activity;

import com.android.qmaker.survey.core.engines.UIHandler;
import com.qmaker.survey.core.utils.PayLoad;
/**
 * @author Toukea Tatsi J
 */
public class AbstractUIDisplayer implements UIHandler.Displayer {
    @Override
    public boolean onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad) {
        return false;
    }
}
