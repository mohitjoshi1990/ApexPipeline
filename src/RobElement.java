
public class RobElement {

	private int pcAddressValue;
	
	private String destRegisterName;
	
	private String archDestRegisterName;
	
	private int result;
	
	private String exceptionCode;
	
	private int statusBit;
	
	private boolean hasCorrectValue;
	
	private String instrnOprType;
	
	private Integer allocStatCode;
	
	private Integer bisIndex;
	
	private InstructionInfo instrnObj;
	
	private Integer deleteUntilROBIndex;

	public int getPcAddressValue() {
		return pcAddressValue;
	}

	public void setPcAddressValue(int pcAddressValue) {
		this.pcAddressValue = pcAddressValue;
	}

	public String getDestRegisterName() {
		return destRegisterName;
	}

	public void setDestRegisterName(String destRegisterName) {
		this.destRegisterName = destRegisterName;
	}

	public String getArchDestRegisterName() {
		return archDestRegisterName;
	}

	public void setArchDestRegisterName(String archDestRegisterName) {
		this.archDestRegisterName = archDestRegisterName;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public int getStatusBit() {
		return statusBit;
	}

	public void setStatusBit(int statusBit) {
		this.statusBit = statusBit;
	}

	public String getInstrnOprType() {
		return instrnOprType;
	}

	public void setInstrnOprType(String instrnOprType) {
		this.instrnOprType = instrnOprType;
	}

	public Integer getAllocStatCode() {
		return allocStatCode;
	}

	public void setAllocStatCode(Integer allocStatCode) {
		this.allocStatCode = allocStatCode;
	}

	public Integer getBisIndex() {
		return bisIndex;
	}

	public void setBisIndex(Integer bisIndex) {
		this.bisIndex = bisIndex;
	}

	public InstructionInfo getInstrnObj() {
		return instrnObj;
	}

	public void setInstrnObj(InstructionInfo instrnObj) {
		this.instrnObj = instrnObj;
	}

	public Integer getDeleteUntilROBIndex() {
		return deleteUntilROBIndex;
	}

	public void setDeleteUntilROBIndex(Integer deleteUntilROBIndex) {
		this.deleteUntilROBIndex = deleteUntilROBIndex;
	}

	public boolean isHasCorrectValue() {
		return hasCorrectValue;
	}

	public void setHasCorrectValue(boolean hasCorrectValue) {
		this.hasCorrectValue = hasCorrectValue;
	}
}
