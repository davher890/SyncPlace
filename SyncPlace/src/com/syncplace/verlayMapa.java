package com.syncplace;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class verlayMapa extends Overlay {
	
    //MapView mapa;        
    Context context;
    
    private GeoPoint geopoint=null;
    
    private ListaPuntos listaGP=null;
    private Boolean b;
  
    boolean intlugar;
    
    //Array de puntos
    HashMap<GeoPoint, Integer> hashMap = new HashMap<GeoPoint, Integer>();

    /*public verlayMapa(MapView map, Context context, ListaPuntos listaGP, Boolean b){
    	this.mapa = map;
    	this.context = context;
    	this.listaGP = listaGP;
    	this.b = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
    	
    	final MapView mpv = mapView;
    	final MotionEvent evt = event;
    	    	
    	if (evt.getAction() == MotionEvent.ACTION_DOWN){
    		intlugar=true;
    		return false;
    	}
    	
    	if (evt.getAction() == MotionEvent.ACTION_MOVE){
    		intlugar=false;
    		return false;
    	}
    	
    	if (evt.getAction() == MotionEvent.ACTION_UP){
    		if (intlugar){    			
	    		geopoint = mpv.getProjection().fromPixels((int) evt.getX(),(int) evt.getY());
	    		Intent i;
	    		if (b == true)
	    			i = new Intent(context, Servicios.class);
	    		else
	    			i = new Intent(context, EditarLugarActivity.class);
	    		
	    		Lugar l = new Lugar("", "", geopoint.getLatitudeE6()/1E6, geopoint.getLongitudeE6()/1E6, "");
	    		ListaPuntos listaGP = new ListaPuntos();
	    		listaGP.add(l);
	    		
	            Bundle contenedor=new Bundle();
	            contenedor.putParcelable("gplist",listaGP);
	            i.putExtras(contenedor);
				i.putExtra("creareditar",true);//crear
				
				if (b == true){
					((Activity) context).setResult(Activity.RESULT_OK, i );
					((Activity) context).finish();
				}
				else
					context.startActivity(i);
				
				return true;	
    		}    		
    	}
    	return false;
    }
 
    /*
     * Este metodo se ejecuta cada vez que se toca la pantalla, ya se para arrastrar el mapa o pulsar sobre �l
     */
    /*
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow){
    	
    	if (listaGP == null || listaGP.size() == 0){
    		return;
    	}
    	
    	if (listaGP.size() > 0){
			// recogemos la proyeccion del mapa
			Projection projection = mapView.getProjection();
			//Point puntoInicio;
			
			//Definimos el pincel de dibujo del poligono
	        Paint p = new Paint();
	        //p.setColor(Color.GREEN); es equivalente a la siguiente sentencia
	        p.setARGB(100, 255, 0, 255);//(alpha=transparencia(0-255), r, g, b)
	        p.setStrokeWidth(9);//Tamaño del pincel
        	    		        
	        p.setStyle(Paint.Style.STROKE);
	        
		    // recorro el array de geoPoints para pintar la linea entre la 1 y 2; 2 y 3; 3 y 4, etc.. n y 1
		    for(int j=0; j<listaGP.size(); j++){
		    	Lugar l1 = (Lugar)listaGP.get(j);
		        GeoPoint g1tmp = new GeoPoint((int)(l1.getLatitud()*1E6), (int)(l1.getLongitud()*1E6));
		      	Point puntoFin = new Point();
		        projection.toPixels(g1tmp, puntoFin);
		        //canvas.drawLine(puntoInicio.x, puntoInicio.y, puntoFin.x, puntoFin.y, p);
		        canvas.drawPoint(puntoFin.x, puntoFin.y, p);
		        //puntoInicio = puntoFin;
		    }
		}  
    }*/
}
