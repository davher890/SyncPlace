package com.syncplace.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.syncplace.ErrorDialogFragment;
import com.syncplace.FavoritoListAdapter;
import com.syncplace.Lugar;
import com.syncplace.SensorDB;
import com.syncplace.v2.R;

public class FavoritosActivity extends Activity {
	
	ArrayList<Lugar> favoritos = new ArrayList<Lugar>();
	ListView lstOpciones;
	FavoritoListAdapter adapter;
	Context contexto;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.lista);
	    contexto = this;
	    
	    lstOpciones = (ListView)findViewById(R.id.LstOpciones);
	    favoritos = consultaFavoritos();	    
	    adapter = new FavoritoListAdapter(this, favoritos);
	    lstOpciones.setAdapter(adapter);
	    
	    lstOpciones.setOnItemClickListener(new OnItemClickListener() {
	    	
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	    	    
	    		Lugar l = (Lugar)parent.getItemAtPosition(position);
	            
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
				EditText textId = (EditText)v.findViewById(R.id.textIdGone);
				textId.setText(""+l.getId());
				
		        ErrorDialogFragment alert = new ErrorDialogFragment();
		        AlertDialog createDialogLugar = alert.createDialogLugar(contexto, v, "");
		        createDialogLugar.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		        createDialogLugar.setOnDismissListener(new OnDismissListener() {					
					@Override
					public void onDismiss(DialogInterface dialog) {
						favoritos = consultaFavoritos();	    
					    adapter = new FavoritoListAdapter((FavoritosActivity)contexto, favoritos);
					    lstOpciones.setAdapter(adapter);						
					}
				});
				createDialogLugar.show();
	    	} 	        
	    });    
	}
	
	private ArrayList<Lugar> consultaFavoritos(){
		
		SensorDB usdbh = new SensorDB(this, "DBSensor", null, 1);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		 
		String sql = "SELECT * FROM Lugar";
		Cursor fila = db.rawQuery(sql, null);
		
		ArrayList<Lugar> lug = new ArrayList<Lugar>();
		//Nos aseguramos de que existe al menos un registro
		if (fila.moveToFirst()) {
		//Recorremos el cursor hasta que no haya m�s registros
			do {
				Number id = Integer.parseInt(fila.getString(0));
				String nombre = fila.getString(1);
			    String descripcion = fila.getString(2);
			    Double lat = Double.valueOf(fila.getString(3)).doubleValue();
			    Double lon = Double.valueOf(fila.getString(4)).doubleValue();
			    String tipo = fila.getString(5);
			    				    		
			    Lugar s = new Lugar(id.intValue(), nombre, descripcion, lat, lon, tipo);
			    lug.add(s);
			    
			} while(fila.moveToNext());
		}
		else {
		   	Log.e("BBDDERROR", "Error cursor. No hay lugares");	    	
		}
		    
		db.close();
		fila.close();
		return lug;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		System.out.println(requestCode+":::"+resultCode);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				favoritos = consultaFavoritos();	    
			    adapter = new FavoritoListAdapter(this, favoritos);
			    lstOpciones.setAdapter(adapter);
			}
		}
	}
}
