package com.syncplace;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	    				  fila.getString(5));
	    }
	    
	    return s;	
	}
	
	public void intLugar (SQLiteDatabase db, Lugar l){
				
		String sql = "SELECT * FROM Lugar WHERE _id = ?";
		String arg[]={String.valueOf(l.getId())};
		Cursor fila = db.rawQuery(sql, arg);
		boolean flag = false;
		
		//Si no existe el registro
		if (!fila.moveToFirst()) {	
			ContentValues registro=new ContentValues();
		    registro.put("nombre", l.getNombre());
		    registro.put("descripcion", l.getDescripcion());
		    registro.put("lattitude", l.getLatitud());
		    registro.put("longitude", l.getLongitud());
		    registro.put("tipo", l.getTipo());
		    if (db.insert("Lugar", null, registro) == -1){
		    	Log.e("BBDD","Error al insertar nuevo");
		    }	
		}
		else{
			//Update Registro
			ContentValues registro = new ContentValues();
			registro.put("nombre", l.getNombre());
		    registro.put("descripcion", l.getDescripcion());
		    registro.put("lattitude", l.getLatitud());
		    registro.put("longitude", l.getLongitud());
		    registro.put("tipo", l.getTipo());
			 
			//Actualizamos el registro en la base de datos
			db.update("Lugar", registro, "_id="+l.getId(), null);			
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