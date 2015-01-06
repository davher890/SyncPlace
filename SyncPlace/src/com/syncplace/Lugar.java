package com.syncplace;

import android.os.Parcel;
import android.os.Parcelable;

public class Lugar implements Parcelable {
	
	private int id;
	private String nombre = null;
	private String descripcion = null;
	private double latitud;
	private double longitud;
	private String phone;
	private String tipo = null;
	
	public Lugar(){
	}
	
	public Lugar(int id, String nombre, String descripcion, double latitud, double longitud, String tipo, String phone){
		this.setId(id);
		this.longitud = longitud;
		this.latitud = latitud;
		this.descripcion = descripcion;
		this.nombre = nombre;
		this.tipo = tipo;
		this.phone = phone;
		
	}
	
	public Lugar(String nombre, String descripcion, double latitud, double longitud, String tipo, String phone){
		this.longitud = longitud;
		this.latitud = latitud;
		this.descripcion = descripcion;
		this.nombre = nombre;
		this.tipo = tipo;
		this.phone = phone;
	}
	
	public Lugar (double latitud, double longitud){
		this.longitud = longitud;
		this.latitud = latitud;
	}
	
	public String getNombre(){
		return nombre;		
	}

	public double getLatitud(){
		return latitud;		
	}
	
	public double getLongitud(){
		return longitud;
	}
	
	public String getDescripcion(){
		return descripcion;
	}
	
	public String getTipo(){
		return tipo;
	}
	
	public void setNombre(String nom){
		nombre = nom;
	}
	
	public void setDescripcion(String desc){
		descripcion = desc;
	}
	
	public void setLatitud(double lat){
		latitud = lat;		
	}	
	
	public void setLongitud(double lon){
		longitud = lon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(nombre);
		dest.writeString(descripcion);
		dest.writeDouble(latitud);
		dest.writeDouble(longitud);
		dest.writeString(tipo);
	}
	
	public static final Parcelable.Creator<Lugar> CREATOR = new Parcelable.Creator<Lugar>() {
		public Lugar createFromParcel(Parcel in) {
		
			Lugar lugar = new Lugar();
			lugar.nombre = in.readString();
			lugar.descripcion = in.readString();
			lugar.latitud = in.readDouble();
			lugar.longitud = in.readDouble();
			lugar.tipo = in.readString();
			return lugar;
			
		}

		@Override
		public Lugar[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Lugar[size];
		}
	};
}
