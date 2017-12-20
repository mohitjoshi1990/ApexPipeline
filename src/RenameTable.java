import java.util.LinkedHashMap;
import java.util.Map;

public class RenameTable {

	static Map<String, RegisterCustom> archToPhyRenTbl = new LinkedHashMap <String, RegisterCustom>();
	
	@Override
    public String toString() {
    	String result = "\n<RENAME TABLE>:";
    	for(Map.Entry<String, RegisterCustom> renTableEntry : archToPhyRenTbl.entrySet()) {
        	
    		result = result +"\n * "+renTableEntry.getKey()+" : "+renTableEntry.getValue().getRegisterName();    			
    	}
    	return result;
    }
}
