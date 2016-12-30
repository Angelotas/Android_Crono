package com.example.ngel.crono;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Ángel on 29/12/2016.
 */

public class CronoProvider extends ContentProvider {


    private static final String TAG = CronoProvider.class.getSimpleName();
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    //IMPLEMENTACIÓN DE LOS MÉTODOS URIMATCHER
    private static final UriMatcher sURIMatcher; //Intérprete de patrones de la URI
        static { //Se utilizará para saber si una URI hace referencia a una tabla entera o a un registro entero de la tabla
            sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            sURIMatcher.addURI(CronoContract.AUTHORITY, CronoContract.TABLE, CronoContract.CRONO_DIR); //acceso a una tabla
            sURIMatcher.addURI(CronoContract.AUTHORITY, CronoContract.TABLE + "/#", CronoContract.CRONO_ITEM);  //acceso a un elemento con id
        }

        @Override
    public boolean onCreate() {
            dbHelper = new DbHelper(getContext()); //se inicializa el dbhelper
            Log.d(TAG, "onCreated");
            return true;
    }

    //DE LOS SIGUIENTES MÉTODOS SOLO SE UTILIZARÁN LAS CONSULTAS (Mostrar ranking) Y EL BORRADO (Limpriar ranking)

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Solo devuelve información, sin modificaciones en la BD
        //PARÁMETROS => URI, campos SELECT, criterios WHERE, ARGUMENTOS y CRITERIO DE ORDENACIÓN

        String where;

        switch (sURIMatcher.match(uri)) {
            case CronoContract.CRONO_DIR: //Consulta de tabla
                where = selection;
                break;
            case CronoContract.CRONO_ITEM:  //Consulta de elemento con id
                long id = ContentUris.parseId(uri);
                where = CronoContract.Column.ID + "=" + id + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        //Si no se incluye criterio de ordenación se recoge el que está en el Contract
        String orderBy = (TextUtils.isEmpty(sortOrder)) ? CronoContract.DEFAULT_SORT : sortOrder;

        SQLiteDatabase db = dbHelper.getReadableDatabase(); //abre la bbdd como lectura
        //Ejecución de Query
        Cursor cursor = db.query(CronoContract.TABLE, projection, where, selectionArgs, null, null, orderBy);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "registros recuperados: " + cursor.getCount());
        //se utilizará para las listview
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri ret = null;

        //Comprobar que la URI es correcta
        if (sURIMatcher.match(uri) != CronoContract.CRONO_DIR){
            //content://com.example.ngel.crono.CronoProvider/puntuaciones
            throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        db = dbHelper.getWritableDatabase();  //abre la bbdd como escritura
        //ahora se inserta la información contenida en el contentValues
        long rowId = db.insertWithOnConflict(CronoContract.TABLE, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);

        //se inserta correctamente?
        if (rowId != -1){ //en caso de que si --> construir URI a devolver y se notifica
            long id = values.getAsLong(CronoContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id); //se construye nueva uri
            Log.d(TAG, "uri insertada: " + ret);

            // Notificar que los datos para la URI han cambiado
            getContext().getContentResolver().notifyChange(uri, null);
        }
        else
            Log.d(TAG,"uri ya insertada");

        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String where;
        switch (sURIMatcher.match(uri)) { //Se utilizará para limpiar la BD
            case CronoContract.CRONO_DIR:
                where = selection;
                break;
            case CronoContract.CRONO_ITEM:
                long id = ContentUris.parseId(uri);
                where = CronoContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(CronoContract.TABLE, where, selectionArgs);
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros borrados: " + ret);
        return ret;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;

        switch (sURIMatcher.match(uri)) {
            case CronoContract.CRONO_DIR:
                where = selection;
                break;
            case CronoContract.CRONO_ITEM:
                long id = ContentUris.parseId(uri);
                where = CronoContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(CronoContract.TABLE, values, where, selectionArgs);
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros actualizados: " + ret);
        return ret;

    }
}
