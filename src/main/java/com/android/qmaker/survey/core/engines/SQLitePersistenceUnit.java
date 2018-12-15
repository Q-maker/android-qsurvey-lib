package com.android.qmaker.survey.core.engines;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.qmaker.survey.core.entities.PushOrder;
import com.qmaker.survey.core.interfaces.PersistenceUnit;

import java.util.List;

import istat.android.data.access.sqlite.SQLite;
import istat.android.data.access.sqlite.utils.TableUtils;
/**
 * @author Toukea Tatsi J
 */
public class SQLitePersistenceUnit implements PersistenceUnit {
    final static String DB_NAME = "survey.db";
    final static int DB_VERSION = 1;

    SQLitePersistenceUnit() {
        this(DB_NAME, DB_VERSION);
    }

    SQLitePersistenceUnit(String dbName, int dbVersion) {
        SQLite.addConnection(new SQLite.SQLiteConnection(getContext(), dbName, dbVersion) {
            @Override
            public void onCreateDb(SQLiteDatabase db) {
                try {
                    TableUtils.create(db, PushOrder.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        });
    }

    @Override
    public boolean persist(PushOrder order) {
        try {
            return getSQL().persist(order).execute().length > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<PushOrder> findAll() {
        try {
            getSQL().findAll(PushOrder.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(PushOrder order) {
        try {
            return getSQL().delete(order);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Context getContext() {
        return AndroidQSurvey.getInstance().getContext();
    }

    private SQLite.SQL getSQL() throws Exception {
        return SQLite.fromConnection(DB_NAME);
    }
}
