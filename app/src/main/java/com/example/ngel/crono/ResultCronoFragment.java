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

        if (resultado.charAt(0) != '+'){ //solo guardaremos el resultado cuando no se haya pasado
            new PostTask().execute(nombreUsuario,dificultad,resultado);
        }



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
        private static final String TAG = ".PostTask";
        String result;

        @Override
        protected String doInBackground(String... params) {

            try{ //se realiza en segundo plano la insercción de datos en la bd
                db = dbHelper.getWritableDatabase(); //se abre la BD para escritura

                if(comprobarExisteUser(params[0],params[1]) == true){ //el usuario con ese nombre y esa dificultad existe
                    int numFilasUpdate= actualizarResultadoUser(params[0],params[1],params[2]);
                    result = "Se ha actualizado correctamente el resulado. NUM FILAS "+numFilasUpdate;
                    return (result);
                }
                else {    //el usuario es nuevo para esa dificultad
                    //mapeo de datos para cada elemento de la tabla
                    ContentValues values = new ContentValues();  //mapea los nombres de la base de datos con sus valores correspondientes.
                    values.put(CronoContract.Column.USER, params[0]);
                    values.put(CronoContract.Column.DIFIC, params[1]);
                    values.put(CronoContract.Column.RESULT, params[2]);

                    //INSERCCIÓN DEL CONTENTVALUES EN LA BBDD CON CONTENT PROVIDER
                    Uri uri = getContext().getContentResolver().insert(CronoContract.CONTENT_URI, values); //Método insert del StatusProvider
                    result = "Se ha insertado correctamente los datos ";
                    return (result);
                }

            }
            catch (Exception e){
                return ("Fallo en la inserccion de la bd");

            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(ResultCronoFragment.this.getActivity(), result, Toast.LENGTH_LONG).show();
        }

        public boolean comprobarExisteUser(String nombre, String dificultad){
            //REALIZA UNA CONSULTA PARA COMPROBAR SI EXISTE YA ESE USUARIO QUE HA JUGADO CON ESE NIVEL DE DIFICULTAD
            //SELECT resultado FROM puntuaciones WHERE usuario='nombre' AND dificultad='dificultad'
            String[] projection= new String[]{"resultado"}; //la columna usuario
            String[] selectionArgs = new String[]{nombre,dificultad};
            Cursor c =getActivity().getContentResolver().query(CronoContract.CONTENT_URI,
                    projection, "usuario=? AND dificultad=?",selectionArgs, null);
            if (c.getCount()!=0){ //SI QUE EXISTE YA ESE NOMBRE CON ESA DIFICULTAD
                return true;
            }
            else  //EL USUARIO CON ESA DIFICULTAD NO EXISTE
                return false;
        }

        public int actualizarResultadoUser(String nombre, String dificultad, String nuevoResultado){
            //REALIZA UNA ACTUALIZACIÓN DEL RESULTADO DE UN DETERMINADO USUARIO CON UNA DETERMINADA DIFICULTADC
            //UPDATE puntuaciones SET resultado='nuevoResultado' WHERE usuario'nombre' AND dificultad='dificultad'
            ContentValues val= new ContentValues();
            val.put("resultado",nuevoResultado); //almacena el nuevo valor para la columna resultado

            String[] args= new String[]{nombre,dificultad}; //los datos=> el nombre de usuario y la dificultad
            String select= "usuario=? AND dificultad=?";
            return getActivity().getContentResolver().update(CronoContract.CONTENT_URI,val,select,args); //nº de filas afectadas

        }
    }

}