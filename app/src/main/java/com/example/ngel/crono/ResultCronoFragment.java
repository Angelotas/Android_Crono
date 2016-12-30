package com.example.ngel.crono;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
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



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_resultcrono, container, false);

        nombreUsuario = this.getActivity().getIntent().getExtras().get("nombreUsu").toString();
        dificultad = this.getActivity().getIntent().getExtras().get("dificult").toString();
        resultado = this.getActivity().getIntent().getExtras().get("resultado").toString();
        /*this.formatearResultado(); //para formatear el resultado obtenido

        if (resultado.charAt(0) != '+'){ //solo guardaremos el resultado cuando no se haya pasado
            new PostTask().execute(nombreUsuario,dificultad,resultado);
        }*/



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
}