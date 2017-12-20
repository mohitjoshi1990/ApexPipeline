
public class PSWRegister {

	static Flags flag;
	
	static boolean busy;
	
	boolean tempZeroflag;

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public boolean isTempZeroflag() {
		return tempZeroflag;
	}

	public void setTempZeroflag(boolean tempZeroflag) {
		this.tempZeroflag = tempZeroflag;
	}
}
