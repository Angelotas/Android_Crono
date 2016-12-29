package com.example.ngel.crono;

/**
 * Created by Ángel on 26/12/2016.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class CronoContract {

    //BASE DE DATOS
    public static final String DB_NAME = "crono.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "puntuaciones";

    public static final String DEFAULT_SORT = Column.ID + " DESC";

    //CONSTANTES CONTENT PROVIDER
    //URI => content://com.example.ngel.crono.CronoProvider/puntuaciones
    public static final String AUTHORITY = "com.example.ngel.crono.CronoProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
    public static final int CRONO_ITEM = 1; //Acceso genérico a una tabla (UriMatcher)
    public static final int CRONO_DIR = 2;  //Acceso a un elemento con id (UriMatcher)

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String USER = "usuario";
        public static final String DIFIC = "dificultad";
        public static final String RESULT = "resultado";
    }

}
