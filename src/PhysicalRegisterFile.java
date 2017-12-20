import java.util.LinkedHashMap;
import java.util.Map;

public class PhysicalRegisterFile {

	Map<String, RegisterCustom> physicalRegisterMap;

	public PhysicalRegisterFile() {		
		physicalRegisterMap = new LinkedHashMap<String, RegisterCustom>();
		for(int i = 0; i < 32; i++) 
		{
			RegisterCustom register = new RegisterCustom();
			register.setRegisterName("P"+i);
			register.setValidStatus(0);
			register.setData(0);
			
			register.setPhysicalRegister(true);
			register.setAllocatedStatusBit(0);
			register.setArchRegNameForPhyReg("");
			physicalRegisterMap.put(register.getRegisterName(), register);
		}
	}
	
	
	public boolean isPhysicalRegisterAvailable() {
		boolean physicalRegisterAvailable = false;
		for (Map.Entry<String,RegisterCustom> entry : physicalRegisterMap.entrySet()) {
			if(entry.getValue().getAllocatedStatusBit() == 0) {
				physicalRegisterAvailable = true;
				break;
			}
		}
		return physicalRegisterAvailable;
	}
	
	
	public RegisterCustom getAvailablePhysicalRegister() {
		RegisterCustom availablePhysicalRegister = null;
		for (Map.Entry<String,RegisterCustom> entry : physicalRegisterMap.entrySet()) {
			if(entry.getValue().getAllocatedStatusBit() == 0) {
				availablePhysicalRegister = entry.getValue();
				break;
			}
		}
		return availablePhysicalRegister;
	}

	public Map<String, RegisterCustom> getPhysicalRegisterMap() {
		return physicalRegisterMap;
	}

	public void setPhysicalRegisterMap(Map<String, RegisterCustom> physicalRegisterMap) {
		this.physicalRegisterMap = physicalRegisterMap;
	}
	
	 @Override
   public String toString() {
   	String result = "Physical Register File : \n";
   	result += "Physical Registers ::";
   	for(Map.Entry<String, RegisterCustom> entry: physicalRegisterMap.entrySet()) {
   		result = result +": Register :"+ entry.getKey() +": Arch Reg :"+ entry.getValue().getArchRegNameForPhyReg()
   				+ ": Allocated :"+ entry.getValue().getAllocatedStatusBit() + ": Valid :"+ entry.getValue().getValidStatus()+ ": Value :"+ entry.getValue().getData();
   	}
   	return result;
   }
	 
}
