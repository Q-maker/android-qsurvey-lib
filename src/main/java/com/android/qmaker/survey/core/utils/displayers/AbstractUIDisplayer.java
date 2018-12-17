package com.android.qmaker.survey.core.utils.displayers;

import android.app.Activity;

import com.android.qmaker.survey.core.engines.UIHandler;
import com.qmaker.survey.core.utils.PayLoad;

/**
 * @author Toukea Tatsi J
 */
public abstract class AbstractUIDisplayer implements UIHandler.Displayer {
    UIDisplayerMessageProvider uiDisplayerMessageProvider;

    public void setUiDisplayerMessageProvider(UIDisplayerMessageProvider uiDisplayerMessageProvider) {
        this.uiDisplayerMessageProvider = uiDisplayerMessageProvider;
    }

    public interface UIDisplayerMessageProvider {
        String getMessage(int state, PayLoad payLoad);
    }

}
