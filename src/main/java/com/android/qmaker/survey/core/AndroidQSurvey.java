package com.android.qmaker.survey.core;

import android.content.Context;

import com.qmaker.core.entities.CopySheet;
import com.qmaker.survey.core.engines.QSurvey;
import com.qmaker.survey.core.entities.Survey;

public class AndroidQSurvey implements QSurvey.SurveyStateListener {
    Context context;
    static AndroidQSurvey instance;

    public Context getContext() {
        return context;
    }

    private AndroidQSurvey(Context context) {
        this.context = context;
    }

    public static void initialize(Context context) {
        instance = new AndroidQSurvey(context);
        instance.init();
    }

    private void init() {
        QSurvey.getInstance().registerSurveyStateListener(0, this);
    }

    public static AndroidQSurvey getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AndroidQSurvey was not initialised.");
        }
        return instance;
    }

    @Override
    public void onSurveyCompleted(Survey survey, CopySheet copySheet) {
        //TODO display PushUI.
    }
}
