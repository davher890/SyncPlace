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

import com.syncplace.activity.MapaLugaresActivity;
import com.syncplace.activity.NuevaRuta;

public class GetService extends AsyncTask<String, Context, String>{

	private ProgressDialog dialog;
	protected Context context = null;
	private String url;
	
	public GetService(String url, Context contexto){
		this.url = url;
		this.context = contexto;
	}
	
	protected String doInBackground(String...params) {
		// TODO Auto-generated method stub
		HttpClient httpClient = new DefaultHttpClient();
		String result = null;
        
        HttpGet del = new HttpGet(url);        
        del.setHeader("Accept", "application/json");
    	del.setHeader("Content-Type", "application/json");
    	del.setHeader("Accept-Charset", "utf-8");
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

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setTitle("SyncPlace");
		dialog.setMessage("Buscando...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
		dialog.show();
	}	
	
	protected void onPostExecute(String result) {
		dialog.cancel();
		if (context instanceof NuevaRuta){
			((NuevaRuta)context).LanzaMapaRuta(result);
		}
		else if (context instanceof MapaLugaresActivity){
			((MapaLugaresActivity)context).getLatLongFromAddress(result);
		}
		
	}
}
