package com.spartanapps.ibeaconsocializer;

public class StatusFlowItem{
	
	private String innerID;
	private String innerGender;
	private String innerAge;
	private String innerStatus;
	
	//CONSTRUCTORS
	public StatusFlowItem(String ID, String Gender, String Age, String Status){
		this.innerID = ID;
		this.innerGender = Gender;
		this.innerAge = Age;
		this.innerStatus = Status;
	}
	
	public String getID(){
		return this.innerID;
	}
	
	public String getGender() {
		return this.innerGender;
	}
	
	public String getAge() {
		return this.innerAge;
	}
	
	public String getStatus(){
		return this.innerStatus;
	}
}