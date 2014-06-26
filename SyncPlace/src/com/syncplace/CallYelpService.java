package com.syncplace;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.syncplace.activity.NuevaRuta;
import com.syncplace.activity.ServiciosActivity;

public class CallYelpService extends AsyncTask<String, Context, String>{

	private ProgressDialog dialog;
	protected ServiciosActivity ServiciosContext = null;
	protected NuevaRuta NuevaRutaContext = null;
	private String url;
	private Yelp yelp;
	private double latitud;
	private double longitud;
	private String busqueda;
	
	public CallYelpService(String url, ServiciosActivity contexto){
		this.url = url;
		this.ServiciosContext = contexto;
	}
	
	public CallYelpService(String url, NuevaRuta contexto){
		this.url = url;
		this.NuevaRutaContext = contexto;
	}
	
	protected String doInBackground(String...params) {
		// TODO Auto-generated method stub
		if (yelp != null){
			return yelp.search(busqueda, latitud, longitud);			
		}
		else{
			HttpClient httpClient = new DefaultHttpClient();
			String result = null;
	        
	        HttpGet del = new HttpGet(url);
	        
	        del.setHeader("content-type", "application/json");
	        try
	        {
				HttpResponse resp = httpClient.execute(del);
				result = EntityUtils.toString(resp.getEntity());
				Log.i("RET", result);
	        }
	        catch(Exception ex)
	        {
	                Log.e("YELP","Error recibiendo datos",ex);
	        }	        
	        return result;
		}        
	}	

	@Override
	protected void onPreExecute() {
		if (NuevaRutaContext == null)
			dialog = new ProgressDialog(ServiciosContext);
		else 
			dialog = new ProgressDialog(NuevaRutaContext);
		dialog.setTitle("SyncPlace");
		dialog.setMessage("Buscando...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
		dialog.show();
	}	
	
	protected void onPostExecute(String result) {
		dialog.cancel();
		/*if (NuevaRutaContext == null)
			ServiciosContext.LanzaMapaLugares(result);		
		else
			NuevaRutaContext.LanzaMapaRuta(result);*/
	}
}
