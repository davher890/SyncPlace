package com.syncplace;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SensorDB extends SQLiteOpenHelper{
	
	
	/*Tabla de Lugares */
	String sqlCreate1 = "CREATE TABLE Lugar (" +
												"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
												"name TEXT," +
												"description TEXT," +
												"lattitude DOUBLE, " +
												"longitude DOUBLE, " +
												"type TEXT, " +
												"phone TEXT)";	
	
	public SensorDB(Context context) {
		super(context, "DBSensor", null, 2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(sqlCreate1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		//Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Lugar");
 
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate1);
	}
	
	public Lugar buscaLugar(SQLiteDatabase db, int id){

		Lugar s = null;
		String arg[]={String.valueOf(id)};
		String sql = "SELECT * FROM Lugar WHERE _id = ?";
	    Cursor fila = db.rawQuery(sql, arg);
	    
	    //Nos aseguramos de que existe al menos un registro
	    if (fila.moveToFirst()) {
	    	s = new Lugar(id,
	    				  fila.getString(1), 
	    				  fila.getString(2), 
	    				  Double.valueOf(fila.getString(3)).doubleValue(), 
	    				  Double.valueOf(fila.getString(4)).doubleValue(), 
	    				  fila.getString(5),
	    				  fila.getString(6));
	    }
	    
	    return s;	
	}
	
	public void intLugar (SQLiteDatabase db, Lugar l){
				
		String sql = "SELECT * FROM Lugar WHERE _id = ?";
		String arg[]={String.valueOf(l.getId())};
		Cursor fila = db.rawQuery(sql, arg);
		
		//Si no existe el registro
		if (!fila.moveToFirst()) {	
			ContentValues registro=new ContentValues();
		    registro.put("name", l.getNombre());
		    registro.put("description", l.getDescripcion());
		    registro.put("lattitude", l.getLatitud());
		    registro.put("longitude", l.getLongitud());
		    registro.put("type", l.getTipo());
		    registro.put("phone", l.getPhone());
		    if (db.insert("Lugar", null, registro) == -1){
		    	Log.e("BBDD","Error al insertar nuevo");
		    }	
		}
		else{
			//Update Registro
			ContentValues registro = new ContentValues();
			registro.put("name", l.getNombre());
		    registro.put("description", l.getDescripcion());
		    registro.put("lattitude", l.getLatitud());
		    registro.put("longitude", l.getLongitud());
		    registro.put("type", l.getTipo());
		    registro.put("phone", l.getPhone());
			 
			//Actualizamos el registro en la base de datos
			db.update("Lugar", registro, "_id="+l.getId(), null);			
		}
	}
	
	public ArrayList<Lugar> getRutaLugar(SQLiteDatabase db){
		ArrayList<Lugar> s = new ArrayList<Lugar>();
		String arg[]={"ruta"};
		String sql = "SELECT * FROM Lugar WHERE type = ?";
	    Cursor fila = db.rawQuery(sql, arg);
	    
	    //Nos aseguramos de que existe al menos un registro
	    if (fila.moveToFirst()) {
	    	Lugar l = new Lugar(Integer.parseInt(fila.getString(0)),
	    				  fila.getString(1), 
	    				  fila.getString(2), 
	    				  Double.valueOf(fila.getString(3)).doubleValue(), 
	    				  Double.valueOf(fila.getString(4)).doubleValue(), 
	    				  fila.getString(5),
	    				  fila.getString(6));
	    	s.add(l);
	    }
	    return s;
	}

	public int buscaIdLugar(SQLiteDatabase db,String nombre){
		
		String arg[]={nombre};
		String sql = "";
	    sql = "SELECT _id FROM Lugar WHERE name = ?";
	    Cursor fila = db.rawQuery(sql, arg);
	    
	    //Nos aseguramos de que existe al menos un registro
	    if (fila.moveToFirst()) {
	    	do{
	    		int i =Integer.valueOf(fila.getString(0));
	    		return i;
	    	 }while(fila.moveToNext());	
	    }	    
	    return -1;	
	}

	public void deleteLugar(SQLiteDatabase db, int id) {
		// TODO Auto-generated method stub
		db.delete("Lugar", "_id="+String.valueOf(id), null);
	}	
}