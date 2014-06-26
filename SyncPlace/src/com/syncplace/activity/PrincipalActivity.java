package com.syncplace.activity;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.MapController;
import com.syncplace.R;
import com.syncplace.SensorDB;
import com.syncplace.R.id;
import com.syncplace.R.layout;

public class PrincipalActivity extends Activity {
	
	MapController mc;
	//MapView mapa;
	EditText direccion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.principal);
		
		Button bmapa = (Button) findViewById(R.id.buttonmapa);
        Button blista = (Button) findViewById(R.id.buttonLista);
        //Button bdatos = (Button) findViewById(R.id.buttondatos);
        //Button bt = (Button) findViewById(R.id.buttonBluetooth);
        Button bruta = (Button) findViewById(R.id.buttonRuta);
        Button bserv = (Button) findViewById(R.id.buttonservicios);
        
        /***************Inicializamos la base de datos ***************/
        //introduceBBDD();	
        /*************************************************************/
        
        bmapa.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrincipalActivity.this, MapaLugaresActivity.class);
				startActivity(i);
			}
		});
        
        blista.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrincipalActivity.this, ListaLugaresActivity.class);
				startActivity(i);
			}
		});
        
        /*bdatos.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrincipalActivity.this, MuestraTabla.class);
				startActivity(i);
			}
		});*/
        
        /*bt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrincipalActivity.this, BTChatActivity.class);
				startActivity(i);
			}
		});
        */
        bruta.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrincipalActivity.this, NuevaRuta.class);
				startActivity(i);
			}
		});
        
        bserv.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrincipalActivity.this, ServiciosActivity.class);
				startActivity(i);
			}
		});
	}
	public void introduceBBDD(){
		
		boolean flag_vacia=false;
		
		SensorDB usdbh = new SensorDB(this, "DBSensor", null, 1);
		SQLiteDatabase db = usdbh.getWritableDatabase();
	    
		if(db != null){
			
			String arg[]={};
			String sql = "SELECT _id, COUNT(_id) FROM Lugar";
		    Cursor fila = db.rawQuery(sql, arg);
		    
		    //Nos aseguramos de que existe al menos un registro
		    if (fila.moveToFirst()) {
		    	do{
		    		System.out.println("Numero de elementos de la tabla: "+fila.getString(1).toString());
		    		if (Integer.parseInt(fila.getString(1).toString()) == 0){
		    			flag_vacia=true;
		    		}
		    		
		    	}while(fila.moveToNext());
		    }
		    	
		    if (flag_vacia == true){
					        
		    	/*****Insertamos los datos en la tabla Sensor*****/
				
				usdbh.intLugar(db, "BarEsquina", "--",  42.7147, -6.8994, "Bar");
				usdbh.intLugar(db, "100monta", "--",  42.4883, -0.2197, "Bar");
				usdbh.intLugar(db, "sureña", "--",  38.4449, -1.4501, "Bar");
				usdbh.intLugar(db, "Prueba1", "--",  38.5137, -5.8886, "Bar");
				usdbh.intLugar(db, "Restaurante", "--", 41.1864, -3.5706, "Bar");

		        /***************************************************/
		    }
		    fila.close();
		}
		db.close();
	}
}