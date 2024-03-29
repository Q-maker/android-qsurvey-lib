package com.android.qmaker.survey.core.utils.displayers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import com.qmaker.core.entities.Marks;
import com.qmaker.core.utils.CopySheetUtils;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.entities.Survey;
import com.qmaker.survey.core.utils.PayLoad;

import java.util.List;

/**
 * @author Toukea Tatsi J
 */
//TODO pour une bonne efficacité il devrai être joué en colaboration avec un Service.
public class NotificationUIDisplayer extends AbstractUIDisplayer {
    protected ProgressDialog progressDialog;
    Activity currentActivity;

    @Override
    public boolean onSurveyResultPublishStateChanged(final Activity currentActivity, int state, PayLoad payLoad) {
        if (currentActivity == null || currentActivity.isFinishing()) {
            return false;
        }
        Survey.Result result = payLoad.getVariable(0);
        if (result == null || result.getSource() == null || result.getSource().isBlockingPublisherNeeded() || result.getSource().isAnonymous()) {
            return false;
        }
        if (STATE_STARTED == state) {
            this.currentActivity = currentActivity;
            displayPublishStarting(currentActivity, payLoad);
        } else if (STATE_PROGRESS == state) {
            if (progressDialog != null) {
                displayPublishProgressing(currentActivity, payLoad);
            }
        } else if (STATE_FINISH == state) {
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

    protected void displayPublishStarting(Activity currentActivity, PayLoad payload) {
        progressDialog = new ProgressDialog(currentActivity);
        progressDialog.setMessage(getTextProvider().getText(STATE_STARTED, payload));
        progressDialog.show();
    }

    protected void displayPublishProgressing(Activity currentActivity, PayLoad payLoad) {
        progressDialog.setMessage(getTextProvider().getText(STATE_PROGRESS, payLoad));
    }

    void displayPublishCompleted(final Activity currentActivity, final PayLoad payLoad) {
        AlertDialog.Builder builder = preparePublishCompletedDialog(currentActivity, payLoad);
        builder.create().show();
    }

    protected AlertDialog.Builder preparePublishCompletedDialog(final Activity currentActivity, PayLoad payLoad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setTitle(getTextProvider().getText(TEXT_ID_FINISH_RESULT_TITLE, payLoad));
        String exitButtonText = getTextProvider().getText(TEXT_ID_FINISH_RESULT_ACTION_EXIT, payLoad);
        builder.setMessage(getTextProvider().getText(TEXT_ID_FINISH_RESULT_MESSAGE, payLoad))
                .setPositiveButton(exitButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentActivity.finish();
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
    public void useTextProvider(AbstractUIDisplayer.TextProvider textProvider) {
        super.useTextProvider(textProvider == null ? DEFAULT_TEXT_PROVIDER : textProvider);
    }

    public final static AbstractUIDisplayer.TextProvider DEFAULT_TEXT_PROVIDER = new AbstractUIDisplayer.TextProvider() {

        @Override
        public String getText(int id, PayLoad payLoad) {
            switch (id) {
                case TEXT_ID_PUBLISH_STARTING:
                    return "Please wait, result publishing...";
                case TEXT_ID_PUBLISH_PROGRESSING:
                    List<PushOrder> list = payLoad.getVariable(2);
                    Survey.Result result = payLoad.getVariable(0);
                    int repositoryCount = result.getSource().getRepositories().size();
                    return "Please wait, result publishing " + (repositoryCount - list.size()) + "/" + repositoryCount;
                case TEXT_ID_FINISH_RESULT_TITLE:
                    return "Result published";
                case TEXT_ID_FINISH_RESULT_MESSAGE:
                    try {
                        result = payLoad.getVariable(0);
                        Marks marks = CopySheetUtils.getMarks(result.getCopySheet());
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
