package com.example.ngel.crono;


import android.database.Cursor;
import android.support.v4.app.Fragment;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.util.Log;

import java.util.Random;

/**
 * Created by Ángel on 27/12/2016.
 */
public class RankingCronoFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RankingCronoFragment.class.getSimpleName();
    private ListView lista;
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {CronoContract.Column.USER,
            CronoContract.Column.DIFIC, CronoContract.Column.RESULT};
    private static final int[] TO = {R.id.list_item_text_user,
            R.id.list_item_text_dificult, R.id.list_item_text_result};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_rankingcrono, container, false);

        lista = (ListView) view.findViewById(R.id.list_ranking);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor c=getActivity().getContentResolver().query(CronoContract.CONTENT_URI,
                null, null, null, CronoContract.DEFAULT_SORT);  //Consulta de la tabla completa almacenada en el cursor
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_crono,
                c, FROM, TO, 0);
        lista.setAdapter(mAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Se realiza una consulta sobre todos los registros
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(
                getActivity(),CronoContract.CONTENT_URI,
                null, null, null, CronoContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        Log.d(TAG, "onLoadFinished with cursor: " + cursor.getCount());
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}
