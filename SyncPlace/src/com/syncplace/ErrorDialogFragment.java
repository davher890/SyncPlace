package com.syncplace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

//Define a DialogFragment that displays the error dialog
public class ErrorDialogFragment extends DialogFragment {

    // Global field to contain the error dialog
    private Dialog mDialog;
    private Context contexto;

    // Default constructor. Sets the dialog field to null
    public ErrorDialogFragment() {
        super();
        mDialog = null;
    }

    // Set the dialog to display
    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }

    // Return a Dialog to the DialogFragment.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog;
    }
    
    public AlertDialog createDialogLugar(Context contexto, final View v, String mensaje){
    	this.contexto = contexto;
    	AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
		 
        builder.setMessage(mensaje).setTitle("Información")
        	   .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
        		   public void onClick(DialogInterface dialog, int id) {        			   
        			   EditText etLat = (EditText)v.findViewById(R.id.editTextLat);
        			   EditText etLon = (EditText)v.findViewById(R.id.editTextLon);
	       			   EditText etInfo = (EditText)v.findViewById(R.id.editTextInfo);
	       			   EditText etNombre = (EditText)v.findViewById(R.id.editTextNombre);
	       			   EditText etTipo = (EditText)v.findViewById(R.id.editTextTipo);
	       			   
	       			   almacenar(new Lugar(etNombre.getText().toString(), etInfo.getText().toString(), 
	       					   Double.valueOf(etLat.getText().toString()), Double.valueOf(etLon.getText().toString()), 
	       					   etTipo.getText().toString()));
        			   dialog.cancel();
                   }

               })
               .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        		   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               })
               .setView(v);
        return builder.create();
    }
    
    private void almacenar(Lugar l) {
		SensorDB usdbh = new SensorDB(contexto, "DBSensor", null, 1);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		usdbh.intLugar(db, l.getNombre(), l.getDescripcion(), l.getLatitud(), l.getLongitud(), l.getTipo());
		db.close();
	}
}