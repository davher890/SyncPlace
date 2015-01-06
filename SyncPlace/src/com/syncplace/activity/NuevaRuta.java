package com.syncplace.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

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

import com.syncplace.GetService;
import com.syncplace.Lugar;
import com.syncplace.SensorDB;
import com.syncplace.v2.R;

public class NuevaRuta extends Activity {
	
	private TextView ruta;
	private Spinner origen;
	private Spinner destino;
	private Spinner modo;
	private Button calcularuta;
	private Button verruta;
	private NuevaRuta contexto;
	
	private ArrayList<Lugar> listaGP = new ArrayList<Lugar>();	
	
	private ArrayList<String> lug = new ArrayList<String>();
	private HashMap<String, Lugar> mapLugares = new HashMap<String, Lugar>();
	
	private Lugar lorigen;
	private Lugar ldestino;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevaruta);
        
        ruta  			= (TextView)findViewById(R.id.textViewDatoRuta1);
        origen			= (Spinner) findViewById(R.id.spinnerorigen);
        destino 		= (Spinner) findViewById(R.id.spinnerdestino);
        calcularuta		= (Button) findViewById(R.id.buttonCalcula);
        verruta			= (Button) findViewById(R.id.buttonVer);
        modo			= (Spinner) findViewById(R.id.spinnermode);
        
        llenaSpinner();
        
        contexto = this;
        		
        calcularuta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SensorDB usdbh = new SensorDB(contexto);
				SQLiteDatabase db = usdbh.getWritableDatabase();
				
				lorigen = mapLugares.get(origen.getSelectedItem());
				ldestino = mapLugares.get(destino.getSelectedItem());
				
				if (lorigen == null || ldestino == null){
					finish();
				}

				db.close();
				
				String url = "http://maps.googleapis.com/maps/api/directions/json?" +
						"origin="+lorigen.getLatitud()+","+lorigen.getLongitud()+"&" +
						"destination="+ldestino.getLatitud()+","+ldestino.getLongitud()+"&" +
						"region=es&language=es&sensor=false&" + 
						"mode=" + modo.getSelectedItem().toString().toLowerCase(Locale.getDefault());

				GetService cys = new GetService(url,contexto);				
				cys.execute();
			}
		});
        
        verruta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
                Intent i=new Intent(NuevaRuta.this,MapaLugaresActivity.class);
                i.putExtra("clase","nuevaruta");
                
                Bundle b = new Bundle();
                b.putParcelableArrayList("listagp", listaGP);
                i.putExtras(b);
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
	        
	        Double lat1 = northeast.getDouble("lat");
	        Double lng1 = northeast.getDouble("lng");  
	        ruta.setText("Origen :"+lorigen.getNombre()+"\nDestino :"+ldestino.getNombre()+"\n");
	        
	        JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
	        JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");
	        JSONObject duration = legs.getJSONObject(0).getJSONObject("duration");
	                        
	        String valuedistance = distance.getString("text");
	        String valueduration = duration.getString("text");     
	        ruta.setText(ruta.getText()+"\nDistancia: "+valuedistance+"\nDuracion: "+valueduration);
	        
	        JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
	        
	        listaGP.clear();
	        int i = 0;
	        while (i<steps.length()) {
	        	ruta.setText(ruta.getText()+"\n - "+steps.getJSONObject(i).getString("html_instructions").toString());
	        	
	        	lat1 = steps.getJSONObject(i).getJSONObject("start_location").getDouble("lat");
	            lng1 = steps.getJSONObject(i).getJSONObject("start_location").getDouble("lng");
	            
	            //Introducimos punto de origen	    		
	    		listaGP.add(new Lugar(lat1, lng1));
	        	i++;
			}
	        listaGP.add(ldestino);
	        ruta.setText(ruta.getText().toString().replace("<b>",""));
	        ruta.setText(ruta.getText().toString().replace("</b>",""));
	        ruta.setText(ruta.getText().toString().replace("</div>",""));
	        verruta.setVisibility(View.VISIBLE);
	    }
	    catch(Exception ex)
	    {
	    	System.out.println("ServicioRest Error!"+ex);
	    }
    }
    
    public void llenaSpinner(){
    	
    	SensorDB usdbh = new SensorDB(this);
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
			    String phone = fila.getString(6);
			    				    		
			    Lugar s = new Lugar(0, nombre, descripcion, lat, lon, tipo, phone);
			    lug.add(s.getNombre());
			    mapLugares.put(s.getNombre(), s);
			    
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
		
		ArrayAdapter<CharSequence> spinner_mode_adapter = ArrayAdapter.createFromResource( this, R.array.routeMode , android.R.layout.simple_spinner_item);
		modo.setAdapter(spinner_mode_adapter);
    }
}