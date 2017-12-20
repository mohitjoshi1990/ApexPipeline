/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class RegisterCustom
{
	String registerName;
	
    Integer data;
    
    int validStatus; // considering 0 as the valid value and anything above 0 as the invalid bit
    
    boolean physicalRegister;
    
    Integer allocatedStatusBit;
    
    String archRegNameForPhyReg;
	
	//Integer mostRecentBit;

	public Integer getData() {
		return data;
	}

	public void setData(Integer data) {
		this.data = data;
	}

	public int getValidStatus() {
		return validStatus;
	}

	public void setValidStatus(int validStatus) {
		this.validStatus = validStatus;
	}

	public String getRegisterName() {
		return registerName;
	}

	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}

	public boolean isPhysicalRegister() {
		return physicalRegister;
	}

	public void setPhysicalRegister(boolean physicalRegister) {
		this.physicalRegister = physicalRegister;
	}

	public Integer getAllocatedStatusBit() {
		return allocatedStatusBit;
	}

	public void setAllocatedStatusBit(Integer allocatedStatusBit) {
		this.allocatedStatusBit = allocatedStatusBit;
	}/*

	public Integer getMostRecentBit() {
		return mostRecentBit;
	}

	public void setMostRecentBit(Integer mostRecentBit) {
		this.mostRecentBit = mostRecentBit;
	}*/

	public String getArchRegNameForPhyReg() {
		return archRegNameForPhyReg;
	}

	public void setArchRegNameForPhyReg(String archRegNameForPhyReg) {
		this.archRegNameForPhyReg = archRegNameForPhyReg;
	}
}


