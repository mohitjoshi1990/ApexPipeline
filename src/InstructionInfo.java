import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class InstructionInfo {
	
    int programCounterValue;
    
    String instruction;
    
    String opCode; 
    
    RegisterCustom reg1;
    
    RegisterCustom reg2;
    
    RegisterCustom destReg;
    
    Integer literalValue;
    
    Integer result;
    
    Integer loadValueFromMemory;
	
	boolean latestBranchArithmeticInstn;
	
	Bus forwardBus;
	
    String zeroFlagForBzBnz;
	
	int nextInstructionPCValue;
	
	Integer branchInstructionDependentInstrn;
	
	Integer cycleCount;
	
	Map<String, RegisterCustom> physicalRegisterMapControlFlowInstrn;
	
	Map<String, RegisterCustom> archToPhyRenTblControlFlowInstrn;
	
	TreeMap<Integer, Integer> memMap;
	
	//ControlFlowElement cfElementForCFInstrn;
	
	Integer cfElementIndexBranchDependingInstrn;

	Integer robIndex;
	
	Integer numOfCycles;
	
	String intrnNum;

	public int getProgramCounterValue() {
		return programCounterValue;
	}
    
	public void setProgramCounterValue(int programCounterValue) {
		this.programCounterValue = programCounterValue;
	}
	
	public String getInstruction() {
		return instruction;
	}
	
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	
	public RegisterCustom getReg1() {
		return reg1;
	}
	
	public void setReg1(RegisterCustom reg1) {
		this.reg1 = reg1;
	}
	
	public RegisterCustom getReg2() {
		return reg2;
	}
	
	public void setReg2(RegisterCustom reg2) {
		this.reg2 = reg2;
	}
	
	public RegisterCustom getDestReg() {
		return destReg;
	}
	
	public void setDestReg(RegisterCustom destReg) {
		this.destReg = destReg;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public Integer getLiteralValue() {
		return literalValue;
	}

	public void setLiteralValue(Integer literalValue) {
		this.literalValue = literalValue;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getLoadValueFromMemory() {
		return loadValueFromMemory;
	}

	public void setLoadValueFromMemory(Integer loadValueFromMemory) {
		this.loadValueFromMemory = loadValueFromMemory;
	}

	public boolean isLatestBranchArithmeticInstn() {
		return latestBranchArithmeticInstn;
	}

	public void setLatestBranchArithmeticInstn(boolean latestBranchArithmeticInstn) {
		this.latestBranchArithmeticInstn = latestBranchArithmeticInstn;
	}

	public Bus getForwardBus() {
		return forwardBus;
	}

	public void setForwardBus(Bus forwardBus) {
		this.forwardBus = forwardBus;
	}

	public String getZeroFlagForBzBnz() {
		return zeroFlagForBzBnz;
	}

	public void setZeroFlagForBzBnz(String zeroFlagForBzBnz) {
		this.zeroFlagForBzBnz = zeroFlagForBzBnz;
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

	public Integer getCycleCount() {
		return cycleCount;
	}

	public void setCycleCount(Integer cycleCount) {
		this.cycleCount = cycleCount;
	}/*

	public ControlFlowElement getCfElementForCFInstrn() {
		return cfElementForCFInstrn;
	}

	public void setCfElementForCFInstrn(ControlFlowElement cfElementForCFInstrn) {
		this.cfElementForCFInstrn = cfElementForCFInstrn;
	}*/

	public Integer getCfElementIndexBranchDependingInstrn() {
		return cfElementIndexBranchDependingInstrn;
	}

	public void setCfElementIndexBranchDependingInstrn(Integer cfElementIndexBranchDependingInstrn) {
		this.cfElementIndexBranchDependingInstrn = cfElementIndexBranchDependingInstrn;
	}

	public Integer getRobIndex() {
		return robIndex;
	}

	public void setRobIndex(Integer robIndex) {
		this.robIndex = robIndex;
	}
    
    public Integer getNumOfCycles() {
		return numOfCycles;
	}

	public void setNumOfCycles(Integer numOfCycles) {
		this.numOfCycles = numOfCycles;
	}

	public String getIntrnNum() {
		return intrnNum;
	}

	public void setIntrnNum(String intrnNum) {
		this.intrnNum = intrnNum;
	}
}


