/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class InstructionInfoFromFile
{
	int lineNumber; 
 
	int instructionAddress;
 
	String instruction;
	 
	boolean completed;
	
	boolean latestBranchArithmeticInstn;
	 
	String opCode;
	
	int nextInstructionPCValue;
	
	Integer branchInstructionDependentInstrn;
	
	Integer zeroFlagSet;
	
	String branchDependenceOnInstrn;

	public String getBranchDependenceOnInstrn() {
		return branchDependenceOnInstrn;
	}

	public void setBranchDependenceOnInstrn(String branchDependenceOnInstrn) {
		this.branchDependenceOnInstrn = branchDependenceOnInstrn;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public int getInstructionAddress() {
		return instructionAddress;
	}
	
	public void setInstructionAddress(int instructionAddress) {
		this.instructionAddress = instructionAddress;
	}
	
	public String getInstruction() {
		return instruction;
	}
	
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isLatestBranchArithmeticInstn() {
		return latestBranchArithmeticInstn;
	}

	public void setLatestBranchArithmeticInstn(boolean latestBranchArithmeticInstn) {
		this.latestBranchArithmeticInstn = latestBranchArithmeticInstn;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public int getNextInstructionPCValue() {
		return nextInstructionPCValue;
	}

	public void setNextInstructionPCValue(int nextInstructionPCValue) {
		this.nextInstructionPCValue = nextInstructionPCValue;
	}

	public Integer getBranchInstructionDependentInstrn() {
		return branchInstructionDependentInstrn;
	}

	public void setBranchInstructionDependentInstrn(Integer branchInstructionDependentInstrn) {
		this.branchInstructionDependentInstrn = branchInstructionDependentInstrn;
	}

	public Integer getZeroFlagSet() {
		return zeroFlagSet;
	}

	public void setZeroFlagSet(Integer zeroFlagSet) {
		this.zeroFlagSet = zeroFlagSet;
	} 
 
}