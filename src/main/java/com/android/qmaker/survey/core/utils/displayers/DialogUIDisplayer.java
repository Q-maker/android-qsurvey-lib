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
    protected ProgressDialog progressDialog;

    @Override
    public boolean onSurveyResultPublishStateChanged(final Activity currentActivity, int state, PayLoad payLoad) {
        if (currentActivity == null || currentActivity.isFinishing()) {
            return false;
        }
        if (STATE_STARTED == state) {
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

        }
        return true;
    }

    protected void displayPublishStarting(Activity currentActivity, PayLoad payload) {
        progressDialog = new ProgressDialog(currentActivity);
        progressDialog.setMessage(getTextProvider().getText(STATE_STARTED, payload));
        progressDialog.show();
    }

    protected void displayPublishProgressing(Activity currentActivity, PayLoad payLoad) {
        List<PushOrder> list = payLoad.getVariable(2);
        Survey.Result result = payLoad.getVariable(0);
        int repositoryCount = result.getOrigin().getRepositories().size();
        progressDialog.setMessage("Please wait, result publishing " + (repositoryCount - list.size()) + "/" + repositoryCount);

    }

    protected void displayPublishCompleted(final Activity currentActivity, PayLoad payLoad) {
        final Survey.Result result = payLoad.getVariable(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        //TODO build publish summary, with retry capbility.
        builder.setTitle("Result");
        builder.setMessage("Result published.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (result.getOrigin().getQuestionnaireConfig().isAutoCorrectionEnable()) {
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                                Marks marks = CopySheetUtils.getMarks(result.getCopySheet(), result.getOrigin().getQuestionnaire());
                                builder.setMessage("Score: " + marks.getValue() + "/" + marks.getMaximum());
                                builder.create().show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).create().show();
    }

    {
        useTextProvider(DEFAULT_TEXT_PROVIDER);
    }

    @Override
    public void useTextProvider(TextProvider textProvider) {
        super.useTextProvider(textProvider == null ? DEFAULT_TEXT_PROVIDER : textProvider);
    }

    public final static TextProvider DEFAULT_TEXT_PROVIDER = new TextProvider() {

        @Override
        public String getText(int state, PayLoad payLoad) {
            switch (state) {
                case STATE_STARTED:
                    return "Please wait, result publishing...";
                case STATE_PROGRESS:
                    break;
                case STATE_FINISH:
                    break;
            }
            return null;
        }
    };
}
