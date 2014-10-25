package com.syncplace.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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
import android.widget.Button;
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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.maps.MapController;
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
	
	/****** Utilizado solo para mostrar mi posiciÃ³n ******/
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private LocationManager mLocationManager;
	Location myLocation = null;
	
	
	private LatLng myPos = null;
	/*****************************************************/
	
	Context contexto;
	
	boolean orig = false;
		
	EditText direccion;	
	LinearLayout searchPanel;
    Button searchButton;
    EditText searchText;
    public static final int SEARCH_ID = Menu.FIRST;
    MapController mc;
    Geocoder geoCoder;
    
    ArrayList<Lugar> listaGPaux; 
    ArrayList<Lugar> listaGP;
	
	final DecimalFormat decf = new DecimalFormat("###.####");
    private boolean isEditMode = false;
    
    //Parametros para oblener la lista de servicios 
    String servicio = null;
    double latitud = 9999;
    double longitud = 9999;
    
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
        
        searchPanel = (LinearLayout) findViewById(R.id.searchPanel);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchText = (EditText) findViewById(R.id.searchText);
        contexto = this;
	    
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
        		orig = false;
        		servicio = contenedor.getString("servicio");
        		latitud = contenedor.getDouble("latitud");
        		longitud = contenedor.getDouble("longitud");
        		//callYelp(latitud, longitud, origen, radius);
        		/*for (int i=0;i<listaGP.size();i++){
	        		LatLng pos = new LatLng(listaGP.get(i).getLatitud(),listaGP.get(i).getLongitud());
	        		
	        		else //Creo la ruta
	        			setMarker(pos, listaGP.get(i).getNombre(), listaGP.get(i).getDescripcion(),BitmapDescriptorFactory.fromResource(R.drawable.posicion));
	        	}*/
        	}
        }
        
        map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Lugar l = null;
				
				if (listaGP == null){
					SensorDB usdbh = new SensorDB(contexto, "DBSensor", null, 1);
					SQLiteDatabase db = usdbh.getWritableDatabase();
					
					l = usdbh.buscaLugar(db, Integer.parseInt(marker.getTitle()));
					if (l == null)
						return false;					
				}	
				else{				
					for (int i=0; i< listaGP.size();i++){
						if (String.valueOf(listaGP.get(i).getId()).equals(marker.getTitle())){
							l = listaGP.get(i);
						}
					}
				}
				LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.dialoglugar, null);
		    	
				EditText etLat = (EditText)v.findViewById(R.id.editTextLat);
				etLat.setText(String.valueOf(l.getLatitud()));
				EditText etLon = (EditText)v.findViewById(R.id.editTextLon);
				etLon.setText(String.valueOf(l.getLongitud()));
				EditText etInfo = (EditText)v.findViewById(R.id.editTextInfo);
				etInfo.setText(l.getDescripcion());
				EditText etNombre = (EditText)v.findViewById(R.id.editTextNombre);
				etNombre.setText(l.getNombre());
				EditText etTipo = (EditText)v.findViewById(R.id. editTextTipo);
				etTipo.setText(l.getTipo());
				EditText editId = (EditText)v.findViewById(R.id.textIdGone);
				editId.setText(String.valueOf(l.getId()));
				
		        ErrorDialogFragment alert = new ErrorDialogFragment();
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
			    	
					EditText etLat = (EditText)v.findViewById(R.id.editTextLat);
					etLat.setText(String.valueOf(point.latitude));
					EditText etLon = (EditText)v.findViewById(R.id.editTextLon);
					etLon.setText(String.valueOf(point.longitude));
					
			        ErrorDialogFragment alert = new ErrorDialogFragment();
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
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String searchFor = searchText.getText().toString();
                
                geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

                try {
                    List<Address> addresses =
                           geoCoder.getFromLocationName(searchFor, 5);
                    if (addresses.size() > 0) {
                        
                    	map.clear();
                    	LatLng Pos = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
                        map.addMarker(new MarkerOptions().position(Pos)
                        								  .title(searchFor)
                        								  .icon(BitmapDescriptorFactory.fromResource(R.drawable.tick)));
                	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(Pos, 15));
                	    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }            
        });        
	}
	
	public boolean isEditMode(){
		return this.isEditMode;
	}
	
	public GoogleMap getMapView(){
		return this.map;
	}
	
	public void dibuja_lugares(){
		
		SensorDB usdbh = new SensorDB(this, "DBSensor", null, 1);
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
	    							fila.getString(5));
	    	
	    		LatLng pos = new LatLng(l.getLatitud(),l.getLongitud());

	    		setMarker(pos, l.getId(), null);
	    			    		
	    	} while(fila.moveToNext());	
	    }
	    else {
	    	System.out.println("No existen lugares en la base de datos");    	
	    }
    	//Cerramos la base de datos
	    db.close();
	    fila.close();
	} 
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	    // Decide what to do based on the original request code
	    switch (requestCode) {

	        case CONNECTION_FAILURE_RESOLUTION_REQUEST:
	            switch (resultCode) {
	                case Activity.RESULT_OK:
	                    mLocationManager.connect();
	                    break;
	            }
            default:
            	centrarCamara(myPos);
            	dibuja_lugares();
            	break;
	    }
	}*/
	
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

	private void centrarCamara(LatLng posicion){		
		// Move the camera instantly to Sydney with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomIn());
		// Zoom out to zoom level 10, animating with a duration of 2 seconds.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);		
	}
	
	private void setMarker(LatLng position, int id, BitmapDescriptor icon) {
		// Agregamos marcadores para indicar sitios de interéses.
		if (icon == null)
			icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
		map.addMarker(new MarkerOptions().title(String.valueOf(id)).position(position).icon(icon));
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

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	/*@Override
	public void onConnected(Bundle dataBundle) {
	    // Display the connection status
	    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	    Location location = mLocationManager.getLastLocation();
	    if (location != null){
		    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
		    map.animateCamera(cameraUpdate);
	    }
	}*/

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	/*@Override
	public void onDisconnected() {
	    // Display the connection status
	    Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}*/

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	/*@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {*/
	    /*
	     * Google Play services can resolve some errors it detects.
	     * If the error has a resolution, try sending an Intent to
	     * start a Google Play services activity that can resolve
	     * error.
	     */
	    /*if (connectionResult.hasResolution()) {
	        try {
	            // Start an Activity that tries to resolve the error
	            connectionResult.startResolutionForResult(
	                    this,
	                    CONNECTION_FAILURE_RESOLUTION_REQUEST);*/
	            /*
	            * Thrown if Google Play services canceled the original
	            * PendingIntent
	            */
	       /* } catch (IntentSender.SendIntentException e) {
	            // Log the error
	            e.printStackTrace();
	        }
	    } else {
	       Toast.makeText(getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
	    }
	}*/
	
	private float calculateZoomLevel_to_Radius() {
		VisibleRegion vr = map.getProjection().getVisibleRegion();
		double left = vr.latLngBounds.southwest.longitude;
		double top = vr.latLngBounds.northeast.latitude;
		double right = vr.latLngBounds.northeast.longitude;
		double bottom = vr.latLngBounds.southwest.latitude;
		
		Location middleLeftCornerLocation = new Location("middleLeftcorner");//(center's latitude,vr.latLngBounds.southwest.longitude)
		middleLeftCornerLocation.setLatitude(vr.latLngBounds.getCenter().latitude);
		middleLeftCornerLocation.setLongitude(left);
		
		Location center=new Location("center");
		center.setLatitude( vr.latLngBounds.getCenter().latitude);
		center.setLongitude( vr.latLngBounds.getCenter().longitude);
		return center.distanceTo(middleLeftCornerLocation);//calculate distane between middleLeftcorner and center 
	}
	
	private ArrayList<Lugar> callYelp(double latitud, double longitud, String servicio, float radius) {
		
		ArrayList<Lugar> listagp = null;
		
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
	            listagp = new ArrayList<Lugar>();
	            
	            for (int i=0;i<businesses.length();i++){            	
	            	JSONObject jsonObject = businesses.getJSONObject(i);
	            	
	            	JSONObject location = jsonObject.getJSONObject("location");
	            	JSONObject coordinate = location.getJSONObject("coordinate");
	            	
	            		            	
	            	String nombre = jsonObject.getString("name");           	
	            	
	            	String desc = "Dirección: "+location.getString("address")+
	            				  "\nCiudad: "+location.getString("city")+
	            				  //"\nPais: "+location.getString("country")+
	            				  "\nDistancia: "+jsonObject.getString("distance")+
	            				  "\nUrl: "+jsonObject.getString("url")+
	            				  "\nUrl móvil: "+jsonObject.getString("mobile_url")+
	            				  "\nCerrado: "+jsonObject.getString("is_closed")/*+
	            				  "\nTeléfono: "+jsonObject.getString("phone")*/;
	            	Double lat	= coordinate.getDouble("latitude");
	            	Double lon = coordinate.getDouble("longitude");
		            //Introducimos punto de origen
	                Lugar gp = new Lugar(0, nombre, desc, lat, lon, servicio);
	                listagp.add(gp);
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
        	for (int i=0;i<listaGP.size();i++){
        		LatLng posi = new LatLng(listaGP.get(i).getLatitud(),listaGP.get(i).getLongitud());
        		setMarker(posi, listaGP.get(i).getId(), BitmapDescriptorFactory.fromResource(R.drawable.tick));
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
        centrarCamara(latLng);
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