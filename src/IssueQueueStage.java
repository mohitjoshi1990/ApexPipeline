public class IssueQueueStage
{
	static IQElement[] issueQueueElementsList;
	
	InstructionInfo inputInstrn;
    
    InstructionInfo outputInstrn;

	boolean stalled;

	public IssueQueueStage(InstructionInfo inputInstrn, InstructionInfo outputInstrn) 
	{
		super();
		this.inputInstrn = inputInstrn;
		this.outputInstrn = outputInstrn;
		this.stalled = false;
		issueQueueElementsList = new IQElement[16];
		for(int i = 0; i < 16; i++) {
			IQElement iqElem = new IQElement();
			iqElem.setAllocatedStatusBit(0);
			issueQueueElementsList[i] = iqElem;
		}
	}
	
	public void flush() {
		InstructionInfo defaultInstrnObj = new InstructionInfo();
		defaultInstrnObj.setInstruction("NOP");
		this.setInputInstrn(defaultInstrnObj);
		this.setOutputInstrn(defaultInstrnObj);
		this.setStalled(false);
		issueQueueElementsList = new IQElement[16];
		for(int i = 0; i < 16; i++) {
			IQElement iqElem = new IQElement();
			iqElem.setAllocatedStatusBit(0);
			issueQueueElementsList[i] = iqElem;
		}		
	}
	
	
	public boolean isIssueQueueFull() {
		boolean issueQueueFull = true;
		for(IQElement iqElem : issueQueueElementsList) {
			if(iqElem.getAllocatedStatusBit() == 0) {
				issueQueueFull = false;
				break;
			}
		}
		return issueQueueFull;
	}
	
	
	public boolean isIssueQueueEmpty() {
		boolean issueQueueEmpty = true;
		for(IQElement iqElem : issueQueueElementsList) {
			if(iqElem.getAllocatedStatusBit() == 1) {
				issueQueueEmpty = false;
				break;
			}
		}
		return issueQueueEmpty;
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
    	String result = "\n<IQ>:";
    	for(int i=0; i< 16; i++) {
    		if(issueQueueElementsList[i].getAllocatedStatusBit() == 1){
        		result = result +"\n * "+issueQueueElementsList[i].getIqInstrnObj().getInstruction()+" :Index :"+i;    			
    		}
    	}
    	return result;
    }
}
