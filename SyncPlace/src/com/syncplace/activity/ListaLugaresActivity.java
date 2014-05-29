package com.syncplace.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.syncplace.ErrorDialogFragment;
import com.syncplace.Lugar;
import com.syncplace.R;
import com.syncplace.SensorDB;

public class ListaLugaresActivity extends Activity {
	
	ArrayList<Lugar> lug = new ArrayList<Lugar>();
	ListView lstOpciones;
	AdaptadorTitulares adaptador;
	Context contexto;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.lista);
	    contexto = this;
	    
	    lstOpciones = (ListView)findViewById(R.id.LstOpciones);
	    llena_lista();
	    
	    lstOpciones.setOnItemClickListener(new OnItemClickListener() {
	    	
	    	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	          	// When clicked, show a toast with the TextView text	        		
	    		SensorDB usdbh = new SensorDB(contexto, "DBSensor", null, 1);
	    		SQLiteDatabase db = usdbh.getWritableDatabase();
	    	    
	    		Lugar l = usdbh.buscaLugar(db, (int)id+1);
	    		l = lug.get(position);
	            
	    		LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.dialoglugar, null);
		    	
				EditText etLat = (EditText)v.findViewById(R.id.editTextLat);
				etLat.setText(String.valueOf(l.getLatitud()));
				EditText etLon = (EditText)v.findViewById(R.id.editTextLon);
				etLon.setText(String.valueOf(l.getLongitud()));
				EditText etInfo = (EditText)v.findViewById(R.id.editTextInfo);
				etInfo.setText(l.getDescripcion());
				EditText etNombre = (EditText)v.findViewById(R.id.editTextNombre);
				etNombre.setText(l.getNombre());
				EditText etTipo = (EditText)v.findViewById(R.id.editTextTipo);
				etTipo.setText(l.getTipo());
				
		        ErrorDialogFragment alert = new ErrorDialogFragment();
		        alert.createDialogLugar(contexto, v).show(); 
	    	} 	        
	    });    
	}
	
	private void llena_lista(){
		
		SensorDB usdbh = new SensorDB(this, "DBSensor", null, 1);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		 
		String sql = "SELECT * FROM Lugar";
		Cursor fila = db.rawQuery(sql, null);
		lug.clear();    
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
			    lug.add(s);
			    
			} while(fila.moveToNext());
		}
		else {
		   	System.out.println("Error cursor. No hay lugares");	    	
		}
		    
		db.close();
		fila.close();
		
	    adaptador = new AdaptadorTitulares(this, lug);
	    lstOpciones.setAdapter(adaptador);		
	}
	
	
	class AdaptadorTitulares extends ArrayAdapter<Lugar> {
	    	
	  	Activity context;
	  	ArrayList<Lugar> lugares;
	    	
	   	AdaptadorTitulares(Activity context, ArrayList<Lugar> lugares) {
	   		super(context, R.layout.elementolista, lugares);
	   		this.context = context;
	   		this.lugares = lugares;
	   	}
	    	
	   	public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View item = inflater.inflate(R.layout.elementolista, null);
				
			TextView lblTitulo = (TextView)item.findViewById(R.id.LblTitulo);
			lblTitulo.setText("Nombre: "+lugares.get(position).getNombre()+"\nDescripcion: "+lugares.get(position).getDescripcion());
			
			TextView lblSubtitulo = (TextView)item.findViewById(R.id.LblSubTitulo);
			lblSubtitulo.setText(lugares.get(position).getLatitud()+","+lugares.get(position).getLongitud());
				
			return(item);
		}	    	
	} 
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		System.out.println(requestCode+":::"+resultCode);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
			    llena_lista();
			}
		}
	}
}
