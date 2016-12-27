package com.example.ngel.crono;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Ángel on 26/12/2016.
 */

public class LoginFragment extends Fragment {

    private static final String TAG = ".LoginFragment";
    String dificultad="null"; //esta variable almacena el nivel de dificultad dependiendo del radio button seleccionado
    RadioGroup rdgGrupo; //los diferenctes radio buttons
    Button btn;
    EditText edt;
    DbHelper dbHelper;
    SQLiteDatabase db;

    public View onCreateView(final LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_login,container,false);

        dbHelper = new DbHelper(getActivity()); //operaciones con la BD

        edt = (EditText) view.findViewById(R.id.txtNombre); //nombre de usuario

        rdgGrupo = (RadioGroup) view.findViewById(R.id.rdgGrupo);
        rdgGrupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){ //para saber que dificultad se selecciona

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.facil_5){
                    dificultad="5";
                    Log.e(TAG,"Seleccion 5");
                }else if (checkedId == R.id.medio_10){
                    dificultad="10";
                    Log.e(TAG,"Seleccion 10");
                }else if (checkedId == R.id.dificil_15){
                    dificultad="15";
                    Log.e(TAG,"Seleccion 15");
                }
            }
        });

        btn = (Button) view.findViewById(R.id.btnJugar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camposCorrectos()){
                    Intent intent= new Intent(getActivity(), CronoActivity.class);
                    intent.putExtra("nombreUsu",edt.getText().toString());
                    intent.putExtra("dificult",dificultad);
                    startActivity(intent);
                }

            }
        });
        return view;
    }

    public boolean camposCorrectos(){
        if (edt.getText().length() == 0 || edt.getText().toString().equals(" ")){
            edt.setError("Campo vacío");
            return false;
        }
        else if(edt.getText().length() > 20){
            edt.setError("El nombre de usuario no puede tener mas de 20 caraceres");
            return false;
        }
        else{ //campo nombre de usuario correcto
            if (dificultad.equals("null")){
                Log.e(TAG,"No hay dificultad seleccionada");
                Toast.makeText(LoginFragment.this.getActivity(),"Dificultad no seleccionada",Toast.LENGTH_LONG).show();
                return false;
            }
            else
                return true;
        }
    }
}
