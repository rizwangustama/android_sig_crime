package com.rizwan.sigcrime.dao;

public class Kecamatan {
	private int id;
	private String name;
	private String alamat;
	private String map_lat;
	private String map_long;
	private String geo_file;
	private String filename;

	public Kecamatan(){

	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name=name;
	}

	public String getAlamat(){
		return alamat;
	}

	public void setAlamat(String alamat){
		this.alamat=alamat;
	}

	public String getMap_lat(){
		return map_lat;
	}

	public void setMap_lat(String map_lat){
		this.map_lat=map_lat;
	}

	public String getMap_long(){
		return map_long;
	}

	public void setMap_long(String map_long){
		this.map_long=map_long;
	}

	public String getGeo_file(){
		return geo_file;
	}

	public void setGeo_file(String geo_file){
		this.geo_file=geo_file;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
