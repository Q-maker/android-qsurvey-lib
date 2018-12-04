package com.android.qmaker.survey.core;

import android.content.Context;
import android.util.Log;

import com.android.qmaker.survey.core.pushers.FileIoPusher;
import com.qmaker.core.entities.CopySheet;
import com.qmaker.core.entities.Test;
import com.qmaker.survey.core.engines.QSurvey;
import com.qmaker.survey.core.entities.Survey;

public class AndroidQSurvey implements QSurvey.SurveyStateListener {
    public final static String TAG = "AndroidQSurvey";
    Context context;
    static AndroidQSurvey instance;

    public Context getContext() {
        return context;
    }

    private AndroidQSurvey(Context context) {
        this.context = context;
    }

    public static AndroidQSurvey initialize(Context context) {
        if (instance != null) {
            throw new IllegalStateException("AndroidQSurvey instance is already initialized and is ready do be get using getInstance");
        }
        instance = new AndroidQSurvey(context);
        instance.init();
        return instance;
    }

    public boolean isInitialized() {
        return instance != null;
    }

    private void init() {
        QSurvey qSurvey = QSurvey.getInstance(true);
        qSurvey.usePersistenceUnit(new SQLitePersistenceUnit());
        qSurvey.registerSurveyStateListener(0, this);
        qSurvey.appendPusher(new FileIoPusher(this.context));
        //TODO start or prepare Workers.
    }

    public QSurvey getQSurveyInstance() {
        return QSurvey.getInstance();
    }

    public static AndroidQSurvey getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AndroidQSurvey was not initialised.");
        }
        return instance;
    }

    @Override
    public void onSurveyCompleted(Survey survey, Test test, CopySheet copySheet) {
        if (Survey.TYPE_SYNCHRONOUS.equals(survey.getType())) {
            //TODO diplay UI
            Log.d(TAG, "survey completed");
        }
    }

    public boolean registerSurveyStateListener(QSurvey.SurveyStateListener stateListener) {
        return getQSurveyInstance().registerSurveyStateListener(stateListener);
    }

    public boolean registerSurveyStateListener(int priority, QSurvey.SurveyStateListener stateListener) {
        return getQSurveyInstance().registerSurveyStateListener(priority, stateListener);
    }

    public boolean unregisterSurveyStateListener(QSurvey.SurveyStateListener stateListener) {
        return getQSurveyInstance().unregisterRunStateListener(stateListener);
    }
}
