package com.example.ngel.crono;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by Ángel on 26/12/2016.
 */
public class CronoFragment extends Fragment {

    String nombreUsuario,dificultad,resultado; //recuperado del activity anterior
    long start,end;
    double tiempo1,tiempo2;
    int pulsacion=0;
    ImageButton btnGo;
    TextView t;

    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crono,
                container, false);

        nombreUsuario = this.getActivity().getIntent().getExtras().get("nombreUsu").toString();
        dificultad= this.getActivity().getIntent().getExtras().get("dificult").toString();

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
                else{
                    end = System.currentTimeMillis(); //se para el crono
                    resultado = getResultado(start,end,Double.parseDouble(dificultad)); //si devuleve positivo es que se ha pasado
                    System.out.println("El resultado obtenido es: "+resultado);
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
}
