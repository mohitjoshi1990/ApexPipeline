class LSQ
{
	static LsqElement lsqElemList[];
	
	InstructionInfo inputInstrn;
    
    InstructionInfo outputInstrn;
    
    static Integer byPassLSQIndex;

	boolean stalled;
	
	public LSQ(InstructionInfo inputInstrn, InstructionInfo outputInstrn) 
	{
		super();
		this.inputInstrn = inputInstrn;
		this.outputInstrn = outputInstrn;
		this.stalled = false;
		byPassLSQIndex = -1;
		lsqElemList = new LsqElement[32];
		for(int i = 0; i < 32; i++) {
			LsqElement lsqElem = new LsqElement();
			lsqElem.setAllocatedStatusBit(0);
			lsqElemList[i] = lsqElem;
		}
	}
	
	
	public boolean isLSQFull() {
		boolean lsqFull = true;
		for(LsqElement lsqElem : lsqElemList) {
			if(lsqElem.getAllocatedStatusBit() == 0) {
				lsqFull = false;
				break;
			}
		}
		return lsqFull;
	}
	
	
	public boolean isLSQEmpty() {
		boolean lsqEmpty = true;
		for(LsqElement lsqElem : lsqElemList) {
			if(lsqElem.getAllocatedStatusBit() == 1) {
				lsqEmpty = false;
				break;
			}
		}
		return lsqEmpty;
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
	
	@Override
    public String toString() {
		Integer allocatedIQElems = 0;
    	String result = "\n<LSQ>:";
    	int i = 0;
    	for(LsqElement lsqelem : lsqElemList) {
    		if(lsqelem.getAllocatedStatusBit() == 1) {
    			if(i == 31) {
    				result = result +"\n * "+lsqelem.getInstrnObj().getInstruction()+ "(Head)";
    			}else {
    				result = result +"\n * "+lsqelem.getInstrnObj().getInstruction();    				
    			}
    		}
    		i++;
    	}
    	return result;
    }
}
