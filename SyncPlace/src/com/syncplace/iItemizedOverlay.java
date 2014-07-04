package com.syncplace;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.syncplace.v2.R;

/*
 * Clase empleada para dibujar los puntos de los sensores a partir de
 * los datos contenidos en la base de datos.
 *  
 */
public class iItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	private Lugar l;

	public iItemizedOverlay(Context context, Drawable defaultMarker, Lugar l) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		this.l = l;
		GeoPoint punto = new GeoPoint((int)(l.getLatitud()*1E6), (int)(l.getLongitud()*1E6));
		OverlayItem item = new OverlayItem(punto, l.getNombre(), null);
		mOverlays.add(item);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		
		ArrayList<Lugar> listaGP = new ArrayList<Lugar>();
		listaGP.add(l);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        alert.createDialogLugar(context, v, "¿Desea almacenar este lugar?").show();        
        return false;
	}
}