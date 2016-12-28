package com.example.ngel.crono;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ángel on 27/12/2016.
 */
public class ResultCronoFragment extends Fragment {

    private static final String TAG = ".ResultCronoFragment";
    private String nombreUsuario, dificultad, resultado, path;
    private TextView tuResul1,tuResul2;
    private Button btnResult,btnReiniciar,btnCompartir;
    private View view;
    private Bitmap bitmap; //recoge la captura de pantalla

    private DbHelper dbHelper;  //variable para el objeto dbHelper para realizar operaciones sobre la BD
    private SQLiteDatabase db;  //variable para el objeto SQLite

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dbHelper = new DbHelper(this.getContext()); //se crea el objeto dbHelper
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_resultcrono, container, false);

        nombreUsuario = this.getActivity().getIntent().getExtras().get("nombreUsu").toString();
        dificultad = this.getActivity().getIntent().getExtras().get("dificult").toString();
        resultado = this.getActivity().getIntent().getExtras().get("resultado").toString();
        this.formatearResultado(); //para formatear el resultado obtenido

        new PostTask().execute(nombreUsuario,dificultad,resultado);


        tuResul1= (TextView) view.findViewById(R.id.txtResultado);
        tuResul1.setText(nombreUsuario+",\n este es tu resultado:");

        tuResul2= (TextView) view.findViewById(R.id.txtResultado2);
        this.stiloResultado();

        //BOTON JUGAR DE NUEVO
        btnReiniciar = (Button) view.findViewById(R.id.botonJugardeNuevo);
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });

        //BOTON COMPARTIR
        btnCompartir = (Button) view.findViewById(R.id.botonCompartir);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //String mPath = Environment.getDownloadCacheDirectory().toString()+"/captura_resultado.png";
                view.setDrawingCacheEnabled(true); //permitir realizar captura
                bitmap = view.getDrawingCache(); //se realiza un cache de bits de la vista
                view.setDrawingCacheEnabled(false);
                guardarImagen(bitmap);
                System.out.println();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");

                share.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse(new File(path).toString()));
                share.putExtra(Intent.EXTRA_TEXT, "hola getResources().getString(R.string.textoCompartir)");

                startActivity(Intent.createChooser(share, "holagetResources().getString(R.string.labelCompartir)"));
            }
        });


        return view;
    }

    public void formatearResultado(){
        String resultAux;
        if (resultado.charAt(0)=='-'){ //no se ha pasado del tiempo
            if(resultado.charAt(1)==','){ //no recoge el 0
                resultAux=resultado.substring(2,5); //parte decimal
                resultado = "-0,"+resultAux;
            }
        }
        else{
            if (resultado.charAt(0)==','){
                resultAux=resultado.substring(1,4);
                resultado = "+0,"+resultAux;
            }
            else
                resultado = "+"+resultado;
        }
    }

    public void stiloResultado(){
        tuResul2.setText(resultado);
        if (resultado.charAt(0)=='+'){ //si se ha pasado del tiempo
            tuResul2.setTextColor(Color.RED);  //texto de resultado de color rojo
        }
        else
            tuResul2.setTextColor(Color.GREEN);
    }

    public void guardarImagen(Bitmap capturaMap){
        path = Environment.getExternalStorageDirectory()+"/captura_crono.png";
        System.out.println(path);
        File imagen_captura = new File(path);
        if (imagen_captura.exists())
            imagen_captura.delete();
        try{
            FileOutputStream out = new FileOutputStream(imagen_captura);
            capturaMap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private final class PostTask extends AsyncTask<String, Void, String> {
        //En segundo plano se introducirá en nombre, la dificultad y el resulado en la base de datos
        //Utiliza String como parámetros ,void en Progress y String como resultado
        //params[0] --> nombre usuario  || params[1] --> dificultad  ||  params[2] --> resultado

        @Override
        protected String doInBackground(String... params) {

            try{ //se realiza en segundo plano la insercción de datos en la bd
                db = dbHelper.getWritableDatabase(); //se abre la BD para escritura
                ContentValues values = new ContentValues();  //mapea los nombres de la base de datos con sus valores correspondientes.
                values.clear(); //se limpia la base de datos
                //mapeo de datos para cada elemento de la tabla
                values.put(StatusContract.Column.ID,""); //se deja vacío ya que es autoincrement
                values.put(StatusContract.Column.USER, params[0]);
                values.put(StatusContract.Column.DIFIC, params[1]);
                values.put(StatusContract.Column.RESULT, params[2]);

                db.insert(StatusContract.TABLE, null, values); //se el inserta el contentValues en la bd
                db.close();
                return ("Se ha insertado correctamente los datos ");
            }
            catch (Exception e){
                return ("Fallo en la inserccion de la bd");

            }

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(ResultCronoFragment.this.getActivity(), "resulado guardado correcamente", Toast.LENGTH_LONG).show();
        }
    }
}