package com.syncplace.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.syncplace.CallYelpService;
import com.syncplace.ListaPuntos;
import com.syncplace.Lugar;
import com.syncplace.R;
import com.syncplace.Yelp;

public class Servicios extends Activity {
	
	Spinner servicio;
	Button bservicio;
	Button borigen;
	EditText textlat;
	EditText textlng;
	
	ListaPuntos listaGP;
	Double milat;
	Double milng;
	LocationManager locManager;
	LocationListener locListener;
	Servicios contexto;
	
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
        textlat = (EditText) findViewById(R.id.editTextLatitud);
        textlng = (EditText) findViewById(R.id.editTextLongitud);
        
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
				// TODO Auto-generated method stub
				
				String textospinner = servicio.getSelectedItem().toString();				
				String textoservicio = textospinner.substring(textospinner.indexOf('(')+1, textospinner.indexOf(')'));
			
				if ("".equals(textlat.getText().toString()) || "".equals(textlng.getText().toString())){
					Toast.makeText(contexto,"Debe seleccionar una posición de origen.",Toast.LENGTH_SHORT).show();
				}
				else{				
					milat = Double.valueOf(textlat.getText().toString()).doubleValue();
					milng = Double.valueOf(textlng.getText().toString()).doubleValue();
					
					String Consumer_Key="g8e8XleY3fOygTjFaCHsMA";
					String Consumer_Secret="HoqxWudi4mdqOR4-skgt9N_XuBs";
					String Token="Z0nWe1IZPXR7b3DyeHJ15PdHuQhPcuac";
					String Token_Secret="2wAES1QuJA7TVkrAGwusHgarw70";
					
					Yelp yelp = new Yelp(Consumer_Key, Consumer_Secret, Token, Token_Secret);
				    
					String url = "http://api.yelp.com/business_review_search?lat="+milat+"&long="+milng+"&radius=1&limit=20&ywsid=qWph-Rz2-kNIhtjqPoOnkQ&category="+textoservicio;
					Log.i("URL_YELP", url);
					//CallYelpService cys = new CallYelpService(yelp, milat, milng, textoservicio, contexto);
					CallYelpService cys = new CallYelpService(url, contexto);
					cys.execute();
				}
			}
		});    
	}
	
	public void LanzaMapaLugares(String respStr){
		
		JSONObject respJSON;
		try {
			respJSON = new JSONObject(respStr);
		              
            JSONArray businesses 	 = respJSON.getJSONArray("businesses");
            listaGP = new ListaPuntos();
            
            for (int i=0;i<businesses.length();i++){

            	String nombre = businesses.getJSONObject(i).getString("name"); 
            	String desc = "Dirección: "+businesses.getJSONObject(i).getString("address1")+
            				  "\nCiudad: "+businesses.getJSONObject(i).getString("city")+
            				  "\nPais: "+businesses.getJSONObject(i).getString("country")+
            				  "\nDistancia: "+businesses.getJSONObject(i).getString("distance")+
            				  "\nUrl: "+businesses.getJSONObject(i).getString("url")+
            				  "\nUrl móvil: "+businesses.getJSONObject(i).getString("mobile_url")+
            				  "\nCerrado: "+businesses.getJSONObject(i).getString("is_closed")+
            				  "\nTeléfono: "+businesses.getJSONObject(i).getString("phone");
            				
            	Double latitud	 = businesses.getJSONObject(i).getDouble("latitude");
            	Double longitud = businesses.getJSONObject(i).getDouble("longitude");
            	String tipo = servicio.getSelectedItem().toString();
                
	                //Introducimos punto de origen
                Lugar gp = new Lugar(nombre, desc, latitud, longitud, tipo);
                listaGP.add(gp);                	
            }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
        Intent i=new Intent(Servicios.this,MapaLugaresActivity.class);
        Bundle contenedor=new Bundle();
        contenedor.putParcelable("gplist",listaGP);
        i.putExtras(contenedor);
        i.putExtra("clase", "servicios");
        startActivity(i);
		
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