
public class LsqElement {

	private int pcAddressValue;
	
	private String destRegisterName;
	
	private String archDestRegisterName;
	
	private RegisterCustom srcRegister1;
	
	private RegisterCustom destRegister;
	
	private int result;
	
	private int statusBit;
	
	private String instrnOprType;
	
	private Integer allocatedStatusBit;
	
	private Integer bisIndex;
	
	private InstructionInfo instrnObj;
	
	private Integer memoryAddress;

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

	public RegisterCustom getSrcRegister1() {
		return srcRegister1;
	}

	public void setSrcRegister1(RegisterCustom srcRegister1) {
		this.srcRegister1 = srcRegister1;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
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

	public Integer getAllocatedStatusBit() {
		return allocatedStatusBit;
	}

	public void setAllocatedStatusBit(Integer allocatedStatusBit) {
		this.allocatedStatusBit = allocatedStatusBit;
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

	public Integer getMemoryAddress() {
		return memoryAddress;
	}

	public void setMemoryAddress(Integer memoryAddress) {
		this.memoryAddress = memoryAddress;
	}

	public RegisterCustom getDestRegister() {
		return destRegister;
	}

	public void setDestRegister(RegisterCustom destRegister) {
		this.destRegister = destRegister;
	}
	
}
