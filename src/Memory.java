import java.util.LinkedHashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


class Memory
{
    static int memoryBaseAddress;
    
    static Map<Integer, Integer> memoryDataMap;

	public Memory() {
		memoryBaseAddress = 0;
		Map<Integer, Integer> memoryMap = new LinkedHashMap<Integer, Integer>();
		for(int i= 0; i < 400; i+=4) {
			memoryMap.put(i, 0);
		}
		this.memoryDataMap = memoryMap;
	}

	public Map<Integer, Integer> getMemoryDataMap() {
		return memoryDataMap;
	}

	public void setMemoryDataMap(Map<Integer, Integer> memoryDataMap) {
		this.memoryDataMap = memoryDataMap;
	}
	
}

