package com.android.qmaker.survey.core.engines;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.android.qmaker.survey.core.utils.displayers.DialogUIDisplayer;
import com.qmaker.core.interfaces.RunnableDispatcher;
import com.qmaker.survey.core.engines.PushExecutor;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.interfaces.Pusher;
import com.qmaker.survey.core.utils.PayLoad;
import com.android.qmaker.survey.core.pushers.HttpBasicPusher;
import com.android.qmaker.survey.core.pushers.JwtPusher;
import com.android.qmaker.survey.core.pushers.WssePusher;
import com.qmaker.survey.core.engines.QSurvey;
import com.qmaker.survey.core.entities.Survey;
import com.qmaker.survey.core.pushers.HttpDigestPusher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AndroidQSurvey implements QSurvey.SurveyStateListener {
    public final static String TAG = "AndroidQSurvey";
    Context context;
    static AndroidQSurvey instance;
    final HashMap<String, UIHandler> uiHandlerHashMap = new HashMap();
    Handler mHandler = new Handler(Looper.getMainLooper());

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

    public static void setRunnableDispatcher(RunnableDispatcher runnableDispatcher) {
        QSurvey.setRunnableDispatcher(runnableDispatcher);
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

    public Pusher appendPusher(Pusher pusher) {
        return getQSurveyInstance().appendPusher(pusher);
    }

    public boolean appendUIDisplayer(UIHandler.Displayer uiDisplayer) {
        return appendUIDisplayer(0, uiDisplayer);
    }

    public boolean appendUIDisplayer(int priority, UIHandler.Displayer uiDisplayer) {
        synchronized (uiDisplayers) {
            if (uiDisplayer == null || uiDisplayers.contains(uiDisplayer)) {
                return false;
            }
            if (priority >= 0 && priority <= uiDisplayers.size() - 1) {
                uiDisplayers.add(priority, uiDisplayer);
            } else {
                uiDisplayers.add(uiDisplayer);
            }
            return true;
        }
    }

    public boolean removeUIDisplayer(UIHandler.Displayer uiDisplayer) {
        if (uiDisplayer == null || !uiDisplayers.contains(uiDisplayer)) {
            return false;
        }
        uiDisplayers.remove(uiDisplayer);
        return true;
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
        if (state == QSurvey.SurveyStateListener.STATE_CANCELED) {
            return;
        }
        int bitResult = QSurvey.SurveyStateListener.STATE_FINISH & state;
        if (state == bitResult) {
            Survey.Result result = payLoad.getVariable(0);
            List<PushOrder> pushOrders = payLoad.getVariable(1);
            UIHandler uiHandler = new UIHandler(result, pushOrders, currentShowingActivity);
            uiHandlerHashMap.put(survey.getId(), uiHandler);
            uiHandler.startHandling(this);
        }
    }

    boolean detachUIHandler(Survey survey) {
        if (survey == null) {
            return false;
        }
        return uiHandlerHashMap.remove(survey.getId()) != null;
    }

    boolean detachUIHandler(String surveyId) {
        return uiHandlerHashMap.remove(surveyId) != null;
    }

    public UIHandler getUIHandler(Survey survey) {
        if (survey == null) {
            return null;
        }
        return getUIHandler(survey.getId());
    }

    public UIHandler getUIHandler(String surveyId) {
        return uiHandlerHashMap.get(surveyId);
    }

    protected void dispatchDisplayerSurveyResultPublishStateChanged(final int state, Object... vars) {
        dispatchDisplayerSurveyResultPublishStateChanged(state, new PayLoad(vars));
    }

    protected void dispatchDisplayerSurveyResultPublishStateChanged(final int state, final PayLoad payLoad) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UIHandler.Displayer[] displayers = collectDisplayers();
                if (displayers == null) {
                    return;
                }
                for (UIHandler.Displayer displayer : displayers) {
                    if (displayer != null && displayer.onSurveyResultPublishStateChanged(currentShowingActivity, state, payLoad)) {
                        break;
                    }
                }
            }
        };
        mHandler.post(runnable);
    }

    private UIHandler.Displayer[] collectDisplayers() {
        UIHandler.Displayer[] displayers = null;
        synchronized (uiDisplayers) {
            if (uiDisplayers.size() > 0) {
                displayers = new UIHandler.Displayer[uiDisplayers.size()];
                displayers = uiDisplayers.toArray(displayers);
            }
        }
        return displayers;
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

    public static final UIHandler.Displayer DEFAULT_UI_DISPLAYER = new DialogUIDisplayer();
    static List<UIHandler.Displayer> uiDisplayers = Collections.synchronizedList(new ArrayList() {
        {
            add(DEFAULT_UI_DISPLAYER);
        }
    });

    public final static RunnableDispatcher DEFAULT_RUNNABLE_DISPATCHER = new RunnableDispatcher() {
        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void dispatch(Runnable runnable, int delay) {
            if (runnable != null) {
                if (delay <= 0) {
                    handler.post(runnable);
                } else {
                    handler.postDelayed(runnable, delay);
                }
            }
        }

        @Override
        public void cancel(Runnable runnable) {
            handler.removeCallbacks(runnable);
        }

        @Override
        public void release() {
            handler.removeCallbacksAndMessages(null);
        }

    };
}
