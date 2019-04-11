package com.android.qmaker.survey.core.utils.displayers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.qmaker.core.entities.Marks;
import com.qmaker.core.utils.CopySheetUtils;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.entities.Survey;
import com.qmaker.survey.core.utils.PayLoad;

import java.util.List;

/**
 * @author Toukea Tatsi J
 */
public class DialogUIDisplayer extends AbstractUIDisplayer {
    protected android.app.AlertDialog progressDialog;
    Activity currentActivity, publishingActivity;

    @Override
    public boolean onSurveyResultPublishStateChanged(final Activity currentActivity, int state, PayLoad payLoad) {
        if (currentActivity == null || currentActivity.isFinishing()) {
            return false;
        }
        Survey.Result result = payLoad.getVariable(0);
        if (result == null || result.getOrigin() == null || !result.getOrigin().isBlockingPublisherNeeded()) {
            return false;
        }
        if (STATE_STARTED == state) {
            this.currentActivity = currentActivity;
            this.publishingActivity = currentActivity;
            this.progressDialog = displayPublishStarting(currentActivity, payLoad);
        } else if (STATE_PROGRESS == state) {
            this.currentActivity = currentActivity;
            if (progressDialog != null) {
                displayPublishProgressing(this.progressDialog, payLoad);
            }
        } else if (STATE_FINISH == state) {
            //TODO traiter de façon specifique les cas de failed.
            if (progressDialog != null) {
                progressDialog.cancel();
            }
            displayPublishCompleted(currentActivity, payLoad);
            this.currentActivity = null;
        }
        return true;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    protected android.app.AlertDialog displayPublishStarting(Activity currentActivity, PayLoad payload) {
        if (currentActivity == null || currentActivity.isFinishing()) {
            return null;
        }
        progressDialog = new ProgressDialog(currentActivity);
        progressDialog.setMessage(getTextProvider().getText(STATE_STARTED, payload));
        //TODO serait t'il mieux d'intercepter l'action de cancel et d'en profiter pour cancel le push?
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }

    protected void displayPublishProgressing(android.app.AlertDialog dialog, PayLoad payLoad) {
        if (dialog == null) {
            return;
        }
        dialog.setMessage(getTextProvider().getText(STATE_PROGRESS, payLoad));
    }

    void displayPublishCompleted(final Activity activity, final PayLoad payLoad) {
        if (activity == null) {
            return;
        }
        AlertDialog.Builder builder = preparePublishCompletedDialog(activity, payLoad);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected AlertDialog.Builder preparePublishCompletedDialog(final Activity activity, PayLoad payLoad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(getTextProvider().getText(TEXT_ID_FINISH_RESULT_TITLE, payLoad));
        String exitButtonText = getTextProvider().getText(TEXT_ID_FINISH_RESULT_ACTION_EXIT, payLoad);
        builder.setMessage(getTextProvider().getText(TEXT_ID_FINISH_RESULT_MESSAGE, payLoad))
                .setPositiveButton(exitButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (activity == publishingActivity) {
                            activity.finish();
                        }
                    }
                });
        return builder;
    }

    {
        useTextProvider(DEFAULT_TEXT_PROVIDER);
    }

    public final static int
            TEXT_ID_PUBLISH_STARTING = STATE_STARTED,
            TEXT_ID_PUBLISH_PROGRESSING = STATE_PROGRESS,
            TEXT_ID_FINISH_RESULT_TITLE = 20,
            TEXT_ID_FINISH_RESULT_MESSAGE = 21,
            TEXT_ID_FINISH_RESULT_ACTION_OK = 22,
            TEXT_ID_FINISH_RESULT_ACTION_REPLAY = 23,
            TEXT_ID_FINISH_RESULT_ACTION_EXIT = 24,
            TEXT_ID_FINISH_RESULT_ACTION_SHOW_CORRECTION = 25;

    @Override
    public void useTextProvider(TextProvider textProvider) {
        super.useTextProvider(textProvider == null ? DEFAULT_TEXT_PROVIDER : textProvider);
    }

    public final static TextProvider DEFAULT_TEXT_PROVIDER = new TextProvider() {

        @Override
        public String getText(int id, PayLoad payLoad) {
            switch (id) {
                case TEXT_ID_PUBLISH_STARTING:
                    return "Please wait, result publishing...";
                case TEXT_ID_PUBLISH_PROGRESSING:
                    List<PushOrder> list = payLoad.getVariable(2);
                    Survey.Result result = payLoad.getVariable(0);
                    int repositoryCount = result.getOrigin().getRepositories().size();
                    return "Please wait, result publishing " + (repositoryCount - list.size()) + "/" + repositoryCount;
                case TEXT_ID_FINISH_RESULT_TITLE:
                    return "Result published";
                case TEXT_ID_FINISH_RESULT_MESSAGE:
                    try {
                        result = payLoad.getVariable(0);
                        Marks marks = CopySheetUtils.getMarks(result.getCopySheet(), result.getOrigin().getQuestionnaire());
                        return "Result published\n\nScore: " + marks.getValue() + "/" + marks.getMaximum();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Result published";
                    }
                case TEXT_ID_FINISH_RESULT_ACTION_OK:
                    return "Ok";
                case TEXT_ID_FINISH_RESULT_ACTION_SHOW_CORRECTION:
                    return "Correct";
                case TEXT_ID_FINISH_RESULT_ACTION_REPLAY:
                    return "Replay";
                case TEXT_ID_FINISH_RESULT_ACTION_EXIT:
                    return "Exit";
            }
            return null;
        }
    };
}
