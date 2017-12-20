/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class Stage
{  
    InstructionInfo inputInstrn;
    
    InstructionInfo outputInstrn;
    
    boolean stalled;

	public Stage(InstructionInfo inputInstrn, InstructionInfo outputInstrn, boolean stalled) 
	{
			super();
			this.inputInstrn = inputInstrn;
			this.outputInstrn = outputInstrn;
			this.stalled = stalled;
	}
	
	public void flush() {
		InstructionInfo defaultInstrnObj = new InstructionInfo();
		defaultInstrnObj.setInstruction("NOP");
		this.setInputInstrn(defaultInstrnObj);
		this.setOutputInstrn(defaultInstrnObj);
		this.setStalled(false);
		
	}
	
	public InstructionInfo getInputInstrn() {
		return inputInstrn;
	}
	
	public void setInputInstrn(InstructionInfo inputInstrn) {
		this.inputInstrn = inputInstrn;
	}
	
	public InstructionInfo getOutputInstrn() {
		return outputInstrn;
	}
	
	public void setOutputInstrn(InstructionInfo outputInstrn) {
		this.outputInstrn = outputInstrn;
	}
	
	public boolean isStalled() {
		return stalled;
	}
	
	public void setStalled(boolean stalled) {
		this.stalled = stalled;
	}
}

