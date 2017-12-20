import java.util.LinkedHashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author mohit
 *
 */
class ArchitectureRegisterFile
{
    Map<String, RegisterCustom> arcRegisterMap;

	public ArchitectureRegisterFile() {
		super();		
		arcRegisterMap = new LinkedHashMap<String, RegisterCustom>();
		for(int i = 0; i <= 15; i++) 
		{
			RegisterCustom register = new RegisterCustom();
			register.setRegisterName("R"+i);
			register.setValidStatus(0);
			register.setData(0);
			register.setPhysicalRegister(false);
			arcRegisterMap.put(register.getRegisterName(), register);
		}
	}

	public Map<String, RegisterCustom> getArcRegisterMap() {
		return arcRegisterMap;
	}

	public void setArcRegisterMap(Map<String, RegisterCustom> arcRegisterMap) {
		this.arcRegisterMap = arcRegisterMap;
	}
}