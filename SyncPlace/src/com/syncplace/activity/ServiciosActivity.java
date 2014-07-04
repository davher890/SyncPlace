package com.syncplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.syncplace.v2.R;

public class ServiciosActivity extends Activity {
	
	Spinner servicio;
	Button bservicio;
	Button borigen;
	TextView textlat;
	TextView textlng;
	
	LocationManager locManager;
	LocationListener locListener;
	public ServiciosActivity contexto;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios);
        
        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy); 
        }        
        contexto = this;        
        bservicio = (Button) findViewById(R.id.buttonbuscaservicio);
        borigen	  = (Button) findViewById(R.id.buttonorigen);
        servicio  = (Spinner) findViewById(R.id.spinnerservicios);
        textlat = (TextView) findViewById(R.id.editTextLatitud);
        textlng = (TextView) findViewById(R.id.editTextLongitud);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.servicios,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicio.setAdapter(adapter);
        
        borigen.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(contexto,MapaLugaresActivity.class);
				i.putExtra("origen", "si");
                startActivityForResult(i, 1); 
			}
		});
        
        bservicio.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {					
				String textoSpinner = servicio.getSelectedItem().toString();
				String textoServicio = textoSpinner.substring(textoSpinner.indexOf('(')+1, textoSpinner.indexOf(')'));
				
				Intent i=new Intent(ServiciosActivity.this,MapaLugaresActivity.class);
		        Bundle contenedor=new Bundle();
		        i.putExtras(contenedor);
		        i.putExtra("clase", "servicios");
		        i.putExtra("servicio", textoServicio);
		        i.putExtra("latitud", textlat.getText().toString());
		        i.putExtra("longitud", textlng.getText().toString());		
		        startActivity(i);
			}
		});    
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Bundle contenedor = intent.getExtras();
		        if (contenedor != null){
		        	Double latitud = contenedor.getDouble("latitud");
		        	Double longitud = contenedor.getDouble("longitud");		        	
		        	
			        textlat.setText(String.valueOf(latitud));
			        textlng.setText(String.valueOf(longitud));;    
		        }
			}
		}
    }
}