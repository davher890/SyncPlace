package com.syncplace.activity;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.ads.AdView;
import com.google.android.maps.MapController;
import com.syncplace.SensorDB;
import com.syncplace.v2.R;

public class PrincipalActivity extends Activity {
	
	/*servidor = mysql.nixiweb.com
	usuario = u440709988_david
	base de datos = u440709988_david
	contraseña = hypsyfzj0468*/

	String IP_Server="192.168.3.20";//IP DE NUESTRO PC
    String URL_connect="http://"+IP_Server+"/droidlogin/acces.php";//ruta en donde estan nuestros archivos
	
	MapController mc;
	//private AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.principal);
        
        /*adView = new AdView(this);
	    adView.setAdUnitId("ca-app-pub-8047492237634735/5661026866");
	    adView.setAdSize(AdSize.BANNER);
	    
	    LinearLayout layout = (LinearLayout)findViewById(R.id.principalAd);
	    layout.addView(adView);

	    // Iniciar una solicitud genérica.
	    //AdRequest adRequest = new AdRequest.Builder().build();
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // Todos los emuladores
	    .addTestDevice("D221E39BD0B771BA8205EBFB62B4D3F3")  // Mi teléfono de prueba Samsung Galaxy s3
	    .build();
	    


	    // Cargar adView con la solicitud de anuncio.
	    adView.loadAd(adRequest);*/
		
		Button bmapa = (Button) findViewById(R.id.buttonmapa);
        Button blista = (Button) findViewById(R.id.buttonLista);
        //Button bdatos = (Button) findViewById(R.id.buttondatos);
        //Button bt = (Button) findViewById(R.id.buttonBluetooth);
        Button bruta = (Button) findViewById(R.id.buttonRuta);
        Button bserv = (Button) findViewById(R.id.buttonservicios);
        //Button breg = (Button) findViewById(R.id.buttonRegistro);
        
        /***************Inicializamos la base de datos ***************/
        //introduceBBDD();	
        /*************************************************************/
        /*breg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrincipalActivity.this, LoginActivity.class);
				startActivity(i);
			}
		});*/
        
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
				Intent i = new Intent(PrincipalActivity.this, FavoritosActivity.class);
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
		
		SensorDB usdbh = new SensorDB(this);
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
				
				/*usdbh.intLugar(db, "BarEsquina", "--",  42.7147, -6.8994, "Bar");
				usdbh.intLugar(db, "100monta", "--",  42.4883, -0.2197, "Bar");
				usdbh.intLugar(db, "sureÃ±a", "--",  38.4449, -1.4501, "Bar");
				usdbh.intLugar(db, "Prueba1", "--",  38.5137, -5.8886, "Bar");
				usdbh.intLugar(db, "Restaurante", "--", 41.1864, -3.5706, "Bar");*/

		        /***************************************************/
		    }
		    fila.close();
		}
		db.close();
	}
}