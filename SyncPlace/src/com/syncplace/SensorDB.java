package com.syncplace;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SensorDB extends SQLiteOpenHelper{
	
	
	/*Tabla de Lugares */
	String sqlCreate1 = "CREATE TABLE Lugar (" +
												"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
												"nombre TEXT," +
												"descripcion TEXT," +
												"lattitude DOUBLE, " +
												"longitude DOUBLE, " +
												"tipo TEXT)";	
	
	public SensorDB(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
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
		String arg[]={""+id};
		String sql = "";
	    sql = "SELECT nombre, descripcion, lattitude, longitude, tipo " +
	    	  "FROM Lugar WHERE _id = ?";
	    Cursor fila = db.rawQuery(sql, arg);
	    
	    //Nos aseguramos de que existe al menos un registro
	    if (fila.moveToFirst()) {
	    	s = new Lugar(fila.getString(0), 
	    				  fila.getString(1), 
	    				  Double.valueOf(fila.getString(2)).doubleValue(), 
	    				  Double.valueOf(fila.getString(3)).doubleValue(), 
	    				  fila.getString(4));
	    }
	    
	    return s;	
	}
	
	public Lugar buscaLugar(SQLiteDatabase db, String nombre){

		Lugar s = null;
		String arg[]={nombre};
		String sql = "";
	    sql = "SELECT descripcion, lattitude, longitude, tipo " +
	    	  "FROM Lugar WHERE nombre = ?";
	    Cursor fila = db.rawQuery(sql, arg);
	    
	    //Nos aseguramos de que existe al menos un registro
	    if (fila.moveToFirst()) {
	    	do{
	    		s = new Lugar(nombre, 
	    					  fila.getString(0), 
	    					  Double.valueOf(fila.getString(1)).doubleValue(), 
	    					  Double.valueOf(fila.getString(2)).doubleValue(), 
	    					  fila.getString(3));       		
	    	}while(fila.moveToNext());	
	    }
	    
	    return s;	
	}
	
	public void intLugar (SQLiteDatabase db, String nombre, String descripcion, double lat, double lon, String tipo){
				
		String sql = "SELECT nombre, descripcion, lattitude, longitude, tipo FROM Lugar";
		Cursor fila = db.rawQuery(sql, null);
		boolean flag = false;
		
		if (fila.moveToFirst()) {
	    	do{
	    		if (fila.getString(0).equals(nombre) &&
	    			fila.getString(1).equals(descripcion) &&
	    			Double.valueOf(fila.getString(2)).doubleValue() == lat &&
	    			Double.valueOf(fila.getString(3)).doubleValue() == lon &&
	    			fila.getString(4).equals(tipo))
	    			
	    			flag = true;
	    		
	    	}while(fila.moveToNext());
	    }
		
		if (flag == false){		
			ContentValues registro=new ContentValues();
		    registro.put("nombre", nombre);
		    registro.put("descripcion",descripcion);
		    registro.put("lattitude",lat);
		    registro.put("longitude",lon);
		    registro.put("tipo", tipo);
		    if (db.insert("Lugar", null, registro) == -1){
		    	System.out.println("Error al insertar nuevo");
		    }	
		}
	}

	public int buscaIdLugar(SQLiteDatabase db,String nombre){
		
		String arg[]={nombre};
		String sql = "";
	    sql = "SELECT _id FROM Lugar WHERE nombre = ?";
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
}