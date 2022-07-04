package com.rizwan.sigcrime.dao;

public class Kriminal {
	private int id;
	private int id_kecamatan;
	private String kasus_kriminal;
	private int id_klassifikasi_kriminal;
	private String tanggal_kejadian;
	private String waktu_kejadian;
	private String alamat;
	private String map_lat;
	private String map_lang;
	private int create_by;
	private String created;
	private String is_verified;
	private String klasifikasi_kriminal;
	private String kecamatan;
	private String user_full_name;
	private String title;
	private String filename;

	public int getId_kecamatan(){
		return id_kecamatan;
	}

	public void setId_kecamatan(int id_kecamatan){
		this.id_kecamatan=id_kecamatan;
	}

	public int getId_klassifikasi_kriminal(){
		return id_klassifikasi_kriminal;
	}

	public void setId_klassifikasi_kriminal(int id_klassifikasi_kriminal){
		this.id_klassifikasi_kriminal=id_klassifikasi_kriminal;
	}

	public String getTanggal_kejadian(){
		return tanggal_kejadian;
	}

	public void setTanggal_kejadian(String tanggal_kejadian){
		this.tanggal_kejadian=tanggal_kejadian;
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

	public String getMap_lang(){
		return map_lang;
	}

	public void setMap_lang(String map_lang){
		this.map_lang=map_lang;
	}

	public int getCreate_by(){
		return create_by;
	}

	public void setCreate_by(int create_by){
		this.create_by=create_by;
	}

	public String getIs_verified(){
		return is_verified;
	}

	public void setIs_verified(String is_verified){
		this.is_verified=is_verified;
	}

	public String getUser_full_name() {
		return user_full_name;
	}

	public void setUser_full_name(String user_full_name) {
		this.user_full_name = user_full_name;
	}

	public String getKecamatan() {
		return kecamatan;
	}

	public void setKecamatan(String kecamatan) {
		this.kecamatan = kecamatan;
	}

	public String getKlasifikasi_kriminal() {
		return klasifikasi_kriminal;
	}

	public void setKlasifikasi_kriminal(String klasifikasi_kriminal) {
		this.klasifikasi_kriminal = klasifikasi_kriminal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKasus_kriminal() {
		return kasus_kriminal;
	}

	public void setKasus_kriminal(String kasus_kriminal) {
		this.kasus_kriminal = kasus_kriminal;
	}

	public String getWaktu_kejadian() {
		return waktu_kejadian;
	}

	public void setWaktu_kejadian(String waktu_kejadian) {
		this.waktu_kejadian = waktu_kejadian;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		if (filename == null || filename== "null" || filename == ""){
			this.filename = "imgdef.png";
		}else{
			this.filename = filename;
		}

	}
}
