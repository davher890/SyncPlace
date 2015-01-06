package com.syncplace.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.QueryStringSigningStrategy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.syncplace.ErrorDialogFragment;
import com.syncplace.Lugar;
import com.syncplace.SensorDB;
import com.syncplace.v2.R;


public class MapaLugaresActivity extends FragmentActivity implements LocationListener, OnCameraChangeListener{
		
	public final String CONSUMER_KEY = "g8e8XleY3fOygTjFaCHsMA"; 
    public final String CONSUMER_SECRET = "HoqxWudi4mdqOR4-skgt9N_XuBs"; 
    public final String TOKEN = "-jmUm2NVi1feTQsd_9gyoCZxCvuauwc8"; 
    public final String TOKEN_SECRET = "s_z5V6etkD3M8soELkw-vJ_N_Ng"; 
    public final String YWSID = "iXUUomsyqBkdXaypz7539A"; 
    public final String ENCODING_SCHEME = "UTF-8";
    
    public static final int SEARCH_ID = Menu.FIRST;
	
	/****** Utilizado solo para mostrar mi posicion ******/
	private GoogleMap map;
	private LocationManager mLocationManager;
	Location myLocation = null;	
	/*****************************************************/
	
	private Context contexto;	
	boolean orig = false;
			
	private LinearLayout searchPanel;
    //private Button searchButton;
    private EditText searchText;
     
    private HashMap<String, Lugar> listaGP;
    private HashMap<String, Marker> mapMarkers;
	
    private boolean isEditMode = false;
    
    //Parametros para oblener la lista de servicios 
    String servicio = null;
    double latitud = -1;
    double longitud = -1;
    float radius = 0;
    
    float currentZoom = -1;    
	    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        isGooglePlayServicesAvailable();
              
        //FrameLayout superior Principal
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.setVisibility(View.VISIBLE);
        
        //searchPanel = (LinearLayout) findViewById(R.id.searchPanel);
        //searchButton = (Button) findViewById(R.id.searchButton);
        //searchText = (EditText) findViewById(R.id.searchText);
        contexto = this;
	    
        mapMarkers = new HashMap<String, Marker>();
        dibuja_lugares();
	            
        /****Pintamos sobre el mapa añadiendo una capa con el dibujo****/        
        //Si vengo de servicios o de nuevaruta
        Bundle contenedor = getIntent().getExtras();
        if (contenedor != null){
        	String origen = contenedor.getString("origen");
        	if (origen != null){
        		//Seleccionamos origen
        		if (origen.equals("si"))
        			orig = true;
        	}
        	else{
        		//Marcamos lugares en el mapa
        		if (contenedor.getString("clase").equals("servicios")){
        			orig = false;
	        		servicio = contenedor.getString("servicio");
	        		latitud = contenedor.getDouble("latitud");
	        		longitud = contenedor.getDouble("longitud");
	        		
	        		if (latitud == 0.0 || longitud == 0.0){
	        			latitud = myLocation.getLatitude();
	        			longitud = myLocation.getLongitude();
	        		}
        		}
        		else if (contenedor.getString("clase").equals("nuevaruta")){
        			
        			Bundle b = this.getIntent().getExtras();
        			ArrayList<Lugar> gpList = b.getParcelableArrayList("listagp");        	        
        			int size = gpList.size();
        			PolylineOptions polilinea = new PolylineOptions().color(Color.BLUE)
        				    .width(5)
        				    .visible(true)
        				    .zIndex(30);;
					for(int i=0;i<size;i++){
						Lugar l = gpList.get(i);
						polilinea.add(new LatLng(l.getLatitud(), l.getLongitud()));
					}
					map.addPolyline(polilinea);
        		}
        	}
        }
        
        map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Lugar l = null;
				
				if (listaGP == null){
					SensorDB usdbh = new SensorDB(contexto);
					SQLiteDatabase db = usdbh.getWritableDatabase();
					
					l = usdbh.buscaLugar(db, Integer.parseInt(marker.getTitle()));					
				}	
				else{					
					l = listaGP.get(marker.getTitle());
					
				}
				
				if (l == null){
					return false;
				}
				LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.dialoglugar, null);
				
		        ErrorDialogFragment alert = new ErrorDialogFragment(l);
		        AlertDialog createDialogLugar = alert.createDialogLugar(contexto, v, "");
		        createDialogLugar.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		        createDialogLugar.show();
				
		        return true;
			}
		});
        
        map.setOnMapClickListener(new OnMapClickListener() {
            public void onMapClick(LatLng point) {
                
                Intent i;
                if (orig == true){
	    			i = new Intent(contexto, ServiciosActivity.class);		    		
		            Bundle contenedor=new Bundle();
		            contenedor.putDouble("latitud", point.latitude);
		            contenedor.putDouble("longitud", point.longitude);
		            i.putExtras(contenedor);
					i.putExtra("creareditar",true);//crear
					
					((Activity) contexto).setResult(Activity.RESULT_OK, i );
					((Activity) contexto).finish();
                }
	    		else{
	    			LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View v = inflater.inflate(R.layout.dialoglugar, null);
										
			        ErrorDialogFragment alert = new ErrorDialogFragment(new Lugar (point.latitude, point.longitude));
			        AlertDialog createDialogLugar = alert.createDialogLugar(contexto, v,"¿Desea almacenar este lugar?");
			        createDialogLugar.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							dibuja_lugares();							
						}
					});
			        
					createDialogLugar.show();
	    		}
            }
        });

        /***************************************************************/
        /*searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String searchFor = searchText.getText().toString();
                
                String url = "http://maps.google.com/maps/api/geocode/json?address=" + searchFor + "&sensor=false";
                                
                GetService gs = new GetService(url, contexto);
                gs.execute();
            }
        });*/
	}
	
	public boolean isEditMode(){
		return this.isEditMode;
	}
	
	public GoogleMap getMapView(){
		return this.map;
	}
	
	public void getLatLongFromAddress(String response) {
		        
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(response);

            double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

            double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");
            
            LatLng position = new LatLng(lat, lng);
			setMarker(position, searchText.getText().toString(), null);
            centrarCamara(position, 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
	
	public void dibuja_lugares(){
		
		SensorDB usdbh = new SensorDB(this);
		SQLiteDatabase db = usdbh.getWritableDatabase();
	    
		String sql = "SELECT * FROM Lugar ";
	    Cursor fila = db.rawQuery(sql, null);
	    
	    //Nos aseguramos de que existe al menos un registro
	    if (fila.moveToFirst()) {
	    	//Recorremos el cursor hasta que no haya mas registros
	    	do {
	    		Lugar l = new Lugar(Integer.parseInt(fila.getString(0)), 
	    							fila.getString(1), 
	    							fila.getString(2), 
	    							Double.valueOf(fila.getString(3)).doubleValue(), 
	    							Double.valueOf(fila.getString(4)).doubleValue(), 
	    							fila.getString(5),
	    							fila.getString(6));
	    	
	    		LatLng pos = new LatLng(l.getLatitud(),l.getLongitud());

	    		setMarker(pos, String.valueOf(l.getId()), null);
	    			    		
	    	} while(fila.moveToNext());	
	    }
	    else {
	    	System.out.println("No existen lugares en la base de datos");    	
	    }
    	//Cerramos la base de datos
	    db.close();
	    fila.close();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, SEARCH_ID, 0, "Search");
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case SEARCH_ID:
                searchPanel.setVisibility(View.VISIBLE);
                break;
        }

        return result;
    }
    
    public void hideSearchPanel() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
             searchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        searchPanel.setVisibility(View.INVISIBLE);
    }

	private void centrarCamara(LatLng posicion, int zoom){		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 7));
		map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 500, null);		
	}
	
	private void setMarker(LatLng position, String id, BitmapDescriptor icon) {
		// Agregamos marcadores para indicar sitios de interéses.
		if (icon == null)
			icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
		
		Marker marker = map.addMarker(new MarkerOptions().title(id).position(position).icon(icon));
		mapMarkers.put(id, marker);
	}
	public void removeMarker(int id){
		Marker marker = mapMarkers.get(String.valueOf(id));
		if (marker!=null){
			marker.remove();
		}
	}
	
	private void isGooglePlayServicesAvailable() {
		
		// Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
 
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
 
        }else { // Google Play Services are available
 
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
 
            // Getting GoogleMap object from the fragment
            map = fm.getMap();
            map.setOnCameraChangeListener(this);
 
            // Enabling MyLocation Layer of Google Map
            map.setMyLocationEnabled(true);
 
            // Getting LocationManager object from System Service LOCATION_SERVICE
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
 
            // Getting the name of the best provider
            String provider = mLocationManager.getBestProvider(criteria, true);
            
            // getting GPS status
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
            	if (isNetworkEnabled) {
                    if (mLocationManager != null) {
                    	myLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (isGPSEnabled) {
                    if (myLocation == null) {
                        if (mLocationManager != null) {
                        	myLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }
 
            if(myLocation!=null){
                onLocationChanged(myLocation);
            }
            mLocationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
	}
	
	private float calculateZoomLevel_to_Radius() {
		VisibleRegion vr = map.getProjection().getVisibleRegion();
		double left = vr.latLngBounds.southwest.longitude;
		
		Location middleLeftCornerLocation = new Location("middleLeftcorner");//(center's latitude,vr.latLngBounds.southwest.longitude)
		middleLeftCornerLocation.setLatitude(vr.latLngBounds.getCenter().latitude);
		middleLeftCornerLocation.setLongitude(left);
		
		Location center=new Location("center");
		center.setLatitude( vr.latLngBounds.getCenter().latitude);
		center.setLongitude( vr.latLngBounds.getCenter().longitude);
		return center.distanceTo(middleLeftCornerLocation);//calculate distane between middleLeftcorner and center 
	}
	
	private HashMap<String, Lugar> callYelp(double latitud, double longitud, String servicio, float radius) {
		
		HashMap<String, Lugar> listagp = null;
		
		String limit = "20"; 
		try { 
			String query = String.format(
		    "http://api.yelp.com/v2/search?ll=%s&category_filter=%s&radius_filter=%s&limit=%s", 
		    URLEncoder.encode(latitud + "," + longitud, ENCODING_SCHEME), 
		    URLEncoder.encode(servicio, ENCODING_SCHEME), 
		    URLEncoder.encode(String.valueOf(radius), ENCODING_SCHEME), 
		    URLEncoder.encode(limit, ENCODING_SCHEME));
			
		    OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET); 
		    consumer.setMessageSigner(new HmacSha1MessageSigner());
		    consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
		    consumer.setSendEmptyTokens(true); 
		    consumer.setSigningStrategy(new QueryStringSigningStrategy());
		    
		    String signedQuery = consumer.sign(query);
		    HttpGet request = new HttpGet(signedQuery); 
		    HttpClient httpClient = new DefaultHttpClient(); 
		    HttpResponse response = (HttpResponse) httpClient.execute(request); 

		    HttpEntity entity = ((org.apache.http.HttpResponse) response).getEntity(); 
		    String result = EntityUtils.toString(entity);		    
		    
		    /**************************************************************************/
		    JSONObject respJSON = null;
			try {
				respJSON = new JSONObject(result);
			              
	            JSONArray businesses = respJSON.getJSONArray("businesses");
	            listagp = new HashMap<String, Lugar>();
	            
	            for (int i=0;i<businesses.length();i++){            	
	            	JSONObject jsonObject = null;
	            	JSONObject location = null;
	            	JSONObject coordinate = null;
	            	String nombre  = null;
	            	String id = null;
	            	double lat = -1.0;
	            	double lon = -1.0;
	            	
	            	if (!businesses.isNull(i)){
	            		jsonObject = businesses.getJSONObject(i);
	            	}
	            	
	            	if (!jsonObject.isNull("location")){
	            		location = jsonObject.getJSONObject("location");
	            	}
	            	
	            	if (!location.isNull("coordinate")){
	            		coordinate = location.getJSONObject("coordinate");
	            		if (!coordinate.isNull("latitude")){
		            		lat	= coordinate.getDouble("latitude");
		            	}		            	
		            	if (!coordinate.isNull("longitude")){
		            		lon = coordinate.getDouble("longitude");
		            	}
	            	}	            	
	            		            	
	            	if (!jsonObject.isNull("name")){
	            		nombre = jsonObject.getString("name");
	            	}
	            	
	            	if (!jsonObject.isNull("id")){
	            		id = jsonObject.getString("id");
	            	}
	            	
	            	StringBuilder desc = new StringBuilder();
	            	if (!location.isNull("address")){
	            		desc.append("Dirección: "+location.getString("address"));
	            	}
	            	if (!location.isNull("city")){
	            		desc.append("\nCiudad: "+location.getString("city"));
	            	}
	            	if (!location.isNull("country_code")){
	            		desc.append("\nPais: "+location.getString("country_code"));
	            	}
	            	if (!jsonObject.isNull("distance")){
	            		desc.append("\nDistancia: "+jsonObject.getString("distance"));
	            	}
	            	if (!jsonObject.isNull("url")){
	            		desc.append("\nUrl: "+jsonObject.getString("url"));
	            	}
	            	if (!jsonObject.isNull("mobile_url")){
	            		desc.append("\nUrl móvil: "+jsonObject.getString("mobile_url"));
	            	}
	            	if (!jsonObject.isNull("is_closed")){
	            		desc.append("\nCerrado: "+jsonObject.getString("is_closed"));
	            	}
	            	String phone = null;
	            	if (!jsonObject.isNull("phone")){
	            		phone = jsonObject.getString("phone");
	            	}
	            	
		            //Introducimos punto de origen
	                Lugar gp = new Lugar(i, nombre, desc.toString(), lat, lon, servicio, phone);
	                listagp.put(id, gp);
	            }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return listagp;
		    
		} catch (UnsupportedEncodingException e) { 
		        // TODO Auto-generated catch block 
		        e.printStackTrace(); 
		} catch (MalformedURLException e) { 
		        // TODO Auto-generated catch block 
		        e.printStackTrace(); 
		} catch (IOException e) { 
		        // TODO Auto-generated catch block 
		        e.printStackTrace(); 
		} catch (OAuthMessageSignerException e) { 
		        // TODO Auto-generated catch block 
		        e.printStackTrace(); 
		} catch (OAuthExpectationFailedException e) { 
		        // TODO Auto-generated catch block 
		        e.printStackTrace(); 
		} catch (OAuthCommunicationException e) { 
		        // TODO Auto-generated catch block 
		        e.printStackTrace(); 
		}
		return listagp;
	}
	
	@Override
	public void onCameraChange(CameraPosition pos) {		
        if (pos.zoom != currentZoom && servicio != null){
            currentZoom = pos.zoom;
            
            float radio = calculateZoomLevel_to_Radius();                    
            
            if (latitud == 9999 && longitud == 9999){
    	        listaGP = callYelp(latitud, longitud, servicio, radio);
            }
            else{
    	        listaGP = callYelp(myLocation.getLatitude(), myLocation.getLongitude(), servicio, radio);
            }
            
            map.clear();
        	Iterator it = listaGP.entrySet().iterator();
        	while (it.hasNext()) {
        		Map.Entry<String, Lugar> e = (Entry<String, Lugar>) it.next();
        		
        		LatLng posi = new LatLng(((Lugar)e.getValue()).getLatitud(),((Lugar)e.getValue()).getLongitud());
        		setMarker(posi, (e.getKey()).toString(), BitmapDescriptorFactory.fromResource(R.drawable.tick));
        	}            
        }
	}

	@Override
	public void onLocationChanged(Location location) {
		
		myLocation = location;
		
        // Getting latitude of the current location
        double latitude = location.getLatitude(); 
        // Getting longitude of the current location
        double longitude = location.getLongitude();
 
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        centrarCamara(latLng, 17);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}
}