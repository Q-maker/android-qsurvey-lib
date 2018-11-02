package com.devup.qcm.survey.android;

import android.content.Context;

public class AndroidQSurvey {
    Context context;
    static AndroidQSurvey instance;

    public Context getContext() {
        return context;
    }

    public static void initialize(Context context) {

    }

    public static AndroidQSurvey getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AndroidQSurvey was not initialised.");
        }
        return instance;
    }
}
