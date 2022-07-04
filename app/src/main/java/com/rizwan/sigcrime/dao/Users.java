package com.rizwan.sigcrime.dao;

public class Users{
private int id;
private String user;
private String pass;
private String email;
private String full_name;
private String is_admin;
private String user_verified;
private String filename;
private UserData userData;

public String getUser(){
		return user;
		}

public void setUser(String user){
		this.user=user;
		}

public String getPass(){
		return pass;
		}

public void setPass(String pass){
		this.pass=pass;
		}

public String getEmail(){
		return email;
		}

public void setEmail(String email){
		this.email=email;
		}

public String getFull_name(){
		return full_name;
		}

public void setFull_name(String full_name){
		this.full_name=full_name;
		}

public String getIs_admin(){
		return is_admin;
		}

public void setIs_admin(String is_admin){
		this.is_admin=is_admin;
		}

public String getUser_verified(){
		return user_verified;
		}

public void setUser_verified(String user_verified){
		this.user_verified=user_verified;
		}

public String getFilename(){
		return filename;
		}

public void setFilename(String filename){
	if (filename == null || filename== "null" || filename == ""){
		this.filename = "def_user.png";
	}else{
		this.filename = filename;
	}
		}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
}
