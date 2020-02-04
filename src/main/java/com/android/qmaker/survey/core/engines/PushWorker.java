package com.android.qmaker.survey.core.engines;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
/**
 * @author Toukea Tatsi J
 */
public class PushWorker extends Service {
    static PushWorker instance;
    public final static String ACTION_COMMAND_SYNC_RESULT = "sync_result";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_COMMAND_SYNC_RESULT:
                break;
            default:
                stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public final static boolean start(Context context, String actionCommand) {
        boolean running = instance != null;
        Intent intent = new Intent(context, PushWorker.class);
        intent.setAction(actionCommand);
        context.startService(intent);
        return running;
    }
}
