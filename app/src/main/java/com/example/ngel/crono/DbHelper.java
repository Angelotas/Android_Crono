package com.example.ngel.crono;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ángel on 26/12/2016.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    // Constructor
    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }

    //Llamado para crear la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {
        //id (int)  nombre de usuario (text)  dificultad(text)  resultado(text)
        String sql = String.format("create table %s (%s int primary key autoincrement, %s text, %s text, %s text)",
                StatusContract.TABLE,
                StatusContract.Column.ID,
                StatusContract.Column.USER,
                StatusContract.Column.DIFIC,
                StatusContract.Column.RESULT);
        Log.d(TAG, "onCreate con SQL: " + sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //solo en caso de tener una versión mas nueva
        db.execSQL("drop table if exists " + StatusContract.TABLE); //elimina bbdd anterior
        onCreate(db); // crea una base de datos nueva
        Log.d(TAG, "onUpgrade");
    }

}
