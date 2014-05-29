package com.syncplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import com.syncplace.CallYelpService;
import com.syncplace.ListaPuntos;
import com.syncplace.Lugar;
import com.syncplace.R;
import com.syncplace.SensorDB;
import com.syncplace.R.id;
import com.syncplace.R.layout;

import java.util.ArrayList;

public class NuevaRuta extends Activity {
	
	TextView ruta;
	Spinner origen;
	Spinner destino;
	Button calcularuta;
	Button verruta;
	NuevaRuta contexto;
	
	ListaPuntos listaGP = new ListaPuntos();	
	
	ArrayList<String> lug = new ArrayList<String>();
	
	String strorigen;
	String strdestino;
	Lugar lorigen;
	Lugar ldestino;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevaruta);
        
        ruta  			= (TextView)findViewById(R.id.textViewDatoRuta1);
        origen			= (Spinner) findViewById(R.id.spinnerorigen);
        destino 		= (Spinner) findViewById(R.id.spinnerdestino);
        calcularuta		= (Button) findViewById(R.id.buttonCalcula);
        verruta		= (Button) findViewById(R.id.buttonVer);
        
        llenaSpinner();
        
        contexto = this;
        		
        calcularuta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SensorDB usdbh = new SensorDB(contexto, "DBSensor", null, 1);
				SQLiteDatabase db = usdbh.getWritableDatabase();
				
				lorigen = usdbh.buscaLugar(db, lug.get(origen.getSelectedItemPosition()));
				ldestino = usdbh.buscaLugar(db, lug.get(destino.getSelectedItemPosition()));
				
				if (lorigen == null || ldestino == null){
					finish();
				}

				db.close();			
				
				String url = "http://maps.googleapis.com/maps/api/directions/json?" +
						"origin="+lorigen.getLatitud()+","+lorigen.getLongitud()+"&" +
						"destination="+ldestino.getLatitud()+","+ldestino.getLongitud()+"&" +
						"region=es&language=es&sensor=false";

				CallYelpService cys = new CallYelpService(url,contexto);				
				cys.execute();
			}
		});
        
        verruta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                Intent i=new Intent(NuevaRuta.this,MapaLugaresActivity.class);
                Bundle contenedor=new Bundle();
                contenedor.putParcelable("gplist",listaGP);
                i.putExtras(contenedor);
                i.putExtra("clase","nuevaruta");
                startActivity(i);				
			}
		});
    }
    
    public void LanzaMapaRuta(String respStr){
    	
    	try{          
	        
	        JSONObject respJSON  = new JSONObject(respStr);                
	        JSONArray routes 	 = respJSON.getJSONArray("routes");
	        JSONObject bounds	 = routes.getJSONObject(0).getJSONObject("bounds");
	        JSONObject northeast = bounds.getJSONObject("northeast");
	        //JSONObject southwest = bounds.getJSONObject("northeast");
	        
	        Double lat1 = northeast.getDouble("lat");
	        Double lng1 = northeast.getDouble("lng");     
	        //Double lat2 = southwest.getDouble("lat");
	        //Double lng2 = southwest.getDouble("lng");
	        ruta.setText("Origen :"+lorigen.getNombre()+"\nDestino :"+ldestino.getNombre()+"\n");
	        
	        JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
	        JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");
	        JSONObject duration = legs.getJSONObject(0).getJSONObject("duration");
	                        
	        String valuedistance = distance.getString("text");
	        String valueduration = duration.getString("text");     
	        ruta.setText(ruta.getText()+"\nDistancia: "+valuedistance+"\nDuracion: "+valueduration);
	        
	        JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
	        		                
	        int i = 0;
	        while (i<steps.length()) {
	        	ruta.setText(ruta.getText()+"\n· "+steps.getJSONObject(i).getString("html_instructions").toString());
	        	
	        	lat1 = steps.getJSONObject(i).getJSONObject("end_location").getDouble("lat");
	            lng1 = steps.getJSONObject(i).getJSONObject("end_location").getDouble("lng");
	            
	            //Introducimos punto de origen
	            Lugar gp = new Lugar("", "", lat1, lng1, "");
	            listaGP.add(gp);
	            
	        	i++;
			}
	        ruta.setText(ruta.getText().toString().replace("<b>",""));
	        ruta.setText(ruta.getText().toString().replace("</b>",""));
	    }
	    catch(Exception ex)
	    {
	            System.out.println("ServicioRest Error!"+ex);
	    }
    }
    
    public void llenaSpinner(){
    	
    	SensorDB usdbh = new SensorDB(this, "DBSensor", null, 1);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		 
		String sql = "SELECT * FROM Lugar";
		Cursor fila = db.rawQuery(sql, null);
		    
		//Nos aseguramos de que existe al menos un registro
		if (fila.moveToFirst()) {
		//Recorremos el cursor hasta que no haya m�s registros
			do {
				String nombre = fila.getString(1);
			    String descripcion = fila.getString(2);
			    Double lat = Double.valueOf(fila.getString(3)).doubleValue();
			    Double lon = Double.valueOf(fila.getString(4)).doubleValue();
			    String tipo = fila.getString(5);
			    				    		
			    Lugar s = new Lugar(nombre, descripcion, lat, lon, tipo);
			    lug.add(s.getNombre());
			    
			} while(fila.moveToNext());
		}
		else {
		   	System.out.println("Error cursor. No hay lugares");	    	
		}
		    
		db.close();
		fila.close();
    	
		ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lug);
		spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		origen.setAdapter(spinner_adapter);
		destino.setAdapter(spinner_adapter);
    }
}
