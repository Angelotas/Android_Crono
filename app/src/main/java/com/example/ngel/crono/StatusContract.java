package com.example.ngel.crono;

/**
 * Created by Ángel on 26/12/2016.
 */

import android.provider.BaseColumns;

public class StatusContract {

    public static final String DB_NAME = "crono.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "puntuaciones";

    public static final String DEFAULT_SORT = Column.ID + " DESC";

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String USER = "usuario";
        public static final String DIFIC = "dificultad";
        public static final String RESULT = "resultado";
    }

}
