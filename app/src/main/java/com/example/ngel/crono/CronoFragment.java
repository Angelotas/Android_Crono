package com.example.ngel.crono;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by Ángel on 26/12/2016.
 */
public class CronoFragment extends Fragment {

    private DbHelper dbHelper;  //variable para el objeto dbHelper para realizar operaciones sobre la BD
    private SQLiteDatabase db;  //variable para el objeto SQLite
    private String nombreUsuario,dificultad,resultado; //recuperado del activity anterior

    ImageButton btnGo;
    TextView t;
    long start,end;
    int pulsacion=0;

    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crono,
                container, false);
        //Se recupera el nombre de usuario y la dificultad
        nombreUsuario = this.getActivity().getIntent().getExtras().get("nombreUsu").toString();
        dificultad= this.getActivity().getIntent().getExtras().get("dificult").toString();

        dbHelper = new DbHelper(this.getContext()); //se crea el objeto dbHelper

        t= (TextView) view.findViewById(R.id.timeDificultad);
        t.setText(nombreUsuario+"\n Recuerda no pasarte de "+dificultad.toString()+" segundos");

        btnGo= (ImageButton) view.findViewById(R.id.botonCrono);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pulsacion == 0){ //es la primera vez que se pulsa
                    start = System.currentTimeMillis(); //tiempo de inicio de crono
                    int idImagen= getResources().getIdentifier("stop_opt","drawable",getActivity().getPackageName());
                    btnGo.setBackground(getResources().getDrawable(idImagen)); //cambio de imagen para el boton
                    pulsacion++; //se ha realiza la primera pulsación
                }
                else{ //Para el crono y se obtiene el resultado => se mete a la bd si es negativo
                    end = System.currentTimeMillis(); //se para el crono
                    resultado = getResultado(start,end,Double.parseDouble(dificultad)); //si devuleve positivo es que se ha pasado
                    formatearResultado(); //para formatear el resultado obtenido
                    if (resultado.charAt(0) != '+'){ //solo guardaremos el resultado cuando no se haya pasado
                        //En segundo plano se realiza la insercción en la base de datos
                        new CronoFragment.PostTask().execute(nombreUsuario,dificultad,resultado);
                    }

                    //Una vez insertado se pasa al siguiente actíviti donde se mostrarán los resultados
                    Intent intent= new Intent(getActivity(), MainActivity.class);
                    //se envia al siguiente activity el nombre de usuario, la dificultad y su resultado obtenido
                    intent.putExtra("nombreUsu",nombreUsuario);
                    intent.putExtra("dificult",dificultad);
                    intent.putExtra("resultado",resultado);
                    startActivity(intent);
                }
            }
        });


        return view;
    }

    public String getResultado(long ini, long fin,double dificultad){
        double t1= (fin-ini)/1000.0; //los segundos que ha cronocmetrado el usuario
        double t2= t1 - dificultad; //se obtienen los segundos de diferencia sin redondear
        DecimalFormat f= new DecimalFormat("##.000"); //formato que redondea en 3 decimales
        return f.format(t2);
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

    private final class PostTask extends AsyncTask<String, Void, String> {
        //En segundo plano se introducirá en nombre, la dificultad y el resulado en la base de datos
        //Utiliza String como parámetros ,void en Progress y String como resultado
        //params[0] --> nombre usuario  || params[1] --> dificultad  ||  params[2] --> resultado
        private static final String TAG = ".PostTask";
        String textoMostrar, resultadoAntiguo;

        @Override
        protected String doInBackground(String... params) {

            try{ //se realiza en segundo plano la insercción de datos en la bd
                db = dbHelper.getWritableDatabase(); //se abre la BD para escritura

                if(comprobarExisteUser(params[0],params[1]) == true){ //el usuario con ese nombre y esa dificultad existe
                    int numFilasUpdate= actualizarResultadoUser(params[0],params[1],params[2]);
                    textoMostrar = "Se han actualizado NUM FILAS "+numFilasUpdate;
                    return (textoMostrar);
                }
                else {    //el usuario es nuevo para esa dificultad
                    //mapeo de datos para cada elemento de la tabla
                    ContentValues values = new ContentValues();  //mapea los nombres de la base de datos con sus valores correspondientes.
                    values.put(CronoContract.Column.USER, params[0]);
                    values.put(CronoContract.Column.DIFIC, params[1]);
                    values.put(CronoContract.Column.RESULT, params[2]);

                    //INSERCCIÓN DEL CONTENTVALUES EN LA BBDD CON CONTENT PROVIDER
                    Uri uri = getContext().getContentResolver().insert(CronoContract.CONTENT_URI, values); //Método insert del StatusProvider
                    textoMostrar = "Se ha insertado correctamente los datos ";
                    return (textoMostrar);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(CronoFragment.this.getActivity(), result, Toast.LENGTH_LONG).show();
        }

        public boolean comprobarExisteUser(String nombre, String dificultad){
            //REALIZA UNA CONSULTA PARA COMPROBAR SI EXISTE YA ESE USUARIO QUE HA JUGADO CON ESE NIVEL DE DIFICULTAD
            //SELECT resultado FROM puntuaciones WHERE usuario='nombre' AND dificultad='dificultad'
            String[] projection= new String[]{"resultado"}; //la columna usuario
            String[] selectionArgs = new String[]{nombre,dificultad};
            Cursor consulta =getActivity().getContentResolver().query(CronoContract.CONTENT_URI,
                    projection, "usuario=? AND dificultad=?",selectionArgs, null);
            if (consulta.getCount()!=0){ //SI QUE EXISTE YA ESE NOMBRE CON ESA DIFICULTAD
                /*será necesario obtener el resultado para ver si es necesario actualizarlo con el nuevo obtenido
                                    si no se mejora el antiguo, se mantendrá.*/
                consulta.moveToFirst(); //ahora apunta a la primera fila
                int resultadoColumn = consulta.getColumnIndex(CronoContract.Column.RESULT);
                resultadoAntiguo = consulta.getString(resultadoColumn); //obtiene el resultado que tiene ese usuario con esa dificultad
                return true;
            }
            else  //EL USUARIO CON ESA DIFICULTAD NO EXISTE
                return false;
        }

        public int actualizarResultadoUser(String nombre, String dificultad, String nuevoResultado){

            if(resultadoAntiguo.length()!=nuevoResultado.length()){
                //tienen 2 cifras enteras y se compara de otra manera (-12,123 lo toma como mejor que -7,123)
                if (nuevoResultado.length() < resultadoAntiguo.length()){ //se mejora el resultado => se actualiza
                    return updateResult(nombre,dificultad,nuevoResultado);
                }
                else //no se mejora el resultado => no se actualiza
                    return 0;
            }else{
                //tienen la misma longitud, no hay problema al comparar cadenas de caracteres
                if (resultadoAntiguo.compareTo(nuevoResultado) > 0){ //si se mejora el resultado antiguo
                //no hay problema (-7,123 es peor que -1,123)
                    return updateResult(nombre,dificultad,nuevoResultado);
                }
                else{ //no se mejora el resultado antiguo, no se actualiza
                    return 0;
                }
            }
        }
        public int updateResult(String nombre, String dificultad, String nuevoResultado){
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
