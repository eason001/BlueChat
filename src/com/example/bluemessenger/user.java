package com.example.bluemessenger;

public class user {
	int finalsegment = 0;
	int currentsegment = 0;
	String[] message;
	String username;
	String macID;
	int id = 0;
	long initimer = 0;

	
	public user(int i,int c, int f, String m, String u, String mac){
		message = new String[f];
		this.finalsegment=f;
		this.currentsegment=c;
		this.message[c-1]=m;
		this.username=u;
		this.macID=mac;
		this.id=i;
		this.initimer = System.currentTimeMillis();

	}
	
	String getMessage(){
		String result = "";
		for(int i=0;i<this.message.length;i++){
			result+=this.message[i];
		}
		return result;		
	}
	

	
	String getName(){
		return this.username;		
	}
	
	int getFinal(){
		return this.finalsegment;
	}
	
	int getCurrent(){
		return this.currentsegment;
	}
	
	int getID(){
		return this.id;
	}
	
	long getIniTimer(){
		return this.initimer;
	}
	
	String getMac(){
		return this.macID;
	}
	
	void setSeg(int c, String m){
		this.message[c-1]=m;
	}
	
	boolean isSingle(){
		if(this.finalsegment==1){
			return true;
		}else{
			return false;
		}
	}
		
	boolean isComplete(){
		int j=0;
		for (int i=0;i<this.finalsegment;i++){
			if (message[i]!=null){
				j++;
			}
		}
		if(j==this.finalsegment){
			return true;
		}else{
			return false;
		}
	}
}
