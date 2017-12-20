import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class InstructionMemory 
{
	static int latestArithmeticInstructionAddress;
	
	static String latestArithmeticInstruction;
	
	Map<Integer, InstructionInfoFromFile> instructionListMap;

	public Map<Integer, InstructionInfoFromFile> getInstructionListMap() {
		return instructionListMap;
	}

	public void setInstructionListMap(Map<Integer, InstructionInfoFromFile> instructionListMap) {
		this.instructionListMap = instructionListMap;
	}
}

