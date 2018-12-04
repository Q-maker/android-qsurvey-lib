package com.android.qmaker.survey.core.pushers;

import android.content.Context;

import com.android.qmaker.core.memories.FileCache;
import com.android.qmaker.survey.core.AndroidQSurvey;
import com.qmaker.survey.core.engines.PushResult;
import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.entities.Repository;
import com.qmaker.survey.core.interfaces.PushProcess;
import com.qmaker.survey.core.interfaces.Pusher;
import com.qmaker.survey.core.utils.ThreadPushProcess;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import istat.android.base.tools.ToolKits;


public class FileIoPusher implements Pusher {
    public final static String ACCEPTED_GRAND_TYPE = Repository.GRAND_TYPE_WSSE;// "file";
    File rootDir;

    public FileIoPusher(Context context) {
        this(FileCache.newInstance(context).getRootDir());
    }

    public FileIoPusher(File rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public PushProcess push(PushOrder order, Callback callback) {
        FileWriteProcess process = new FileWriteProcess();
        process.proceed(order, callback);
        return process;
    }

    @Override
    public String getSupportedGrandType() {
        return ACCEPTED_GRAND_TYPE;
    }

    class FileWriteProcess extends ThreadPushProcess {

        @Override
        protected void run(PushOrder order) throws IOException {
            String fileName = order.getId();
            File orderFile = new File(rootDir, fileName);
            OutputStream outputStream = new FileOutputStream(orderFile);
            ToolKits.Stream.copyStream(new ByteArrayInputStream(order.toString().getBytes()), outputStream);
            outputStream.close();
            notifySuccess(new PushResult(order));
        }
    }
}
