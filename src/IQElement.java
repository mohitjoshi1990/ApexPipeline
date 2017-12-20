
public class IQElement {
	
	private Integer validBitSrcRegister1;
	
	private Integer validBitSrcRegister2;
	
	private RegisterCustom srcRegister1;
	
	private RegisterCustom srcRegister2;
	
	private RegisterCustom destRegister;
	
	private int indexLSQElement;
	
	private String opCode;
	
	private Integer literalValue;
	
	private Integer allocatedStatusBit;
	
	//used to keep for the instruction just to remember the selection logic for the earliest instruction ...
	private int cycleNumber;
	
	private InstructionInfo iqInstrnObj;

	private Boolean branchOnZeroFlag;
	
	private Integer zeroFalgValue;

	public RegisterCustom getSrcRegister1() {
		return srcRegister1;
	}

	public void setSrcRegister1(RegisterCustom srcRegister1) {
		this.srcRegister1 = srcRegister1;
	}

	public RegisterCustom getSrcRegister2() {
		return srcRegister2;
	}

	public void setSrcRegister2(RegisterCustom srcRegister2) {
		this.srcRegister2 = srcRegister2;
	}

	public RegisterCustom getDestRegister() {
		return destRegister;
	}

	public void setDestRegister(RegisterCustom destRegister) {
		this.destRegister = destRegister;
	}

	public int getIndexLSQElement() {
		return indexLSQElement;
	}

	public void setIndexLSQElement(int indexLSQElement) {
		this.indexLSQElement = indexLSQElement;
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

	public int getCycleNumber() {
		return cycleNumber;
	}

	public void setCycleNumber(int cycleNumber) {
		this.cycleNumber = cycleNumber;
	}

	public Integer getAllocatedStatusBit() {
		return allocatedStatusBit;
	}

	public Integer getValidBitSrcRegister1() {
		return validBitSrcRegister1;
	}

	public void setValidBitSrcRegister1(Integer validBitSrcRegister1) {
		this.validBitSrcRegister1 = validBitSrcRegister1;
	}

	public Integer getValidBitSrcRegister2() {
		return validBitSrcRegister2;
	}

	public void setValidBitSrcRegister2(Integer validBitSrcRegister2) {
		this.validBitSrcRegister2 = validBitSrcRegister2;
	}

	public void setAllocatedStatusBit(Integer allocatedStatusBit) {
		this.allocatedStatusBit = allocatedStatusBit;
	}

	public InstructionInfo getIqInstrnObj() {
		return iqInstrnObj;
	}

	public void setIqInstrnObj(InstructionInfo iqInstrnObj) {
		this.iqInstrnObj = iqInstrnObj;
	}

	public Boolean getBranchOnZeroFlag() {
		return branchOnZeroFlag;
	}

	public void setBranchOnZeroFlag(Boolean branchOnZeroFlag) {
		this.branchOnZeroFlag = branchOnZeroFlag;
	}

	public Integer getZeroFalgValue() {
		return zeroFalgValue;
	}

	public void setZeroFalgValue(Integer zeroFalgValue) {
		this.zeroFalgValue = zeroFalgValue;
	}
}
