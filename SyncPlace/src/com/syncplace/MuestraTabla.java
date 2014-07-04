package com.syncplace;

import com.syncplace.v2.R;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MuestraTabla extends Activity {
	
	
	TableLayout data_table;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basedatos);
		data_table=(TableLayout)findViewById(R.id.datatable);
		fillDataTable();    
	}

	
	void fillDataTable(){

		TableRow row;
		TextView t1, t2, t3, t4, t5;
		
		SensorDB usdbh = new SensorDB(this, "DBSensor", null, 1);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		
		String sql = "SELECT nombre, descripcion, lattitude, longitude, tipo FROM Lugar";
		Cursor fila = db.rawQuery(sql, null);
		
		//int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float) 1, getResources().getDisplayMetrics());
		    
		//Nos aseguramos de que existe al menos un registro
		if (fila.moveToFirst()) {
		//Recorremos el cursor hasta que no haya mas registros
			do {
			    row = new TableRow(this);

				t1 = new TextView(this);
				t2 = new TextView(this);
				t3 = new TextView(this);
				t4 = new TextView(this);
				t5 = new TextView(this);

				t1.setText(fila.getString(0));
				t2.setText(fila.getString(1));
				t3.setText(fila.getString(2));
				t4.setText(fila.getString(3));
				t5.setText(fila.getString(4));				
				
				row.addView(t1);
				row.addView(t2);
				row.addView(t3);
				row.addView(t4);
				row.addView(t5);
				
				data_table.addView(row, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				
			} while(fila.moveToNext());
		}
		
		
	}



}
