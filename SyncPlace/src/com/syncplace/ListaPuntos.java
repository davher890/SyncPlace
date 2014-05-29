package com.syncplace;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ListaPuntos extends ArrayList<Lugar> implements Parcelable{
	
	public ListaPuntos(){		
	}
	public void writeToParcel(Parcel dest, int flags){
		int size = this.size();
		dest.writeInt(size);
		for (int i = 0; i < size; i++){
			Lugar gp = this.get(i);
			dest.writeString(gp.getNombre());
			dest.writeString(gp.getDescripcion());
			dest.writeString(String.valueOf(gp.getLatitud()));
			dest.writeString(String.valueOf(gp.getLongitud()));
			dest.writeString(gp.getTipo());
		}	
	}
	
	public ListaPuntos(Parcel in){
		readfromParcel(in);	
	}
	
	private void readfromParcel(Parcel in){
		this.clear();
		//Leemos el tamaño del array 
		int size = in.readInt();
		for (int i = 0; i < size; i++){
			//el orden de los atributos SI importa
			
			String nombre = in.readString();
			String desc = in.readString();
			double lat = Double.valueOf(in.readString()).doubleValue();
			double lng = Double.valueOf(in.readString()).doubleValue();
			String tipo = in.readString();
			
			Lugar gp = new Lugar(nombre, desc, lat, lng, tipo);
			this.add(gp);	
		}	
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){		
		public ListaPuntos createFromParcel(Parcel in){
			return new ListaPuntos(in);
		}		
		public Object[] newArray(int arg0){
			return null;	
		}	
	};
	
	public int describeContents(){
		return 0;	
	}	
}