package com.android.qmaker.survey.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.qmaker.survey.core.engines.PushExecutor;
import com.qmaker.survey.core.utils.PayLoad;
import com.android.qmaker.survey.core.pushers.HttpBasicPusher;
import com.android.qmaker.survey.core.pushers.JwtPusher;
import com.android.qmaker.survey.core.pushers.WssePusher;
import com.qmaker.survey.core.engines.QSurvey;
import com.qmaker.survey.core.entities.Survey;
import com.qmaker.survey.core.pushers.HttpDigestPusher;

import java.util.List;
//TODO se décider a untiliser un UIDisplayerProvider ou un seul useUIDIsplayer
public class AndroidQSurvey implements QSurvey.SurveyStateListener {
    public final static String TAG = "AndroidQSurvey";
    Context context;
    static AndroidQSurvey instance;
    UIHandler.Displayer uiDisplayer = DEFAULT_UI_DISPLAYER;

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

    public void terminate() {
        Application application = instance.getApplication();
        if (application != null) {
            application.unregisterActivityLifecycleCallbacks(mActivityLifeCycleListener);
        }
        QSurvey qSurvey = QSurvey.getInstance(true);
        qSurvey.unregisterSurveyStateListener(this);
        instance = null;
    }

    public boolean isInitialized() {
        return instance != null;
    }

    private void init() {
        QSurvey qSurvey = prepare();
//        qSurvey.appendPusher(new FileIoPusher(this.context));
        qSurvey.appendPusher(new JwtPusher());
        qSurvey.appendPusher(new WssePusher());
        qSurvey.appendPusher(new HttpBasicPusher());
        qSurvey.appendPusher(new HttpDigestPusher());
        //TODO start or prepare Workers.
    }

    public void useUIDisplayer(UIHandler.Displayer uiDisplayer) {
        this.uiDisplayer = uiDisplayer;
    }

    private QSurvey prepare() {
        Application application = getApplication();
        if (application != null) {
            application.registerActivityLifecycleCallbacks(mActivityLifeCycleListener);
        }
        QSurvey qSurvey = QSurvey.getInstance(true);
        qSurvey.usePersistenceUnit(new SQLitePersistenceUnit());
        qSurvey.registerSurveyStateListener(0, this);
        return qSurvey;
    }

    public void reset() {
        Application application = getApplication();
        if (application != null) {
            application.unregisterActivityLifecycleCallbacks(mActivityLifeCycleListener);
        }
        prepare();
    }

    public List<PushExecutor.Task> syncResults() {
        return getQSurveyInstance().syncResults();
    }

    private Application getApplication() {
        try {
            return (Application) context.getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    public void onSurveyStateChanged(int state, Survey survey, PayLoad payLoad) {
        if (state == QSurvey.SurveyStateListener.STATE_FINISH &&
                Survey.TYPE_SYNCHRONOUS.equals(survey.getType())) {
            if (uiDisplayer != null) {
                uiDisplayer.onSurveyResultPublishStateChanged(currentShowingActivity, UIHandler.Displayer.STATE_STARTED, payLoad);
                getPushExecutor().registerExecutionStateChangeListener(new PushExecutor.ExecutionStateChangeListener() {
                    @Override
                    public void onTaskStateChanged(PushExecutor.Task task) {

                    }
                });
            }
        }
    }

    PushExecutor getPushExecutor() {
        return getQSurveyInstance().getPushExecutor();
    }

    public boolean registerSurveyStateListener(QSurvey.SurveyStateListener stateListener) {
        return getQSurveyInstance().registerSurveyStateListener(stateListener);
    }

    public boolean registerSurveyStateListener(int priority, QSurvey.SurveyStateListener stateListener) {
        return getQSurveyInstance().registerSurveyStateListener(priority, stateListener);
    }

    public boolean unregisterSurveyStateListener(QSurvey.SurveyStateListener stateListener) {
        return getQSurveyInstance().unregisterSurveyStateListener(stateListener);
    }

    Activity currentShowingActivity = null;
    private Application.ActivityLifecycleCallbacks mActivityLifeCycleListener = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            currentShowingActivity = activity;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            currentShowingActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            currentShowingActivity = null;
        }
    };

    public static final UIHandler.Displayer DEFAULT_UI_DISPLAYER = new UIHandler.Displayer() {
        @Override
        public void onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad) {
            if (STATE_STARTED == state) {

            } else if (STATE_SUCCESS == state) {

            } else if (STATE_FAILED == state) {

            }
        }
    };
}
