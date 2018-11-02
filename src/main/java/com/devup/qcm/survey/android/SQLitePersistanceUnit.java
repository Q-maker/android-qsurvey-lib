package com.devup.qcm.survey.android;

import com.devup.qcm.survey.engines.QSurvey;
import com.devup.qcm.survey.entities.PushOrder;

import java.util.List;

public class SQLitePersistanceUnit implements QSurvey.PersistenceUnit {
    @Override
    public void persist(PushOrder order) {

    }

    @Override
    public List<PushOrder> findAll() {
        return null;
    }

    @Override
    public void delete(PushOrder order) {

    }
}
