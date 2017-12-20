import java.util.ArrayList;
import java.util.List;

public class ROB {
	
    int currentSize; //Current Circular Queue Size
    RobElement[] robElements;
    
    private String archDestReg;
    private RegisterCustom destPhyReg;
    private Integer branchIdIndex;
    
    
    private InstructionInfo instrnObj;
	
	private List<InstructionInfo> committedInstrnList;

	private int maxSize; //Circular Queue maximum size
    private int tail;//rear position of Circular queue(new element enqueued at rear).
    private int head; //front position of Circular queue(element will be dequeued from front).      

    public ROB() {
        this.maxSize = 32;
        robElements = new RobElement[maxSize];
        currentSize = 0;
        head = 0;
        tail = 0;
        this.setCommittedInstrnList(new ArrayList<InstructionInfo> ());
		for(int i = 0; i <= 31; i++) {
			RobElement robElem = new RobElement();
			robElem.setStatusBit(0);
			robElem.setHasCorrectValue(false);
			robElem.setAllocStatCode(0);
			robElements[i] = robElem;
		}
    }

    /**
     * Enqueue elements to rear.
     */
   public Integer addElemToROB(String archDestReg, RegisterCustom destReg, InstructionInfo inputInstrnObj, Integer branchIdIndex) {
    	Integer index = tail;
        if (isRObFull()) {
        }
        else {
        	RobElement robElem = new RobElement();
        	if(destReg != null && !destReg.getRegisterName().equalsIgnoreCase("")) {
        		robElem.setDestRegisterName(destReg.getRegisterName());
        	}

        	if(archDestReg != null && !archDestReg.equalsIgnoreCase("")) {
        		robElem.setArchDestRegisterName(archDestReg);
        	}
        	robElem.setStatusBit(1);
        	robElem.setHasCorrectValue(false);
        	robElem.setPcAddressValue(inputInstrnObj.getProgramCounterValue());
        	robElem.setInstrnOprType(inputInstrnObj.getOpCode());
        	robElem.setAllocStatCode(1);
        	robElem.setInstrnObj(inputInstrnObj);
        	if(branchIdIndex != null) {
        		robElem.setBisIndex(branchIdIndex);
        	}
            robElements[tail] = robElem;
            tail = (tail + 1) % maxSize;
            currentSize++;
        }
        return index;
    }

    /**
     * Dequeue element from Front.
     */
    public RobElement remElemFromROB() {
    	RobElement removedElement = new RobElement();
        if (isRobEmpty()) {
        }
        else {
        	removedElement = robElements[head];
            robElements[head] = new RobElement();
            robElements[head].setStatusBit(0);
            robElements[head].setHasCorrectValue(false);
            robElements[head].setAllocStatCode(0);
            head = (head + 1) % maxSize;
            currentSize--;
        }
        return removedElement;
    }
    

    public boolean isRObFull() {
        return (currentSize == maxSize);
    }

    
    public boolean isRobEmpty() {
        return (currentSize == 0);
    }

    @Override
    public String toString() {
    	String result = "\n<ROB>:";
    	for(RobElement robelem : robElements) {
    		if(robelem != null && robelem.getAllocStatCode() != null && robelem.getAllocStatCode() == 1) {
    			if(robelem == robElements[head] && robelem.getStatusBit() == 0) {
    				// Do nothing
    			}else {
    				result = result +"\n * "+ robelem.getInstrnObj().getInstruction();
    			}
    		}
    	}
		result = result + "\n Commit:";
		if(this.getCommittedInstrnList().size() > 0) {
			for(InstructionInfo instrn : this.getCommittedInstrnList()) {
				result = result + "\n * "+ instrn.getInstruction();				
			}
		}
    	return result;
    }
	 

    public RobElement[] getRobElements() {
		return robElements;
	}

	public void setRobElements(RobElement[] robElements) {
		this.robElements = robElements;
	}

	public int getTail() {
		return tail;
	}

	public void setTail(int tail) {
		this.tail = tail;
	}

	public int getHead() {
		return head;
	}

	public void setHead(int head) {
		this.head = head;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public InstructionInfo getInstrnObj() {
		return instrnObj;
	}

	public void setInstrnObj(InstructionInfo instrnObj) {
		this.instrnObj = instrnObj;
	}

	public String getArchDestReg() {
		return archDestReg;
	}

	public void setArchDestReg(String archDestReg) {
		this.archDestReg = archDestReg;
	}

	public RegisterCustom getDestPhyReg() {
		return destPhyReg;
	}

	public void setDestPhyReg(RegisterCustom destPhyReg) {
		this.destPhyReg = destPhyReg;
	}

	public Integer getBranchIdIndex() {
		return branchIdIndex;
	}

	public void setBranchIdIndex(Integer branchIdIndex) {
		this.branchIdIndex = branchIdIndex;
	}

	public List<InstructionInfo> getCommittedInstrnList() {
		return committedInstrnList;
	}

	public void setCommittedInstrnList(List<InstructionInfo> committedInstrnList) {
		this.committedInstrnList = committedInstrnList;
	}
}
