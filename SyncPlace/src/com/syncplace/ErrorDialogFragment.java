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

import com.syncplace.activity.MapaLugaresActivity;
import com.syncplace.v2.R;

//Define a DialogFragment that displays the error dialog
public class ErrorDialogFragment extends DialogFragment {

    // Global field to contain the error dialog
    private Dialog mDialog;
    private Context contexto;
    private Lugar lugar = null;
    
    private EditText etLat;
    private EditText etLon;
    private EditText etPhone;
    private EditText etInfo;	       			   
    private EditText etNombre;
    private EditText etTipo;
    private EditText etId;

    // Default constructor. Sets the dialog field to null
    public ErrorDialogFragment(Lugar l) {
        super();
        this.lugar = l;
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
    
    public AlertDialog createDialogLugar(final Context contexto, final View v, String mensaje){
    	this.contexto = contexto;
    	
    	etLat = (EditText)v.findViewById(R.id.editTextLat);
    	etLon = (EditText)v.findViewById(R.id.editTextLon);
    	etPhone = (EditText)v.findViewById(R.id.editTextPhone);
    	etInfo = (EditText)v.findViewById(R.id.editTextInfo);	       			   
    	etNombre = (EditText)v.findViewById(R.id.editTextNombre);
    	etTipo = (EditText)v.findViewById(R.id.editTextTipo);
    	etId = (EditText)v.findViewById(R.id.textIdGone);
   
    	etLat.setText(String.valueOf(lugar.getLatitud()));
    	etLon.setText(String.valueOf(lugar.getLongitud()));
    	etInfo.setText(lugar.getDescripcion());
    	etNombre.setText(lugar.getNombre());
    	etPhone.setText(lugar.getPhone());
    	etTipo.setText(lugar.getTipo());
    	etId.setText(String.valueOf(lugar.getId()));
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setMessage(mensaje).setTitle("Info")
        	   .setPositiveButton("Save", new DialogInterface.OnClickListener() {
        		   public void onClick(DialogInterface dialog, int id) {
        			   
        			   String idText = etId.getText().toString();
	       			   if (idText != null && !idText.equals("") ){
	       				   almacenar(new Lugar(Integer.parseInt(etId.getText().toString()), etNombre.getText().toString(), etInfo.getText().toString(),
	       						   Double.valueOf(etLat.getText().toString()), Double.valueOf(etLon.getText().toString()), 
	       						   etTipo.getText().toString(), etPhone.getText().toString()));
	       			   }
	       			   else{
		       			   almacenar(new Lugar(etNombre.getText().toString(), etInfo.getText().toString(), 
		       					   Double.valueOf(etLat.getText().toString()), Double.valueOf(etLon.getText().toString()), 
		       					   etTipo.getText().toString(), etPhone.getText().toString()));
	       			   }
        			   dialog.cancel();
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               })
               .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
        		   public void onClick(DialogInterface dialog, int id) {
        			   EditText etId = (EditText)v.findViewById(R.id.textIdGone);
        			   String idText = etId.getText().toString();
        			   if (idText != null && !idText.equals("") ){
	        			   borrar(Integer.parseInt(idText));
	        			   if (contexto instanceof MapaLugaresActivity){
	        				   ((MapaLugaresActivity)contexto).removeMarker(Integer.parseInt(idText));
	        			   }
        			   }
        			   dialog.cancel();
                  }})
               .setView(v);
        return builder.create();
    }
    
    private void almacenar(Lugar l) {
		SensorDB usdbh = new SensorDB(contexto);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		usdbh.intLugar(db, l);
		db.close();
	}
    
    private void borrar(int id){
    	SensorDB usdbh = new SensorDB(contexto);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		usdbh.deleteLugar(db, id);
		db.close();
    }
}