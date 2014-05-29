package com.syncplace.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.syncplace.R;
import com.syncplace.SensorDB;

public class BTChatActivity extends Activity{
		
	Context context;
	
	// Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public static final int TAMANO_ENVIO = 1000000; //Bytes enviados
    public static int maxtramas=0;
    public static int tamanoreal=0;
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 4;
    public static final int MESSAGE_READ = 5;
    public static final int MESSAGE_WRITE = 6;
    public static final int MESSAGE_DEVICE_NAME = 7;
    public static final int MESSAGE_TOAST = 8;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

	// Intent request codes
    //private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 10;
    
    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChat";
    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    
    // Member fields
    //private final BluetoothAdapter mAdapter;
    //private final Handler mHandler;
    private static AcceptThread mAcceptThread;
    private static ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    
	private Button conecta;
	private Button desconecta;
	private Button busca;
	private Button envia;
	private Button paired;
	
	private ListView lista;
	ArrayAdapter<String> adaptador;
	ArrayList<String> datos;
	
	int offset=1;
	
	TextView textnombre;
	TextView textdesc;
	TextView textlatitud;
	TextView textlongitud;
	TextView texttipo;

	//private ArrayAdapter<String> mNewDevicesArrayAdapter;
	HashMap<String, String> foundDevices = new HashMap<String, String>();
	static BluetoothAdapter mBluetoothAdapter;
	
	String mac;
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        context = this;
        
        conecta 	= 	(Button)findViewById(R.id.buttonconecta);
        busca 		=   (Button)findViewById(R.id.buttonbusca);
        desconecta  = 	(Button)findViewById(R.id.buttondesconectar);
        envia 		= 	(Button)findViewById(R.id.buttonenvia);
        paired 		= 	(Button)findViewById(R.id.buttonpaired);   
        lista 		= 	(ListView)findViewById(R.id.listconectados);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth No disponible", Toast.LENGTH_SHORT);
			toast.show();
		}
		else{
			Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth disponible", Toast.LENGTH_SHORT);
			toast.show();
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
			Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth desactivado", Toast.LENGTH_SHORT);
			toast.show();
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else {
			Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth activado", Toast.LENGTH_SHORT);
			toast.show();
		}
		
		/*//Ponemos el dispositivo visible indefinidamente
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
		startActivity(discoverableIntent);*/
		
		// Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
        	System.out.println("mConnectThread != null");
        	mConnectThread.cancel(); 
        	mConnectThread = null;	
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
        	System.out.println("mConnectedThread != null");
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
        	System.out.println("mAcceptThread == null");
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }               
        
        conecta.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (mBluetoothAdapter == null) {
					Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth No disponible", Toast.LENGTH_SHORT);
					toast.show();
				}
				else{
					Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth disponible", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				if (!mBluetoothAdapter.isEnabled()) {
					Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth desactivado", Toast.LENGTH_SHORT);
					toast.show();
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}
				else {
					Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth activado", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				//Ponemos el dispositivo visible indefinidamente
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
				startActivity(discoverableIntent);
				
				/****Meto las funciones del servidor ****/				
				// Cancel any thread attempting to make a connection
		        if (mConnectThread != null) {
		        	System.out.println("mConnectThread != null");
		        	mConnectThread.cancel(); 
		        	mConnectThread = null;	
		        }

		        // Cancel any thread currently running a connection
		        if (mConnectedThread != null) {
		        	System.out.println("mConnectedThread != null");
		        	mConnectedThread.cancel(); 
		        	mConnectedThread = null;
		        }

		        // Start the thread to listen on a BluetoothServerSocket
		        if (mAcceptThread == null) {
		        	System.out.println("mAcceptThread == null");
		            mAcceptThread = new AcceptThread();
		            mAcceptThread.start();
		        }
		        /****Meto las funciones del servidor ****/
			}
        });
        
		busca.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if (adaptador != null)//Lista creada anteiormente
					//Boramos la lista
					adaptador.clear();					
								
				if (mBluetoothAdapter == null) {
					Toast toast = Toast.makeText(getApplicationContext(), "Conectar antes de buscar", Toast.LENGTH_SHORT);
					toast.show();
				}
				else{
					//Descubrimos
					if (mBluetoothAdapter.isDiscovering()) {
						mBluetoothAdapter.cancelDiscovery();
						Toast toast = Toast.makeText(getApplicationContext(), " Ya estaba buscando. Comienza de nuevo", Toast.LENGTH_SHORT);
						toast.show();
			        }
	
			        // Request discover from BluetoothAdapter
					mBluetoothAdapter.startDiscovery();
					Toast toast = Toast.makeText(getApplicationContext(), " Comienza la busqueda", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		
		desconecta.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mBluetoothAdapter.isDiscovering()) {
					mBluetoothAdapter.cancelDiscovery();
				}
				offset=1;
			}
		});
		
		envia.setOnClickListener(new OnClickListener() {
			//Actua como servidor
			public void onClick(View v) {
				
				if ((mConnectedThread == null) || (mConnectThread == null) || (mAcceptThread == null)){
					Toast toast = Toast.makeText(getApplicationContext(), " No se ha iniciado ninguna conexión", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				enviaBBDD();
			}
		});
		
		paired.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if (adaptador != null)//Lista creada anteiormente
					//Boramos la lista
					adaptador.clear();
				
				if (mBluetoothAdapter == null) {
					Toast toast = Toast.makeText(getApplicationContext(), "Conectar antes de buscar", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else{				
					// Get a set of currently paired devices
			        Set<BluetoothDevice> pairedDev = mBluetoothAdapter.getBondedDevices();
					
					if (pairedDev.size() > 0) {
			            for (BluetoothDevice device : pairedDev) {
			                foundDevices.put(device.getName(), device.getAddress());
			            }
			            if (datos == null)
							datos = new ArrayList<String>();
						
		            	Iterator<String> showdevices = foundDevices.keySet().iterator();
		            	while(showdevices.hasNext()) {
		            	    String key=(String)showdevices.next();
		            	    datos.add(""+key);
		            	    //Almacenamos el nombre de los dispositivos para mostrarlos en la lista
		            	}
		            	adaptador = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, datos);
		                lista.setAdapter(adaptador);
					}
				}
			}
		});
		
		lista.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				
				mac = foundDevices.get(datos.get(arg2));
				
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
				
				// Cancel any thread attempting to make a connection
		        if (mConnectThread != null) {
		        	mConnectThread.cancel();
		        	mConnectThread = null;
		        }

		        // Cancel any thread currently running a connection
		        if (mConnectedThread != null) {
		        	mConnectedThread.cancel(); 
		        	mConnectedThread = null;
		        }

		        // Start the thread to connect with the given device
		        mConnectThread = new ConnectThread(device);
		        mConnectThread.start();			
			}
		});
		
		// Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
    }
    
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mConnectThread != null) {
        	mConnectThread.cancel();
        	mConnectThread = null;
        }
        
        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }
        
        if (mAcceptThread != null) {
        	mAcceptThread.cancel(); 
        	mAcceptThread = null;
        }
        
        if (mBluetoothAdapter != null) {
        	mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                foundDevices.put(device.getName(), device.getAddress());
				
            // When discovery is finished, change the Activity title            
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	if (foundDevices.isEmpty()){
                	Toast toast = Toast.makeText(getApplicationContext(), "No se encontro dispositivos", Toast.LENGTH_LONG);
    				toast.show();
                }
                else {//Lista de dispositivos
                	if (datos == null)
                		datos = new ArrayList<String>();
                	
                	Iterator<String> showdevices = foundDevices.keySet().iterator();
                	while(showdevices.hasNext()) {
                	    String key=(String)showdevices.next();
                	    datos.add(""+key);
                	    //Almacenamos el nombre de los dispositivos para mostrarlos en la lista
                	}
                	adaptador = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, datos);
                    lista.setAdapter(adaptador);
                	
                }
            } 
            else{
            	Toast toast = Toast.makeText(getApplicationContext(), "Opcion desconocida", Toast.LENGTH_LONG);
				toast.show();
            }
        }
    };    
    
    /*************************************************************
    **************************************************************
    **************************************************************
    **************************************************************/
    
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                System.out.println("creo btserversocket");
            } catch (IOException e) {
            	System.out.println("AcceptThread ex 1");
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Listen to the server socket if we're not connected
            while (true) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();                  
                } catch (IOException e) {
                	System.out.println("AcceptThread ex 2");
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
	                	synchronized (BTChatActivity.this) {
	                	System.out.println("Conexion aceptada");
	                 	// Do work to manage the connection (in a separate thread)
	                	// Cancel the thread that completed the connection
	                    if (mConnectThread != null) {
	                    	mConnectThread.cancel();
	                    	mConnectThread = null;
	                    }
	
	                    // Cancel any thread currently running a connection
	                    if (mConnectedThread != null) {
	                    	mConnectedThread.cancel(); 
	                    	mConnectedThread = null;
	                    }
	
	                    // Cancel the accept thread because we only want to connect to one device
	                    if (mAcceptThread != null) {
	                    	mAcceptThread.cancel();
	                    	mAcceptThread = null;
	                    }
	
	                    // Start the thread to manage the connection and perform transmissions
	                    mConnectedThread = new ConnectedThread(socket);
	                    mConnectedThread.start();
	
	                    // Send the name of the connected device back to the UI Activity
	                    Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
	                    Bundle bundle = new Bundle();
	                    bundle.putString(DEVICE_NAME, socket.getRemoteDevice().getName());
	                    msg.setData(bundle);
	                    mHandler.sendMessage(msg);
	                    
	                    try {
							mmServerSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    break;
	                }
                }
                else
                	System.out.println("No hay conexion");
            }
        }

        public void cancel() {
        	try {
				mmServerSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            	System.out.println("ConnectThread ex 1");
            }
            mmSocket = tmp;
        }

        public void run() {
        
        	// Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
            	System.out.println("ConnectThread ex 2");
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                	System.out.println("ConnectThread ex 3");
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            synchronized (BTChatActivity.this) {
            	mConnectThread = null;
            }
            // Cancel the thread that completed the connection
            if (mConnectThread != null) {
            	mConnectThread.cancel();
            	mConnectThread = null;
            }

            // Cancel any thread currently running a connection
            if (mConnectedThread != null) {
            	mConnectedThread.cancel();
            	mConnectedThread = null;
            }

            // Cancel the accept thread because we only want to connect to one device
            if (mAcceptThread != null) {
            	mAcceptThread.cancel();
            	mAcceptThread = null;
            }

            // Start the thread to manage the connection and perform transmissions
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

            // Send the name of the connected device back to the UI Activity
            Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_NAME, mmDevice.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            	System.out.println("ConnectThread ex 4");
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            	System.out.println("ConnectedThread ex 1");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                	// Read from the InputStream
                    int bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                	System.out.println("ERROR BT");
                    break;
                }
            }
        }
    
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { 
	        	System.out.println("ConnectedThread ex 3");
	        }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { 
	        	System.out.println("ConnectedThread ex 4");
	        }
	    }  
    } 
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            // Create temporary object
            ConnectedThread r;
            synchronized (this) {
                r = mConnectedThread;
            }
            // Perform the write unsynchronized
            System.out.println("Envio: "+message);
            r.write(send);
        }
    }  
       
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case STATE_CONNECTED:
                    break;
                case STATE_CONNECTING:
                    break;
                case STATE_LISTEN:
                case STATE_NONE:
                    break;
                }
                break;
            case MESSAGE_WRITE:
                break;
            case MESSAGE_READ:
                /*byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                
                if (readMessage.substring(0, 1).equals("*")){
                	
                	final Dialog dialog = new Dialog(context);
         			dialog.setContentView(R.layout.dialoglugar);
         			dialog.setTitle("Información del lugar");
         			dialog.setCancelable(true);
         			 			
         			textnombre = (TextView) dialog.findViewById(R.id.textViewNombre);
         			textdesc = (TextView) dialog.findViewById(R.id.textViewDesc);
         			textlatitud = (TextView) dialog.findViewById(R.id.textViewLatitud);
         			textlongitud = (TextView) dialog.findViewById(R.id.textViewLongitud);
         			texttipo = (TextView) dialog.findViewById(R.id.textViewTipo);
         			
                    Button ok = (Button) dialog.findViewById(R.id.buttonok);
                    Button cancel = (Button) dialog.findViewById(R.id.buttoncancel);

                	int pos=0;
                	textnombre.setText(readMessage.substring(1,readMessage.indexOf("*",1)).toString());
                	pos = readMessage.indexOf("*",1)+1;
                	textdesc.setText(readMessage.substring(pos, readMessage.indexOf("*",pos)).toString());
                	pos = readMessage.indexOf("*",pos)+1;
                	textlatitud.setText(readMessage.substring(pos, readMessage.indexOf("*",pos)).toString());
                	pos = readMessage.indexOf('*',pos)+1;
                	textlongitud.setText(readMessage.substring(pos, readMessage.indexOf("*",pos)).toString());
                	pos = readMessage.indexOf('*',pos)+1;
                	texttipo.setText(readMessage.substring(pos, readMessage.indexOf("*",pos)).toString());
                	
                	dialog.show();
                	
                	ok.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
							//Almacena en BBDD
							SensorDB usdbh = new SensorDB(context, "DBSensor", null, 1);
							SQLiteDatabase db = usdbh.getWritableDatabase();
							
							usdbh.intLugar(db, 
										   textnombre.getText().toString(), 
										   textdesc.getText().toString(), 
										   Double.valueOf(textlatitud.getText().toString()).doubleValue(),
										   Double.valueOf(textlongitud.getText().toString()).doubleValue(), 
										   texttipo.getText().toString());
							
							BTChatActivity.this.sendMessage("next");
							
						}
					});
                	
                	cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
							BTChatActivity.this.sendMessage("next");
						}
					});
                }
                
                else if (readMessage.substring(0, 4).equals("next")){
                	offset++;
                	enviaBBDD();
                }*/
                
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    public void enviaBBDD(){
    	SensorDB usdbh = new SensorDB(context, "DBSensor", null, 1);
		SQLiteDatabase db = usdbh.getWritableDatabase();
		
		String sql = "SELECT nombre, descripcion, lattitude, longitude, tipo FROM Lugar";
		Cursor fila = db.rawQuery(sql, null);
		
		String trama;
		
		System.out.println("Offset "+offset);
		//Nos aseguramos de que existe al menos un registro
		if (fila.move(offset)) {
			System.out.println("envio");
			
			trama = "*"+fila.getString(0)+"*"+fila.getString(1)+"*"
					   +fila.getString(2)+"*"+fila.getString(3)+"*"
					   +fila.getString(4)+"*";
			
			sendMessage(trama);					
		}	
		else 
			offset = 1;
    }
}    