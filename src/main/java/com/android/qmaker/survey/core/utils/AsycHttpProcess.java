package com.android.qmaker.survey.core.utils;

import com.istat.freedev.processor.Process;

public class AsycHttpProcess<R, E extends Throwable> extends Process<R, E> {
    @Override
    protected void onExecute(ExecutionVariables executionVariables) throws Exception {

    }

    @Override
    protected void onResume() {

    }

    @Override
    protected void onPaused() {

    }

    @Override
    protected void onStopped() {

    }

    @Override
    protected void onCancel() {

    }

    @Override
    public boolean isPaused() {
        return false;
    }

}