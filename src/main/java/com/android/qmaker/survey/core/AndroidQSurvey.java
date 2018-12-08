package com.android.qmaker.survey.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.qmaker.survey.core.engines.PushExecutor;
import com.qmaker.survey.core.utils.PayLoad;
import com.qmaker.survey.core.utils.pushers.HttpBasicPusher;
import com.qmaker.survey.core.utils.pushers.JwtPusher;
import com.qmaker.survey.core.utils.pushers.MemoryPusher;
import com.qmaker.survey.core.utils.pushers.WssePusher;
import com.qmaker.survey.core.engines.QSurvey;
import com.qmaker.survey.core.entities.Survey;

import java.util.List;

public class AndroidQSurvey implements QSurvey.SurveyStateListener {
    public final static String TAG = "AndroidQSurvey";
    Context context;
    static AndroidQSurvey instance;
    UIDisplayer uiDisplayer = DEFAULT_UI_DISPLAYER;

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
        qSurvey.unregisterRunStateListener(this);
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
        qSurvey.appendPusher(new MemoryPusher());
        //TODO start or prepare Workers.
    }

    public void useUIDisplayer(UIDisplayer uiDisplayer) {
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
        Survey.Result result = payLoad.getVariable(0, Survey.Result.class);
        if (Survey.TYPE_SYNCHRONOUS.equals(result.getOrigin().getType())) {
            if (uiDisplayer != null) {
                uiDisplayer.onSurveyResultPublishStateChanged(currentShowingActivity, UIDisplayer.STATE_STARTED, payLoad);
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
        return getQSurveyInstance().unregisterRunStateListener(stateListener);
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

    public interface UIDisplayer {
        int STATE_STARTED = 0, STATE_SUCCESS = 1, STATE_FAILED = 2;

        void onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad);
    }

    public static final UIDisplayer DEFAULT_UI_DISPLAYER = new UIDisplayer() {
        @Override
        public void onSurveyResultPublishStateChanged(Activity currentActivity, int state, PayLoad payLoad) {
            if (STATE_STARTED == state) {

            } else if (STATE_SUCCESS == state) {

            } else if (STATE_FAILED == state) {

            }
        }
    };
}
