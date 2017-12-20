
public class Bus {

	int forwardedValForDecode;
	
	String forwardedReg;
	
	PSWRegister forwardedPsgReg;

	public int getForwardedValForDecode() {
		return forwardedValForDecode;
	}

	public void setForwardedValForDecode(int forwardedValForDecode) {
		this.forwardedValForDecode = forwardedValForDecode;
	}

	public String getForwardedReg() {
		return forwardedReg;
	}

	public void setForwardedReg(String forwardedReg) {
		this.forwardedReg = forwardedReg;
	}

	public PSWRegister getForwardedPsgReg() {
		return forwardedPsgReg;
	}

	public void setForwardedPsgReg(PSWRegister forwardedPsgReg) {
		this.forwardedPsgReg = forwardedPsgReg;
	}
}
