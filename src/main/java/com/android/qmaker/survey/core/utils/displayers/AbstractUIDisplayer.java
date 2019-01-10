package com.android.qmaker.survey.core.utils.displayers;

import com.android.qmaker.survey.core.engines.UIHandler;
import com.qmaker.survey.core.utils.PayLoad;

/**
 * @author Toukea Tatsi J
 */
public abstract class AbstractUIDisplayer implements UIHandler.Displayer {
    TextProvider textProvider;

    public void useTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public TextProvider getTextProvider() {
        return textProvider;
    }

    public interface TextProvider {
        String getText(int id, PayLoad payLoad);
    }

}
