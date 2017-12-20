import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class PipelineExecutor
{
	
	Stage fetchStage;
    Stage decodeStage;
    Stage executeAddStage;
    Stage executeMulStage1;
    Stage executeMulStage2;
    Stage executeDivStage1;
    Stage executeDivStage2;
    Stage executeDivStage3;
    Stage executeDivStage4;
    Stage memoryStage;
    IssueQueueStage iqStage;
    Memory mem;
    PSWRegister pswReg;
    ArchitectureRegisterFile arcRegFile;
    PhysicalRegisterFile phyRegFile;
    ROB robObj;
    Map<String, RegisterCustom> renameTable;
    LSQ lsqObj;
    RenameTable renTableObj;
    
	InstructionInfo defaultInstrnObj;
	Integer instructionBaseAddress;
	Integer finalInstructionAddress;
	Integer pcAddress;
    InstructionMemory instructionsFromFile;
	int latestBranchArithmeticInstrnCompleted = 0;
	boolean isExecuteAddStageCalculated = false;
	Integer numInstructionIssued = -1;
	Integer lastInstructionAddress = 0;
	
    
	/*
	 * 
	 */
	public void initialize(String fileName) 
	{	  
	    // declare the different stage of the instruction
		defaultInstrnObj = new InstructionInfo(); 
		instructionsFromFile = new InstructionMemory();
		defaultInstrnObj.setInstruction("NOP");
	    fetchStage = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    decodeStage = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    executeAddStage = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    executeMulStage1 = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    executeMulStage2 = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    executeDivStage1 = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    executeDivStage2 = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    executeDivStage3 = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    executeDivStage4 = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    
	    memoryStage = new Stage(defaultInstrnObj, defaultInstrnObj, false);
	    
	    iqStage = new IssueQueueStage(defaultInstrnObj, defaultInstrnObj);
	    robObj = new ROB();
	    lsqObj = new LSQ(defaultInstrnObj, defaultInstrnObj);
	    arcRegFile = new ArchitectureRegisterFile();
	    phyRegFile = new PhysicalRegisterFile();
	    renTableObj = new RenameTable();
	    renameTable = renTableObj.archToPhyRenTbl;
	    //cfElemsQueue = new ControlFlowQueue();
	    //biStack = new BranchInstructionStack();

		instructionBaseAddress = 4000;
		finalInstructionAddress = 4000;
		pcAddress = 4000;
	    mem = new Memory();
	    pswReg.busy = false;
	    pswReg.flag.zeroFlag = false;
	    
	    readInstructionFromFile(instructionsFromFile, fileName);
	    Cycle.cycleNumber = 0;
	    numInstructionIssued = -1;
	    lastInstructionAddress = 0;
	}
	
	// Method which holds the control flow of instructions through the stages
	void executeInstructionFromFile(Integer cycleCount, String fileName)
	{	
		FileWriter writer = null;
		try {
			writer = new FileWriter("Output.txt");
		} catch (IOException e) {
			System.out.println("Results could not be write to an output result file..");
		}
		PrintWriter printWriter = new PrintWriter(writer);
		
		Cycle.cycleNumber = 0;
		InstructionInfoFromFile instrn = null;
		Map<Integer, InstructionInfoFromFile> instrnFileMap = instructionsFromFile.getInstructionListMap();
		do {
			if(instrnFileMap.containsKey(pcAddress)) {
				
				instrn = instrnFileMap.get(pcAddress);
		    	instrn.setInstructionAddress(pcAddress);
		        pcAddress += 4;
			}else {
			
		    	instrn = new InstructionInfoFromFile();
		    	instrn.setInstruction("NOP");
		    	instrn.setInstructionAddress(pcAddress);
			}
	    	do {
		    	if(cycleCount != -1 && cycleCount == Cycle.cycleNumber) {
		    		initialize(fileName);
		    		printWriter.close();
		    		return;
		    	}
	    		Cycle.cycleNumber++;
	    		commitInstruction();
	        	executeLSQ();
	    		memory();   		
	    		// First fetching to be passed to the functional units if possible.
	    		instructionReadByIntFU();
	    		instructionReadByMulFU();
	    		instructionReadByDivFU();
	        	boolean isflushed = execute(instrnFileMap);
	        	if(!isflushed) {	
		        	executeROB(); 
	        		issueInstructionToQueue(instrnFileMap);
	        		isflushed = decode(printWriter);
	        		if(!isflushed) {
			        	fetch(instrn);
	        		}
	        	}
		    	printCurrentStatus(printWriter);
	    	}while(fetchStage.isStalled());
		}while(pcAddress <= finalInstructionAddress || !(fetchStage.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP") && 
	    		decodeStage.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP") &&
	    		iqStage.isIssueQueueEmpty() && 
	    		lsqObj.isLSQEmpty() && 
	    		executeAddStage.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && 
	    		executeMulStage1.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && 
	    		executeMulStage2.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && 
	    		executeDivStage1.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && 
	    		executeDivStage2.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && 
	    		executeDivStage3.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && 
	    		executeDivStage4.getInputInstrn().getInstruction().equalsIgnoreCase("NOP")  &&
				robObj.isRobEmpty() && 
	    		memoryStage.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP")));
		
		
		printWriter.println("Simulation time in clock cycles simulated:"+ Cycle.cycleNumber);
		printWriter.println("Instructions completed:"+ numInstructionIssued);
		printWriter.println("Achieved CPI :"+ ((numInstructionIssued*1.0)/Cycle.cycleNumber));
		printWriter.close();
	}
	
	
	void printCurrentStatus(PrintWriter printWriter)
	{
		printWriter.println();	
		printWriter.println("Cycle :"+ Cycle.cycleNumber);
		if(fetchStage.isStalled()) {
			printWriter.println("Fetch  :"+ fetchStage.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("Fetch  :"+ fetchStage.outputInstrn.getInstruction().replace("NOP", "Empty"));
		}
		if(decodeStage.isStalled()) {
			printWriter.println("DRF  :"+ decodeStage.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("DRF  :"+ decodeStage.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		
		printWriter.println(renTableObj);
		printWriter.println(iqStage);
		printWriter.println(robObj);
		printWriter.println(lsqObj);
		
		
		if(executeAddStage.isStalled()) {
			printWriter.println("INTFU  :"+ executeAddStage.outputInstrn.getInstruction()+" Stalled "+"Cycle Count :"+executeAddStage.outputInstrn.getCycleCount());
		}else {
			printWriter.println("INTFU  :"+ executeAddStage.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		if(executeMulStage1.isStalled()) {
			printWriter.println("MUL1  :"+ executeMulStage1.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("MUL1  :"+ executeMulStage1.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		if(executeMulStage2.isStalled()) {
			printWriter.println("MUL2  :"+ executeMulStage2.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("MUL2  :"+ executeMulStage2.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		if(executeDivStage1.isStalled()) {
			printWriter.println("DIV1  :"+ executeDivStage1.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("DIV1  :"+ executeDivStage1.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		if(executeDivStage2.isStalled()) {
			printWriter.println("DIV2  :"+ executeDivStage2.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("DIV2  :"+ executeDivStage2.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		if(executeDivStage3.isStalled()) {
			printWriter.println("DIV3  :"+ executeDivStage3.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("DIV3  :"+ executeDivStage3.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		if(executeDivStage4.isStalled()) {
			printWriter.println("DIV4  :"+ executeDivStage4.outputInstrn.getInstruction()+" Stalled");
		}else {
			printWriter.println("DIV4  :"+ executeDivStage4.outputInstrn.getInstruction().replace("NOP", "Empty"));			
		}
		printWriter.println("MEM  :"+ memoryStage.outputInstrn.getInstruction().replace("NOP", "Empty"));
		
		printWriter.println("Register Status");
		for(Map.Entry<String, RegisterCustom> entry: ((Map<String, RegisterCustom>)arcRegFile.getArcRegisterMap()).entrySet()) {
			printWriter.print(entry.getKey()+" : "+ entry.getValue().getData() +": Valid :"+ entry.getValue().getValidStatus() +" ,");
		}
		printWriter.println(phyRegFile);/*
		printWriter.println("Memory Status");
		TreeMap<Integer, Integer> mapSorted = new TreeMap<Integer, Integer>(mem.getMemoryDataMap());
		int count = 0;
		for(Map.Entry<Integer, Integer> memEntry: ((Map<Integer, Integer>)mapSorted).entrySet()) {
			if(count == 100) {
				break;
			}
			printWriter.print(memEntry.getKey()+" ::"+ memEntry.getValue() +", ");
			count++;
		}*/
		printWriter.println("");
		printWriter.println("PC Address :"+pcAddress + ": Final instrn address :"+ finalInstructionAddress);
		printWriter.println("");
		printWriter.println("");	
	}
	
	
	void fetch(InstructionInfoFromFile instructionFileObj) 
	{
		InstructionInfo instrnObj = new InstructionInfo();
		instrnObj.setInstruction(instructionFileObj.getInstruction());
		instrnObj.setProgramCounterValue(instructionFileObj.getInstructionAddress());
		instrnObj.setLatestBranchArithmeticInstn(instructionFileObj.isLatestBranchArithmeticInstn());
		instrnObj.setOpCode(instructionFileObj.getOpCode());
		instrnObj.setNextInstructionPCValue(instructionFileObj.getNextInstructionPCValue());
		instrnObj.setIntrnNum("(I"+instructionFileObj.getLineNumber()+")");
		if(instructionFileObj.getBranchInstructionDependentInstrn() != null) {
			instrnObj.setBranchInstructionDependentInstrn(instructionFileObj.getBranchInstructionDependentInstrn());
		}
		instrnObj.setCycleCount(Cycle.cycleNumber);
		fetchStage.setInputInstrn(instrnObj);
		fetchStage.setOutputInstrn(instrnObj);
		if(decodeStage.isStalled()) {
			fetchStage.setStalled(true);
			return;
		}
		else {
			fetchStage.setStalled(true);
			if(!decodeStage.isStalled()) {
				if(instrnObj.getProgramCounterValue() != lastInstructionAddress) {
					numInstructionIssued ++;
					lastInstructionAddress = instrnObj.getProgramCounterValue();
				}
				decodeStage.setInputInstrn(instrnObj);
				fetchStage.setStalled(false);
				fetchStage.setOutputInstrn(instrnObj);
			}
			return;
		}
	}
	
	
	boolean decode(PrintWriter printWriter) 
	{
		boolean isFlushed = false;
		RegisterCustom reg1 = null;
		RegisterCustom reg2 = null;
		RegisterCustom destReg = null;
		InstructionInfo inputInstrnObj = decodeStage.getInputInstrn();		
		Map<String, RegisterCustom> arcRegisterMap = arcRegFile.getArcRegisterMap();		
		Map<String, RegisterCustom> phyRegisterMap = phyRegFile.getPhysicalRegisterMap();
		boolean destRegistrationReqd = true;
		boolean phyRegCheck = true;
		boolean lsqCheck = true;
		
		if(!inputInstrnObj.getInstruction().equalsIgnoreCase("NOP")) {			
			if(inputInstrnObj.getOpCode().equalsIgnoreCase("JUMP") || inputInstrnObj.getOpCode().equalsIgnoreCase("HALT") || 
					inputInstrnObj.getOpCode().equalsIgnoreCase("STORE") || inputInstrnObj.getOpCode().equalsIgnoreCase("BZ") ||
					inputInstrnObj.getOpCode().equalsIgnoreCase("BNZ")) {
				destRegistrationReqd = false;
			}	
			
			if(destRegistrationReqd && !phyRegFile.isPhysicalRegisterAvailable()) {
				phyRegCheck = false;
			}
			if((inputInstrnObj.getOpCode().equalsIgnoreCase("STORE") || inputInstrnObj.getOpCode().equalsIgnoreCase("LOAD")) && lsqObj.isLSQFull() ) {
				lsqCheck = false;
			}
		}
		
		if(decodeStage.isStalled() && phyRegCheck && lsqCheck && robObj.isRObFull() && iqStage.isIssueQueueFull()) {			
			return isFlushed;
		}
		else {

			boolean stallInstructionInDecode = false;
			if(!inputInstrnObj.getInstruction().equalsIgnoreCase("NOP")) {
				decodeStage.setStalled(true);
				String[] instruction = inputInstrnObj.getInstruction().split(",");
				String operation = inputInstrnObj.getOpCode();
				if(operation.equalsIgnoreCase("HALT")) {
					decodeStage.setInputInstrn(inputInstrnObj);
					decodeStage.setOutputInstrn(inputInstrnObj);
					if(!robObj.isRObFull()) {
						fetchStage.flush();
						decodeStage.setInputInstrn(defaultInstrnObj);
						decodeStage.setStalled(false);
						//robObj.addElemToROB(null, null, inputInstrnObj, null);
						robObj.setArchDestReg(null);
						robObj.setDestPhyReg(null);
						robObj.setInstrnObj(inputInstrnObj);
						robObj.setBranchIdIndex(null);
						inputInstrnObj.setRobIndex(robObj.getTail());
						isFlushed = true;
						pcAddress = finalInstructionAddress + 4;
					}else {
						stallInstructionInDecode = true;
					}
					return isFlushed;
				}else if(operation.equalsIgnoreCase("STORE")) {

					if(!iqStage.isIssueQueueFull() && !robObj.isRObFull() && !lsqObj.isLSQFull()) {
						reg1 = arcRegisterMap.get(instruction[1].trim());
						if(renameTable.containsKey(reg1.getRegisterName())) {
							String phyRegName = renameTable.get(reg1.getRegisterName()).getRegisterName();
							reg1 = phyRegisterMap.get(phyRegName);
						}
							
						String regName = reg1.getRegisterName();
						RegisterCustom regup = new RegisterCustom();
						regup.setRegisterName(regName);
						regup.setValidStatus(reg1.getValidStatus());
						if(reg1.getData() != null) {
							regup.setData(reg1.getData());
						}
						inputInstrnObj.setReg1(regup);
						//inputInstrnObj.setInstruction(inputInstrnObj.getInstruction().replace(instruction[1].trim(), regName));
					
						reg2 = arcRegisterMap.get(instruction[2].trim());
						if(renameTable.containsKey(reg2.getRegisterName())) {
							String phyRegName = renameTable.get(reg2.getRegisterName()).getRegisterName();
							reg2 = phyRegisterMap.get(phyRegName);
						}
							
						String regName2 = reg2.getRegisterName();
						RegisterCustom regup2 = new RegisterCustom();
						regup2.setRegisterName(regName2);
						regup2.setValidStatus(reg2.getValidStatus());
						if(reg2.getData() != null) {
							regup2.setData(reg2.getData());
						}
						inputInstrnObj.setReg2(regup2);
						inputInstrnObj.setInstruction(inputInstrnObj.getIntrnNum()+operation+", "+regName+", "+regName2+", "+instruction[3].trim());
					
						inputInstrnObj.setLiteralValue(new Integer(instruction[3].trim().replaceAll("#", "")));
						robObj.setArchDestReg(null);
						robObj.setDestPhyReg(null);
						robObj.setInstrnObj(inputInstrnObj);
						inputInstrnObj.setRobIndex(robObj.getTail());
						lsqObj.setInputInstrn(inputInstrnObj);
					}else {
						stallInstructionInDecode = true;
					}
				}else if(operation.equalsIgnoreCase("BZ") || operation.equalsIgnoreCase("BNZ")) {
					
					if(!iqStage.isIssueQueueFull() && !robObj.isRObFull()) {
						String tempString = instruction[1].trim();
						if(tempString.contains("#")) {
							inputInstrnObj.setLiteralValue(new Integer(tempString.replaceAll("#", "")));
						}
						Map<String, RegisterCustom> tempArchPhyRegMap = new HashMap<String, RegisterCustom> ();
						tempArchPhyRegMap.putAll(phyRegFile.getPhysicalRegisterMap());
						inputInstrnObj.physicalRegisterMapControlFlowInstrn = tempArchPhyRegMap;
						
						Map<String, RegisterCustom> tempRenTableMap = new HashMap<String, RegisterCustom> ();
						tempRenTableMap.putAll(renameTable);
						inputInstrnObj.archToPhyRenTblControlFlowInstrn = tempRenTableMap;
						
						
						TreeMap<Integer, Integer> memMap = new TreeMap<Integer, Integer>();
						memMap.putAll(mem.getMemoryDataMap());
						inputInstrnObj.memMap = memMap;
						
						robObj.setArchDestReg(null);
						robObj.setDestPhyReg(null);
						robObj.setInstrnObj(inputInstrnObj);
						inputInstrnObj.setRobIndex(robObj.getTail());
					}else {
						stallInstructionInDecode = true;						
					}
				}else if(operation.equalsIgnoreCase("JUMP")) {				
					
					if(!iqStage.isIssueQueueFull() && !robObj.isRObFull()) {
						reg1 = arcRegisterMap.get(instruction[1].trim());
						if(renameTable.containsKey(reg1.getRegisterName())) {
							String phyRegName = renameTable.get(reg1.getRegisterName()).getRegisterName();
							reg1 = phyRegisterMap.get(phyRegName);
						}
							
						String regName = reg1.getRegisterName();
						RegisterCustom regup = new RegisterCustom();
						regup.setRegisterName(regName);
						regup.setValidStatus(reg1.getValidStatus());
						if(reg1.getData() != null) {
							regup.setData(reg1.getData());
						}
						inputInstrnObj.setReg1(regup);
					
						String tempString = instruction[2].trim().replaceAll(",", "");
						if(tempString.contains("#")) {
							inputInstrnObj.setLiteralValue(new Integer(tempString.replaceAll("#", "")));
						}
						inputInstrnObj.setInstruction(inputInstrnObj.getIntrnNum()+operation+", "+regName+", "+tempString);
						
						Map<String, RegisterCustom> tempArchPhyRegMap = new HashMap<String, RegisterCustom> ();
						tempArchPhyRegMap.putAll(phyRegFile.getPhysicalRegisterMap());
						inputInstrnObj.physicalRegisterMapControlFlowInstrn = tempArchPhyRegMap;
						
						Map<String, RegisterCustom> tempRenTableMap = new HashMap<String, RegisterCustom> ();
						tempRenTableMap.putAll(renameTable);
						inputInstrnObj.archToPhyRenTblControlFlowInstrn = tempRenTableMap;
						
						TreeMap<Integer, Integer> memMap = new TreeMap<Integer, Integer>();
						memMap.putAll(mem.getMemoryDataMap());
						inputInstrnObj.memMap = memMap;
						
						robObj.setArchDestReg(null);
						robObj.setDestPhyReg(null);
						robObj.setInstrnObj(inputInstrnObj);
						inputInstrnObj.setRobIndex(robObj.getTail());
					}else {
						stallInstructionInDecode = true;						
					}
				}else {
				
					int arrLength = instruction.length;
					int count = 2;
					boolean firstSrcRegSet = false;
					boolean stallIfLoad = false;
					String regName1 = "";
					String regName2 = "";
					
					if(operation.equalsIgnoreCase("LOAD") && lsqObj.isLSQFull()) {
						stallIfLoad = true;
						stallInstructionInDecode = true;
					}
					
					if(phyRegFile.isPhysicalRegisterAvailable() && !robObj.isRObFull() && !iqStage.isIssueQueueFull() && !stallIfLoad) {
					
						while(count < arrLength)
						{
							String tempString = instruction[count].trim().replaceAll(",", "");
							if(tempString.contains("#")) {
								inputInstrnObj.setLiteralValue(new Integer(tempString.replaceAll("#", "")));
							}else {				
								
								if(!firstSrcRegSet) {
									reg1 = arcRegisterMap.get(instruction[count].trim());
									if(renameTable.containsKey(reg1.getRegisterName())) {
										String phyRegName = renameTable.get(reg1.getRegisterName()).getRegisterName();
										reg1 = phyRegisterMap.get(phyRegName);
									}
										
									regName1 = reg1.getRegisterName();
									RegisterCustom regup1 = new RegisterCustom();
									regup1.setRegisterName(regName1);
									regup1.setValidStatus(reg1.getValidStatus());
									if(reg1.getData() != null) {
										regup1.setData(reg1.getData());
									}
									inputInstrnObj.setReg1(regup1);
									firstSrcRegSet = true;
								}else {
									reg2 = arcRegisterMap.get(instruction[count].trim());
									if(renameTable.containsKey(reg2.getRegisterName())) {
										String phyRegName = renameTable.get(reg2.getRegisterName()).getRegisterName();
										reg2 = phyRegisterMap.get(phyRegName);
									}
										
									regName2 = reg2.getRegisterName();
									RegisterCustom regup2 = new RegisterCustom();
									regup2.setRegisterName(regName2);
									regup2.setValidStatus(reg2.getValidStatus());
									if(reg2.getData() != null) {
										regup2.setData(reg2.getData());
									}
									inputInstrnObj.setReg2(regup2);								
								}
							}
							count++;						
						}

						String archDestReg = instruction[1].trim().replaceAll(",", "");
						destReg = phyRegFile.getAvailablePhysicalRegister();
						destReg.setAllocatedStatusBit(1);
						destReg.setValidStatus(1);
						destReg.setArchRegNameForPhyReg(archDestReg);
						
						renameTable.put(archDestReg, destReg);
						inputInstrnObj.setRobIndex(robObj.getTail());
						RegisterCustom regup = new RegisterCustom();
						regup.setRegisterName(destReg.getRegisterName());
						regup.setValidStatus(destReg.getValidStatus());
						regup.setPhysicalRegister(true);
						inputInstrnObj.setDestReg(regup);
						
						String resetInstruction = inputInstrnObj.getIntrnNum()+operation+", "+destReg.getRegisterName();
						if(!regName1.equalsIgnoreCase("")) {
							resetInstruction += ", "+regName1;
						}
						if(!regName2.equalsIgnoreCase("")) {
							resetInstruction += ", "+regName2;							
						}
						if(inputInstrnObj.getLiteralValue() != null) {
							resetInstruction += ", #"+inputInstrnObj.getLiteralValue();
						}
						inputInstrnObj.setInstruction(resetInstruction);
						robObj.setArchDestReg(archDestReg);
						robObj.setDestPhyReg(destReg);
						robObj.setInstrnObj(inputInstrnObj);
						if(operation.equalsIgnoreCase("LOAD")) {
							lsqObj.setInputInstrn(inputInstrnObj);
						}
						if(operation.equalsIgnoreCase("JAL")) {
							Map<String, RegisterCustom> tempArchPhyRegMap = new HashMap<String, RegisterCustom> ();
							tempArchPhyRegMap.putAll(phyRegFile.getPhysicalRegisterMap());
							inputInstrnObj.physicalRegisterMapControlFlowInstrn = tempArchPhyRegMap;
							
							Map<String, RegisterCustom> tempRenTableMap = new HashMap<String, RegisterCustom> ();
							tempRenTableMap.putAll(renameTable);
							inputInstrnObj.archToPhyRenTblControlFlowInstrn = tempRenTableMap;
							
							
							TreeMap<Integer, Integer> memMap = new TreeMap<Integer, Integer>();
							memMap.putAll(mem.getMemoryDataMap());
							inputInstrnObj.memMap = memMap;
						}
					}else {
						stallInstructionInDecode = true;
					}						
				}
				decodeStage.setInputInstrn(inputInstrnObj);
				decodeStage.setOutputInstrn(inputInstrnObj);
				if(stallInstructionInDecode){
					return isFlushed;
				}
				iqStage.setInputInstrn(inputInstrnObj);
				decodeStage.setStalled(false);
				decodeStage.setInputInstrn(defaultInstrnObj);
			}else {
				decodeStage.setOutputInstrn(defaultInstrnObj);
				iqStage.setInputInstrn(defaultInstrnObj);
			}
			return isFlushed;
		}
	}
	
	boolean executeROB() {
		boolean isAddedToRob = false;
		if(robObj.getInstrnObj() != null && !robObj.getInstrnObj().getInstruction().equalsIgnoreCase("NOP")) {
			String archDestReg = robObj.getArchDestReg();
			RegisterCustom destPhyReg = robObj.getDestPhyReg();
			Integer branchIdIndex = robObj.getBranchIdIndex();
			InstructionInfo inputInstrnObj = robObj.getInstrnObj();
			if(!robObj.isRObFull() && inputInstrnObj != null) {
				int robIndex = robObj.addElemToROB(archDestReg, destPhyReg, inputInstrnObj, branchIdIndex);
				robObj.setArchDestReg("");
				robObj.setDestPhyReg(null);
				robObj.setBranchIdIndex(null);
				robObj.setInstrnObj(null);
			}
		}
		return isAddedToRob;
	}
	
	boolean issueInstructionToQueue(Map<Integer, InstructionInfoFromFile> instrnFileMap) 
	{
		boolean isFlushed = false;
		InstructionInfo inputInstrnObj = iqStage.getInputInstrn();				
		Map<String, RegisterCustom> phyRegisterMap = phyRegFile.getPhysicalRegisterMap();
		IQElement[] iqElements = iqStage.issueQueueElementsList;
		

		// shifting the IQ.
		// checking up for the registers and setting the iq element
		for(int i = 15; i >= 0; i--) {
			IQElement iqElem = iqElements[i];
			if(iqElem.getAllocatedStatusBit() == 0) {
				int j = i-1;
				while(j >= 0) {
					IQElement updatedIQElem = iqElements[j];
					if(updatedIQElem.getAllocatedStatusBit() != 0) {
						iqElem.setAllocatedStatusBit(updatedIQElem.getAllocatedStatusBit());
						if(updatedIQElem.getSrcRegister1()!=null) {
							iqElem.setSrcRegister1(updatedIQElem.getSrcRegister1());
							iqElem.getSrcRegister1().setValidStatus(updatedIQElem.getSrcRegister1().getValidStatus());
						}else {
							iqElem.setSrcRegister1(null);
						}
						if(updatedIQElem.getSrcRegister2()!=null) {
							iqElem.setSrcRegister2(updatedIQElem.getSrcRegister2());
							iqElem.getSrcRegister2().setValidStatus(updatedIQElem.getSrcRegister2().getValidStatus());
						}else {
							iqElem.setSrcRegister2(null);
						}
						iqElem.setCycleNumber(updatedIQElem.getCycleNumber());
						iqElem.setDestRegister(updatedIQElem.getDestRegister());
						iqElem.setIqInstrnObj(updatedIQElem.getIqInstrnObj());
						iqElem.setLiteralValue(updatedIQElem.getLiteralValue());
						iqElem.setOpCode(updatedIQElem.getOpCode());
						if(updatedIQElem.getBranchOnZeroFlag() != null && updatedIQElem.getBranchOnZeroFlag()) {
							iqElem.setBranchOnZeroFlag(updatedIQElem.getBranchOnZeroFlag());
							iqElem.setZeroFalgValue(updatedIQElem.getZeroFalgValue());
						}						
						updatedIQElem = new IQElement();
						updatedIQElem.setAllocatedStatusBit(0);
						iqElements[j] = updatedIQElem;
						break;
					}
					j--;
				}
				
				
			}
		}
		
		
		// checking up for the registers and setting the iq element
		boolean elementInserted = false;
		for(int i = 15; i >= 0; i--) {
			IQElement iqElem = iqElements[i];
			if(iqElem.getAllocatedStatusBit() != 0) {

				if(iqElem.getOpCode().equalsIgnoreCase("BZ") || iqElem.getOpCode().equalsIgnoreCase("BNZ")) {
					iqElem.setBranchOnZeroFlag(true);
					String depInstrn = instrnFileMap.get(iqElem.getIqInstrnObj().getProgramCounterValue()).getBranchDependenceOnInstrn();
					for(Map.Entry<Integer, InstructionInfoFromFile> instrnMap : instrnFileMap.entrySet()) 
					{
						if(instrnMap.getValue().getInstruction().contains(depInstrn) && instrnMap.getValue().getZeroFlagSet() != null) {
							iqElem.setZeroFalgValue(instrnMap.getValue().getZeroFlagSet());
						}
					}
				}
				
				if(iqElem.getSrcRegister1() != null) {
					if(iqElem.getSrcRegister1().getValidStatus() != 0) {
						if(phyRegisterMap.get(iqElem.getSrcRegister1().getRegisterName()).getValidStatus() == 0) {
							iqElem.getSrcRegister1().setPhysicalRegister(true);
							iqElem.getSrcRegister1().setData(phyRegisterMap.get(iqElem.getSrcRegister1().getRegisterName()).getData());
							iqElem.getSrcRegister1().setValidStatus(0);
							iqElem.setValidBitSrcRegister1(0);
							iqElem.getIqInstrnObj().setReg1(iqElem.getSrcRegister1());
						}
					}
				}
				
				if(iqElem.getSrcRegister2() != null) {
					if(iqElem.getSrcRegister2().getValidStatus() != 0) {
						if(phyRegisterMap.get(iqElem.getSrcRegister2().getRegisterName()).getValidStatus() == 0) {
							iqElem.getSrcRegister2().setPhysicalRegister(true);
							iqElem.getSrcRegister2().setData(phyRegisterMap.get(iqElem.getSrcRegister2().getRegisterName()).getData());
							iqElem.getSrcRegister2().setValidStatus(0);
							iqElem.setValidBitSrcRegister2(0);
							iqElem.getIqInstrnObj().setReg2(iqElem.getSrcRegister2());
						}
					}
				}
			}else {
				if(!inputInstrnObj.getInstruction().equalsIgnoreCase("NOP") && !elementInserted) {
					elementInserted = true;
					iqElem.setIqInstrnObj(inputInstrnObj);
					iqElem.setOpCode(inputInstrnObj.getOpCode());
					iqElem.setAllocatedStatusBit(1);
					if(iqElem.getOpCode().equalsIgnoreCase("BZ") || iqElem.getOpCode().equalsIgnoreCase("BNZ")) {
						iqElem.setBranchOnZeroFlag(false);
						String depInstrn = instrnFileMap.get(iqElem.getIqInstrnObj().getProgramCounterValue()).getBranchDependenceOnInstrn();
						for(Map.Entry<Integer, InstructionInfoFromFile> instrnMap : instrnFileMap.entrySet()) 
						{
							if(instrnMap.getValue().getInstruction().contains(depInstrn) && instrnMap.getValue().getZeroFlagSet() != null) {
								iqElem.setBranchOnZeroFlag(true);
								iqElem.setZeroFalgValue(instrnMap.getValue().getZeroFlagSet());
							}
						}
					}
					
					if(inputInstrnObj.getReg1() != null) {
						iqElem.setSrcRegister1(inputInstrnObj.getReg1());
						if(iqElem.getSrcRegister1().getValidStatus() != 0) {
							if(phyRegisterMap.get(iqElem.getSrcRegister1().getRegisterName()).getValidStatus() == 0) {
								iqElem.getSrcRegister1().setPhysicalRegister(true);
								iqElem.getSrcRegister1().setData(phyRegisterMap.get(iqElem.getSrcRegister1().getRegisterName()).getData());
								iqElem.getSrcRegister1().setValidStatus(0);
								iqElem.setValidBitSrcRegister1(0);
								iqElem.getIqInstrnObj().setReg1(iqElem.getSrcRegister1());
							}
						}else {
							iqElem.setValidBitSrcRegister1(0);
						}
					}
					
					if(inputInstrnObj.getReg2() != null) {
						iqElem.setSrcRegister2(inputInstrnObj.getReg2());
						if(iqElem.getSrcRegister2().getValidStatus() != 0) {
							if(phyRegisterMap.get(iqElem.getSrcRegister2().getRegisterName()).getValidStatus() == 0) {
								iqElem.getSrcRegister2().setPhysicalRegister(true);
								iqElem.getSrcRegister2().setData(phyRegisterMap.get(iqElem.getSrcRegister2().getRegisterName()).getData());
								iqElem.getSrcRegister2().setValidStatus(0);
								iqElem.setValidBitSrcRegister2(0);
								iqElem.getIqInstrnObj().setReg2(iqElem.getSrcRegister2());
							}else {
								iqElem.setValidBitSrcRegister2(0);
							}
						}
					}
					
					if(inputInstrnObj.getLiteralValue() != null) {
						iqElem.setLiteralValue(inputInstrnObj.getLiteralValue());
					}

					if(inputInstrnObj.getDestReg() != null) {
						iqElem.setDestRegister(inputInstrnObj.getDestReg());						
					}
				}
			}
		}
		iqStage.setInputInstrn(defaultInstrnObj);
		iqStage.setOutputInstrn(inputInstrnObj);
		return isFlushed;
	}
	
	
	boolean instructionReadByIntFU()
	{
		IQElement[] iqElements = iqStage.issueQueueElementsList;
		for(int i = 15; i >= 0; i--) {
			IQElement iqElem = iqElements[i];
			if(iqElem.getAllocatedStatusBit() == 1) {
				if(!iqElem.getOpCode().equalsIgnoreCase("MUL") && !iqElem.getOpCode().equalsIgnoreCase("DIV")) {
					
					if(iqElem.getOpCode().equalsIgnoreCase("BZ")||iqElem.getOpCode().equalsIgnoreCase("BNZ")) {
						if(iqElem.getZeroFalgValue() != null && iqElem.getBranchOnZeroFlag() != null && iqElem.getBranchOnZeroFlag()) {
							if(!executeAddStage.isStalled()) {
								if(iqElem.getZeroFalgValue() == 0) {
									iqElem.getIqInstrnObj().setZeroFlagForBzBnz("zero");									
								}else {
									iqElem.getIqInstrnObj().setZeroFlagForBzBnz("nonZero");
								}
								executeAddStage.setInputInstrn(iqElem.getIqInstrnObj());
								iqElem = new IQElement();
								iqElem.setAllocatedStatusBit(0);
								iqElements[i] = iqElem;
								break;
							}else {
								break;
							}
						}else {
							continue;
						}
					}
					
					
					if(iqElem.getSrcRegister1() != null && iqElem.getSrcRegister2() != null) {
						if(iqElem.getSrcRegister1().getValidStatus() == 0 && iqElem.getSrcRegister2().getValidStatus() == 0) {
							if(!executeAddStage.isStalled()) {
								iqElem.getIqInstrnObj().setReg1(iqElem.getSrcRegister1());
								iqElem.getIqInstrnObj().setReg2(iqElem.getSrcRegister2());
								executeAddStage.setInputInstrn(iqElem.getIqInstrnObj());
								iqElem = new IQElement();
								iqElem.setAllocatedStatusBit(0);
								iqElements[i] = iqElem;
								break;
							}else {
								break;
							}
						}else {
							continue;
						}
					}else if(iqElem.getSrcRegister1() != null) {
						if(iqElem.getSrcRegister1().getValidStatus() == 0) {
							if(!executeAddStage.isStalled()) {
								iqElem.getIqInstrnObj().setReg1(iqElem.getSrcRegister1());
								executeAddStage.setInputInstrn(iqElem.getIqInstrnObj());
								iqElem = new IQElement();
								iqElem.setAllocatedStatusBit(0);
								iqElements[i] = iqElem;								
								break;
							}else {
								break;
							}
						}else {
							continue;
						}
					}else {
						if(!executeAddStage.isStalled()) {
							executeAddStage.setInputInstrn(iqElem.getIqInstrnObj());
							iqElem = new IQElement();
							iqElem.setAllocatedStatusBit(0);
							iqElements[i] = iqElem;								
							break;
						}else {
							break;
						}
					}
				}
			}
		}
		return true;
	}

	
	boolean instructionReadByMulFU(){
		IQElement[] iqElements = iqStage.issueQueueElementsList;
		for(int i = 15; i >= 0; i--) {
			IQElement iqElem = iqElements[i];
			if(iqElem.getAllocatedStatusBit() == 1) {
				if(iqElem.getOpCode().equalsIgnoreCase("MUL")) {
					if(iqElem.getSrcRegister1() != null && iqElem.getSrcRegister2() != null) {
						if(iqElem.getSrcRegister1().getValidStatus() == 0 && iqElem.getSrcRegister2().getValidStatus() == 0) {
							if(!executeMulStage1.isStalled()) {
								iqElem.getIqInstrnObj().setReg1(iqElem.getSrcRegister1());
								iqElem.getIqInstrnObj().setReg2(iqElem.getSrcRegister2());
								executeMulStage1.setInputInstrn(iqElem.getIqInstrnObj());
								iqElem = new IQElement();
								iqElem.setAllocatedStatusBit(0);
								iqElements[i] = iqElem;								
								break;
							}
						}
					}	
				}
			}
		}
		return true;
	}
	
	
	boolean instructionReadByDivFU(){
		IQElement[] iqElements = iqStage.issueQueueElementsList;
		for(int i = 15; i >= 0; i--) {
			IQElement iqElem = iqElements[i];
			if(iqElem.getAllocatedStatusBit() == 1) {
				if(iqElem.getOpCode().equalsIgnoreCase("DIV")) {
					if(iqElem.getSrcRegister1() != null && iqElem.getSrcRegister2() != null) {
						if(iqElem.getSrcRegister1().getValidStatus() == 0 && iqElem.getSrcRegister2().getValidStatus() == 0) {
							if(!executeDivStage1.isStalled()) {
								iqElem.getIqInstrnObj().setReg1(iqElem.getSrcRegister1());
								iqElem.getIqInstrnObj().setReg2(iqElem.getSrcRegister2());
								executeDivStage1.setInputInstrn(iqElem.getIqInstrnObj());
								iqElem = new IQElement();
								iqElem.setAllocatedStatusBit(0);
								iqElements[i] = iqElem;									
								break;
							}
						}
					}			
				}
			}
		}
		return true;
	}
	
	
	boolean forwardToPhysicalRegFile(InstructionInfo inputInstrnObj) 
	{
		RegisterCustom destReg = inputInstrnObj.getDestReg();
		if(inputInstrnObj.getResult() != null) {
			if(phyRegFile.getPhysicalRegisterMap().containsKey(destReg.getRegisterName())) {
				RegisterCustom phyReg = phyRegFile.getPhysicalRegisterMap().get(destReg.getRegisterName());
				if(inputInstrnObj.getOpCode().equalsIgnoreCase("LOAD")) {
					phyReg.setData(inputInstrnObj.getLoadValueFromMemory());
				}else {
					phyReg.setData(inputInstrnObj.getResult());
				}
				phyReg.setValidStatus(0);
				for(RobElement robElem: robObj.getRobElements()) {
					if(robElem != null && robElem.getAllocStatCode() != null && robElem.getAllocStatCode() == 1) {
						if(robElem.getDestRegisterName() != null && robElem.getDestRegisterName().equalsIgnoreCase(destReg.getRegisterName())) {
							robElem.setHasCorrectValue(true);
						}
					}
				}
			}
		}
		return true;
	}
	
	
	boolean execute(Map<Integer, InstructionInfoFromFile> instrnFileMap) 
	{
		boolean isFlushed = false;
		executeDivStage4(instrnFileMap);
		executeMulStage2(instrnFileMap);
		isFlushed = executeAddStage(instrnFileMap);
		
		if(!executeDivStage4.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP")){
			
			forwardToPhysicalRegFile(executeDivStage4.getOutputInstrn());
			executeDivStage4.setInputInstrn(defaultInstrnObj);
		}
		if(!executeMulStage2.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP")){
			
			forwardToPhysicalRegFile(executeMulStage2.getOutputInstrn());
			executeMulStage2.setInputInstrn(defaultInstrnObj);
		}
		if(!executeAddStage.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP")){
			
			if(executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("LOAD") ||
					executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("STORE")) {
				lsqByPassing(executeAddStage.getOutputInstrn().getCycleCount(), executeAddStage.getOutputInstrn().getResult());
				//forwardAddressToLSQ(executeAddStage.getOutputInstrn());
			}if(executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("BZ") ||
					executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("BNZ")||
					executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("JUMP")) {
					for(RobElement robElem: robObj.getRobElements())
					{
							if(robElem != null && robElem.getAllocStatCode() != null && robElem.getAllocStatCode() == 1) {
								if(robElem.getInstrnObj().getCycleCount() == executeAddStage.getOutputInstrn().getCycleCount()) {
									robElem.setHasCorrectValue(true);
								}
							}
					}				
			}else if(executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("ADD")||
						executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("SUB")||
						executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("SUB")||
						executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("AND")||
						executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("OR")||
						executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("EXOR")||
						executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("MOVC")||
						executeAddStage.getOutputInstrn().getOpCode().equalsIgnoreCase("JAL")){
				forwardToPhysicalRegFile(executeAddStage.getOutputInstrn());
			}
			executeAddStage.setInputInstrn(defaultInstrnObj);
			isExecuteAddStageCalculated = false;	
		}
		executeDivStage3();
		executeDivStage2();
		executeDivStage1();
		executeMulStage1();
		return isFlushed;
	}
	
	
	void executeDivStage4(Map<Integer, InstructionInfoFromFile> instrnFileMap) 
	{
		InstructionInfo instrnObj = executeDivStage4.getInputInstrn();
		if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
			if(instrnObj.isLatestBranchArithmeticInstn()){
				InstructionInfoFromFile tempInstrnObj = instrnFileMap.get(instrnObj.getProgramCounterValue());
				if(instrnObj.getResult() == 0)
					tempInstrnObj.setZeroFlagSet(0);
				else
					tempInstrnObj.setZeroFlagSet(1);
			}
			executeDivStage4.setOutputInstrn(instrnObj);
		}else {
			executeDivStage4.setOutputInstrn(defaultInstrnObj);				
		}
		return;
	}	
	
	
	void executeDivStage3() 
	{
		InstructionInfo instrnObj = executeDivStage3.getInputInstrn();
		if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
			executeDivStage3.setInputInstrn(instrnObj);
			executeDivStage3.setOutputInstrn(instrnObj);
		}else {
			executeDivStage3.setOutputInstrn(defaultInstrnObj);				
		}
		if(!executeDivStage4.isStalled()) {
			executeDivStage4.setInputInstrn(instrnObj);
			executeDivStage3.setInputInstrn(defaultInstrnObj);
		}
		return;
	}	
	
	void executeDivStage2() 
	{
		InstructionInfo instrnObj = executeDivStage2.getInputInstrn();
		if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
			executeDivStage2.setInputInstrn(instrnObj);
			executeDivStage2.setOutputInstrn(instrnObj);
		}else {
			executeDivStage2.setOutputInstrn(defaultInstrnObj);				
		}
		if(!executeDivStage3.isStalled()) {
			executeDivStage3.setInputInstrn(instrnObj);
			executeDivStage2.setInputInstrn(defaultInstrnObj);
		}
		return;
	}	
	
	
	void executeDivStage1() 
	{
		InstructionInfo instrnObj = executeDivStage1.getInputInstrn();
		if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
			int result = 0;			
			result = instrnObj.getReg1().getData() / instrnObj.getReg2().getData();
			instrnObj.setResult(result);			
			executeDivStage1.setInputInstrn(instrnObj);
			executeDivStage1.setOutputInstrn(instrnObj);
		}else {
			executeDivStage1.setOutputInstrn(defaultInstrnObj);				
		}
		if(!executeDivStage2.isStalled()) {
			executeDivStage2.setInputInstrn(instrnObj);
			executeDivStage1.setInputInstrn(defaultInstrnObj);
		}
		return;
	}
	
	void executeMulStage2(Map<Integer, InstructionInfoFromFile> instrnFileMap) 
	{

		InstructionInfo instrnObj = executeMulStage2.getInputInstrn();
		if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
			if(instrnObj.isLatestBranchArithmeticInstn()){
				InstructionInfoFromFile tempInstrnObj = instrnFileMap.get(instrnObj.getProgramCounterValue());
				if(instrnObj.getResult() == 0)
					tempInstrnObj.setZeroFlagSet(0);
				else
					tempInstrnObj.setZeroFlagSet(1);
			}
			executeMulStage2.setOutputInstrn(instrnObj);
		}else {
			executeMulStage2.setOutputInstrn(defaultInstrnObj);				
		}
		return;
	}
	
	
	void executeMulStage1() 
	{
		InstructionInfo instrnObj = executeMulStage1.getInputInstrn();
		if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
			int result = 1;			
			result = instrnObj.getReg1().getData() * instrnObj.getReg2().getData();
			instrnObj.setResult(result);
			executeMulStage1.setInputInstrn(instrnObj);
			executeMulStage1.setOutputInstrn(instrnObj);
		}else {
			executeMulStage1.setOutputInstrn(defaultInstrnObj);				
		}
		if(!executeMulStage2.isStalled()) {
			executeMulStage2.setInputInstrn(instrnObj);
			executeMulStage1.setInputInstrn(defaultInstrnObj);
		}
		return;
	}
	
	
	boolean executeAddStage(Map<Integer, InstructionInfoFromFile> instrnFileMap) 
	{
		boolean isFlushed = false;
		InstructionInfo instrnObj = executeAddStage.getInputInstrn();
		if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
			int result = 0;
			if(instrnObj.getOpCode().equalsIgnoreCase("ADD")) {
				
				result = instrnObj.getReg1().getData() + instrnObj.getReg2().getData();
				instrnObj.setResult(result);
			}else if(instrnObj.getOpCode().equalsIgnoreCase("SUB")) {
				
				result = instrnObj.getReg1().getData() - instrnObj.getReg2().getData();
				instrnObj.setResult(result);
			}else if(instrnObj.getOpCode().equalsIgnoreCase("AND")) {
				
				result = instrnObj.getReg1().getData() & instrnObj.getReg2().getData();
				instrnObj.setResult(result);
			}else if(instrnObj.getOpCode().equalsIgnoreCase("OR")) {
				
				result = instrnObj.getReg1().getData() | instrnObj.getReg2().getData();
				instrnObj.setResult(result);
			}else if(instrnObj.getOpCode().equalsIgnoreCase("EXOR")) {
				
				result = instrnObj.getReg1().getData() ^ instrnObj.getReg2().getData();
				instrnObj.setResult(result);
			}else if(instrnObj.getOpCode().equalsIgnoreCase("MOVC")) {	
									
				instrnObj.setResult(instrnObj.getLiteralValue() + 0);
			}else if(instrnObj.getOpCode().equalsIgnoreCase("LOAD")) {	
				
				instrnObj.setResult(instrnObj.getReg1().getData() + instrnObj.getLiteralValue());
			}else if(instrnObj.getOpCode().equalsIgnoreCase("STORE")) {	
				
				instrnObj.setResult(instrnObj.getReg2().getData() + instrnObj.getLiteralValue());
			}else if(instrnObj.getOpCode().equalsIgnoreCase("BZ") && !isExecuteAddStageCalculated) {
				if(instrnObj.getZeroFlagForBzBnz() != null) {					
					if(instrnObj.getZeroFlagForBzBnz().equalsIgnoreCase("zero")) {
						pcAddress = instrnObj.getProgramCounterValue() + instrnObj.getLiteralValue();
						isFlushed = true;
						isExecuteAddStageCalculated = true;
						flushInstructionsInAllStagesForBranches(instrnObj.getCycleCount(), instrnObj);
					}
				}
			}else if(instrnObj.getOpCode().equalsIgnoreCase("BNZ") && !isExecuteAddStageCalculated) {
				if(instrnObj.getZeroFlagForBzBnz() != null) {
					if(instrnObj.getZeroFlagForBzBnz().equalsIgnoreCase("nonZero")) {
						pcAddress = instrnObj.getProgramCounterValue() + instrnObj.getLiteralValue();
						isFlushed = true;
						isExecuteAddStageCalculated = true;
						flushInstructionsInAllStagesForBranches(instrnObj.getCycleCount(), instrnObj);
					}
				}		
			}else if(instrnObj.getOpCode().equalsIgnoreCase("JUMP") && !isExecuteAddStageCalculated) {
				pcAddress = instrnObj.getReg1().getData() + instrnObj.getLiteralValue();
				isFlushed = true;
				isExecuteAddStageCalculated = true;
				flushInstructionsInAllStagesForBranches(instrnObj.getCycleCount(), instrnObj);
			}else if(instrnObj.getOpCode().equalsIgnoreCase("JAL") && !isExecuteAddStageCalculated) {
				pcAddress = instrnObj.getReg1().getData() + instrnObj.getLiteralValue();
				instrnObj.setResult(instrnObj.getNextInstructionPCValue());
				isFlushed = true;
				isExecuteAddStageCalculated = true;
				flushInstructionsInAllStagesForBranches(instrnObj.getCycleCount(), instrnObj);
			}
			executeAddStage.setInputInstrn(instrnObj);
			if(instrnObj.getOpCode().equalsIgnoreCase("ADD")||instrnObj.getOpCode().equalsIgnoreCase("SUB")
					||instrnObj.getOpCode().equalsIgnoreCase("AND")||instrnObj.getOpCode().equalsIgnoreCase("OR")
					||instrnObj.getOpCode().equalsIgnoreCase("EXOR")) {
				if(instrnObj.isLatestBranchArithmeticInstn()){
					InstructionInfoFromFile tempInstrnObj = instrnFileMap.get(instrnObj.getProgramCounterValue());
					if(instrnObj.getResult() == 0)
						tempInstrnObj.setZeroFlagSet(0);
					else
						tempInstrnObj.setZeroFlagSet(1);
				}
			}
			executeAddStage.setOutputInstrn(instrnObj);
		}else {
			executeAddStage.setOutputInstrn(defaultInstrnObj);
		}
		return isFlushed;
	}
	
	void lsqByPassing(Integer cycleNumber, Integer memoryAddress) 
	{
		LsqElement []lsqelems = lsqObj.lsqElemList;
		Integer currentUpdatedLsqIndex = null;
		Integer valueFromStore = null;
		boolean loadAddressMatched = false;
		boolean loadInstrnForwarded = false;
		boolean allMemAddressCalculatedForStore = true;
		Map<Integer, Integer> memoryDataMap = mem.getMemoryDataMap();
		
		for(int i = 0; i < 32; i++) {
			if(lsqelems[i].getAllocatedStatusBit() == 1 && lsqelems[i].getInstrnObj().getCycleCount() == cycleNumber) {
				currentUpdatedLsqIndex = i;
				lsqelems[i].setMemoryAddress(memoryAddress);
				lsqelems[i].getInstrnObj().setResult(memoryAddress);
				lsqelems[i].getInstrnObj().setLoadValueFromMemory(memoryDataMap.get(Memory.memoryBaseAddress + memoryAddress));
				lsqObj.byPassLSQIndex = i;
				break;
			}
		}
	}
	
	
	void shiftLSQ() {

		LsqElement []lsqelems = lsqObj.lsqElemList;
		// shifting the LSQ and checking up for the registers and setting the iq element
		for(int i = 31; i >= 0; i--) {
			LsqElement lsqelem = lsqelems[i];
			if(lsqelem.getAllocatedStatusBit() == 0) {
				int j = i-1;
				while(j >= 0) {
					LsqElement updatedLsqElem = lsqelems[j];
					if(updatedLsqElem.getAllocatedStatusBit() != 0) {
						lsqelem.setAllocatedStatusBit(updatedLsqElem.getAllocatedStatusBit());
						if(updatedLsqElem.getSrcRegister1 ()!= null) {
							lsqelem.setSrcRegister1(updatedLsqElem.getSrcRegister1());
						}else {
							lsqelem.setSrcRegister1(null);
						}
						lsqelem.setDestRegister(updatedLsqElem.getDestRegister());
						lsqelem.setInstrnObj(updatedLsqElem.getInstrnObj());
						lsqelem.setMemoryAddress(updatedLsqElem.getMemoryAddress());
						lsqelem.setInstrnOprType(updatedLsqElem.getInstrnOprType());						
						updatedLsqElem = new LsqElement();
						updatedLsqElem.setAllocatedStatusBit(0);
						lsqelems[j] = updatedLsqElem;
						break;
					}
					j--;
				}			
			}
		}		
	}
	

	void executeLSQ() 
	{
		RegisterCustom reg1 = null;
		RegisterCustom destReg = null;
		LsqElement []lsqelems = lsqObj.lsqElemList;
		InstructionInfo inputInstrnObj = lsqObj.getInputInstrn();				
		Map<String, RegisterCustom> phyRegisterMap = phyRegFile.getPhysicalRegisterMap();	
		
		
		// checking up for the registers and setting the lsq element
		boolean elementInserted = false;
		for(int i = 31; i >= 0; i--) {
			LsqElement lsqElem = lsqelems[i];
			if(lsqElem.getAllocatedStatusBit() != 0) {				
				if(lsqElem.getSrcRegister1() != null) {
					if(lsqElem.getSrcRegister1().getValidStatus() != 0) {
						if(phyRegisterMap.get(lsqElem.getSrcRegister1().getRegisterName()).getValidStatus() == 0) {
							lsqElem.getSrcRegister1().setPhysicalRegister(true);
							lsqElem.getSrcRegister1().setData(phyRegisterMap.get(lsqElem.getSrcRegister1().getRegisterName()).getData());
							lsqElem.getSrcRegister1().setValidStatus(0);
							lsqElem.getInstrnObj().setReg1(lsqElem.getSrcRegister1());
						}
					}
				}
				
			}else {
				if(!inputInstrnObj.getInstruction().equalsIgnoreCase("NOP") && !elementInserted) {
					elementInserted = true;
					lsqElem.setInstrnObj(inputInstrnObj);
					lsqElem.setInstrnOprType(inputInstrnObj.getOpCode());
					lsqElem.setAllocatedStatusBit(1);
					
					if(inputInstrnObj.getReg1() != null) {
						lsqElem.setSrcRegister1(inputInstrnObj.getReg1());
						if(lsqElem.getSrcRegister1().getValidStatus() != 0) {
							if(phyRegisterMap.get(lsqElem.getSrcRegister1().getRegisterName()).getValidStatus() == 0) {
								lsqElem.getSrcRegister1().setPhysicalRegister(true);
								lsqElem.getSrcRegister1().setData(phyRegisterMap.get(lsqElem.getSrcRegister1().getRegisterName()).getData());
								lsqElem.getSrcRegister1().setValidStatus(0);
								lsqElem.getInstrnObj().setReg1(lsqElem.getSrcRegister1());
							}
						}
					}
					if(inputInstrnObj.getDestReg() != null) {
						lsqElem.setDestRegister(inputInstrnObj.getDestReg());						
					}
					lsqObj.setInputInstrn(defaultInstrnObj);
				}
			}
		}
		
		
		Integer valueFromStore = null;
		boolean loadAddressMatched = false;
		boolean allMemAddressCalculatedForStore = true;
		boolean loadInstrnForwarded = false;

		//checking for the selected index to Bypass only for LOAD instructions and shift lsq elements if required
		if(lsqObj.byPassLSQIndex != null && lsqObj.byPassLSQIndex > -1 && lsqelems[lsqObj.byPassLSQIndex].getAllocatedStatusBit() != 0 && lsqelems[lsqObj.byPassLSQIndex].getInstrnOprType().equalsIgnoreCase("LOAD")) {
			LsqElement lsqElem = lsqObj.lsqElemList[lsqObj.byPassLSQIndex];
			for(int i = 31; i > lsqObj.byPassLSQIndex; i--) {
				if(lsqelems[i].getInstrnOprType().equalsIgnoreCase("STORE")) {
					if(lsqelems[i].getMemoryAddress() != null) {
						if(lsqelems[i].getMemoryAddress().equals(lsqElem.getMemoryAddress()) && lsqelems[i].getSrcRegister1().getValidStatus() == 0) {
							valueFromStore = lsqelems[i].getSrcRegister1().getData();
							loadAddressMatched = true;
						}
					}else {
						// memory addresses are still not calculated 
						allMemAddressCalculatedForStore = false;
						break;							
					}
				}	
			}
			if(loadAddressMatched && valueFromStore != null) {
				InstructionInfo newInputInstrnObj = lsqelems[lsqObj.byPassLSQIndex].getInstrnObj();
				inputInstrnObj.setResult(valueFromStore);
				forwardToPhysicalRegFile(newInputInstrnObj);
				LsqElement lsqElemNew=  new LsqElement();
				lsqElemNew.setAllocatedStatusBit(0);
				lsqelems[lsqObj.byPassLSQIndex] = lsqElemNew;
				loadInstrnForwarded = true;
			}
			
			// Shifting the current LSQ element to the head of the LSQ
			if(!loadInstrnForwarded && allMemAddressCalculatedForStore) {
				
				for(int i = lsqObj.byPassLSQIndex; i < 31; i++) {					
					int j = i+1;
					if(lsqelems[j].getAllocatedStatusBit() == 1) {
						if(lsqelems[j].getInstrnOprType().equalsIgnoreCase("STORE")) {
							/*if(lsqelems[j].getSrcRegister1().getValidStatus() == 0) {
							}else {*/
								LsqElement tempElem = lsqelems[j];
								lsqelems[j] = lsqelems[i];
								lsqelems[i] = tempElem;		
							/*}*/
						} else {
							
							if(lsqelems[j].getSrcRegister1().getValidStatus() != 0) {
								
								LsqElement tempElem = lsqelems[j];
								lsqelems[j] = lsqelems[i];
								lsqelems[i] = tempElem;									
							}else {
								break;
							}
						}
										
					}else {
						LsqElement tempElem = lsqelems[j];
						lsqelems[j] = lsqelems[i];
						lsqelems[i] = tempElem;
					}					
				}				
			}
			lsqObj.byPassLSQIndex = -1;
		}
		

		shiftLSQ();		
		
		
		if(!memoryStage.isStalled()) {
			LsqElement lsqHead = lsqelems[31];
			if(lsqHead.getAllocatedStatusBit() == 1 && lsqHead.getInstrnOprType().equalsIgnoreCase("LOAD")) {
				if(lsqHead.getMemoryAddress() != null) {
					memoryStage.setInputInstrn(lsqHead.getInstrnObj());
					LsqElement lsq = new LsqElement();
					lsq.setAllocatedStatusBit(0);
					lsqelems[31] = lsq;
					shiftLSQ();
				}
			}else {
				if(lsqHead.getMemoryAddress() != null && lsqHead.getInstrnObj().getReg1().getValidStatus()==0 
						&& robObj.robElements[robObj.getHead()].getInstrnObj().getCycleCount() == lsqHead.getInstrnObj().getCycleCount()) {
					memoryStage.setInputInstrn(lsqHead.getInstrnObj());
					LsqElement lsq = new LsqElement();
					lsq.setAllocatedStatusBit(0);
					lsqelems[31] = lsq;
					shiftLSQ();
				}
			}
		}
			
	}
	
	
	void memory() 
	{
		if(memoryStage.isStalled() && !memoryStage.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP")) {			
			memoryStage.getOutputInstrn().setNumOfCycles(memoryStage.getOutputInstrn().getNumOfCycles()+1);
			if(memoryStage.getOutputInstrn().getNumOfCycles() == 3) {
				if(memoryStage.getOutputInstrn().getOpCode().equalsIgnoreCase("LOAD")) {
					forwardToPhysicalRegFile(memoryStage.getOutputInstrn());
				}else {
					/*for(RobElement robElem: robObj.getRobElements()) {
						if(robElem != null && robElem.getAllocStatCode() != null && robElem.getAllocStatCode() == 1) {
							if(robElem.getInstrnObj().getCycleCount() == memoryStage.getOutputInstrn().getCycleCount()) {
								robElem.setHasCorrectValue(true);
							}
						}
					}*/
				}
				memoryStage.setInputInstrn(defaultInstrnObj);		
				//memoryStage.setOutputInstrn(defaultInstrnObj);		
				memoryStage.setStalled(false);
			}
			/*if(memoryStage.getOutputInstrn().getNumOfCycles() == 2 && memoryStage.getOutputInstrn().getOpCode().equalsIgnoreCase("STORE")) {
				for(RobElement robElem: robObj.getRobElements()) {
					if(robElem != null && robElem.getAllocStatCode() != null && robElem.getAllocStatCode() == 1) {
						if(robElem.getInstrnObj().getCycleCount() == memoryStage.getOutputInstrn().getCycleCount()) {
							robElem.setHasCorrectValue(true);
						}
					}
				}				
			}*/
			
			return;
		}
		else {

			InstructionInfo instrnObj = memoryStage.getInputInstrn();
			if(!instrnObj.getInstruction().equalsIgnoreCase("NOP")) {
				int memoryAddress = 0;
				int memoryIndex = 0;
				Map<Integer, Integer> memoryDataMap = mem.getMemoryDataMap();
				memoryStage.setStalled(true);
				if(instrnObj.getOpCode().equalsIgnoreCase("LOAD")) {
					
					memoryAddress = Memory.memoryBaseAddress + instrnObj.getResult();
					memoryIndex = memoryAddress/4;
					if(memoryDataMap.containsKey(memoryIndex*4)) {
						
						instrnObj.setLoadValueFromMemory(memoryDataMap.get(memoryIndex*4));
					}
				}else if(instrnObj.getOpCode().equalsIgnoreCase("STORE")) {
					
					int memValueToSet = instrnObj.getReg1().getData();
					memoryAddress = Memory.memoryBaseAddress + instrnObj.getResult();
					memoryIndex = memoryAddress/4;
					memoryDataMap.put(memoryIndex*4, memValueToSet);
					for(RobElement robElem: robObj.getRobElements()) {
						if(robElem != null && robElem.getAllocStatCode() != null && robElem.getAllocStatCode() == 1) {
							if(robElem.getInstrnObj().getCycleCount() == instrnObj.getCycleCount()) {
								robElem.setHasCorrectValue(true);
							}
						}
					}	
				}
				memoryStage.setInputInstrn(instrnObj);
				memoryStage.setOutputInstrn(instrnObj);			
				memoryStage.getOutputInstrn().setNumOfCycles(1);
			}else {
				memoryStage.setOutputInstrn(defaultInstrnObj);				
			}
			return;
		}
	}
	
	void commitInstruction() 
	{
		int iterationCount = 0;
		robObj.setCommittedInstrnList(new ArrayList<InstructionInfo> ());
		while(!robObj.isRobEmpty() && iterationCount < 2) {
			RobElement[] robElements = robObj.getRobElements();
			Integer data = null;
			iterationCount++;
			Integer robheadIndex = robObj.getHead();
			if(robElements[robheadIndex].getExceptionCode() != null) {
				// Write code for exception handling in future
			}
			if(robElements[robheadIndex].getAllocStatCode() == 1 && robElements[robheadIndex].isHasCorrectValue()) {
				robElements[robheadIndex].setStatusBit(0);
			}
			
			if(robElements[robheadIndex].getAllocStatCode() == 1 && robElements[robheadIndex].getInstrnOprType().equalsIgnoreCase("HALT")){	
				robObj.getCommittedInstrnList().add(robElements[robheadIndex].getInstrnObj());
				robObj.remElemFromROB();				
			}
			if(robElements[robheadIndex].getAllocStatCode() == 1 && robElements[robheadIndex].getStatusBit() == 0) {
				
				
				if(robElements[robObj.getHead()].getDestRegisterName() != null && !robElements[robObj.getHead()].getDestRegisterName().equalsIgnoreCase("")) {
				// First logic for freeing the physical register.
					String archDestReg = robElements[robObj.getHead()].getArchDestRegisterName();
					String phyRegName = robElements[robObj.getHead()].getDestRegisterName();
					//RegisterCustom phyReg = phyRegFile.getPhysicalRegisterMap().get(phyRegName);
					String mostRecentPhyRegName = renameTable.get(archDestReg).getRegisterName();
					//RegisterCustom mostRecentPhyReg = renameTable.get(archDestReg);
					for(Map.Entry<String, RegisterCustom> entry : phyRegFile.getPhysicalRegisterMap().entrySet()) {
						
						if(archDestReg.equalsIgnoreCase(entry.getValue().getArchRegNameForPhyReg()) && phyRegName.equalsIgnoreCase(entry.getKey())) {
							data = entry.getValue().getData();
						}
						boolean srcWaitngForPhyReg = checkSrcWaiting(entry.getKey());
						if(archDestReg.equalsIgnoreCase(entry.getValue().getArchRegNameForPhyReg()) && !mostRecentPhyRegName.equalsIgnoreCase(entry.getKey())
								&& (entry.getValue()).getValidStatus() == 0 && !srcWaitngForPhyReg) {
							RegisterCustom register = new RegisterCustom();
							register.setRegisterName(entry.getValue().getRegisterName());
							register.setValidStatus(0);
							register.setData(0);
							
							register.setPhysicalRegister(true);
							register.setAllocatedStatusBit(0);
							register.setArchRegNameForPhyReg("");
							entry.setValue(register);
						}
					}					
					if(data != null) {
						RegisterCustom arcReg = arcRegFile.getArcRegisterMap().get(archDestReg);
						arcReg.setData(data);
						arcReg.setValidStatus(0);
					}
				}
				robObj.getCommittedInstrnList().add(robElements[robheadIndex].getInstrnObj());
				robObj.remElemFromROB();
			}
		}
	}
	
	boolean checkSrcWaiting(String destReg)
	{
		boolean srcWaitngForPhyReg = false;
		IQElement iqElemList[] = iqStage.issueQueueElementsList;
		for(IQElement iqelem: iqElemList) 
		{
			if(iqelem.getAllocatedStatusBit() == 1) {
				if(iqelem.getSrcRegister1() != null) {
					if(iqelem.getSrcRegister1().getRegisterName().equalsIgnoreCase(destReg) && iqelem.getSrcRegister1().getValidStatus() != 0) {
						srcWaitngForPhyReg = true;
					}
				}
				if(iqelem.getSrcRegister2()!= null) {
					if(iqelem.getSrcRegister2().getRegisterName().equalsIgnoreCase(destReg) && iqelem.getSrcRegister2().getValidStatus() != 0) {
						srcWaitngForPhyReg = true;
					}					
				}
			}
		}
		return srcWaitngForPhyReg;
	}
	
	
	void restorePhysicalAndRenameTbl(InstructionInfo instrnObj)
	{
		phyRegFile.setPhysicalRegisterMap(instrnObj.physicalRegisterMapControlFlowInstrn);
		renameTable = instrnObj.archToPhyRenTblControlFlowInstrn;
	}
	
	void flushStage(Stage stage  ,Integer cycleCount)
	{
		if(!stage.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && stage.getInputInstrn().getCycleCount() > cycleCount) {
			stage.flush();
		}
	}
	
	void restoreROB(Integer cycle, Integer currentBranchingIndex) {
		if(currentBranchingIndex == 31) {
			robObj.setTail(0);
		}else {
			robObj.setTail(currentBranchingIndex+1);
		}
		for(int i = 31; i >= 0; i--) {
			RobElement robelem = robObj.getRobElements()[i];
			if(robelem.getAllocStatCode() == 1 && robelem.getInstrnObj().getCycleCount() > cycle) {
				robObj.currentSize--;
				robelem = new RobElement();
				robelem.setStatusBit(0);
				robelem.setHasCorrectValue(false);
				robelem.setAllocStatCode(0);
				robObj.robElements[i] = robelem;
			}
		}
		if(robObj.getInstrnObj() != null && !robObj.getInstrnObj().getInstruction().equalsIgnoreCase("NOP") && robObj.getInstrnObj().getCycleCount() > cycle) {
			robObj.setInstrnObj(defaultInstrnObj);
		}
	}
	
	
	
	void restoreLSQ(Integer cycle) {
		for(int i = 31; i >= 0; i--) {
			LsqElement lsqelem = lsqObj.lsqElemList[i];
			if(lsqelem.getAllocatedStatusBit() == 1) {
				if(lsqelem.getInstrnObj().getCycleCount() > cycle) {					
					lsqelem = new LsqElement();
					lsqelem.setAllocatedStatusBit(0);
					lsqObj.lsqElemList[i] = lsqelem;
				}		
			}
		}
		if(lsqObj.getInputInstrn() != null && !lsqObj.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && lsqObj.getInputInstrn().getCycleCount()>cycle) {
			lsqObj.setInputInstrn(defaultInstrnObj);
		}
		if(lsqObj.getOutputInstrn() != null && !lsqObj.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP")  && lsqObj.getOutputInstrn().getCycleCount()>cycle) {
			lsqObj.setOutputInstrn(defaultInstrnObj);
		}
	}
	
	void restoreIQ(Integer cycle) {
		for(int i = 15; i >= 0; i--) {
			IQElement iqElem = iqStage.issueQueueElementsList[i];
			if(iqElem.getAllocatedStatusBit() == 1) {
				if(iqElem.getIqInstrnObj().getCycleCount() > cycle) {						
					iqElem = new IQElement();
					iqElem.setAllocatedStatusBit(0);
					iqStage.issueQueueElementsList[i] = iqElem;
				}
			}
		}
		if(iqStage.getInputInstrn() != null && !iqStage.getInputInstrn().getInstruction().equalsIgnoreCase("NOP") && iqStage.getInputInstrn().getCycleCount()>cycle) {
			iqStage.setInputInstrn(defaultInstrnObj);
		}
		if(iqStage.getOutputInstrn() != null && !iqStage.getOutputInstrn().getInstruction().equalsIgnoreCase("NOP") && iqStage.getOutputInstrn().getCycleCount()>cycle) {
			iqStage.setOutputInstrn(defaultInstrnObj);
		}
	}
	
	
	//Removing instructions from FU's and IQ stages.
	public void flushInstructionsInAllStagesForBranches(Integer cycleCount, InstructionInfo inputInstruction) {
		flushStage(executeAddStage ,cycleCount);
		flushStage(executeMulStage1 ,cycleCount);
		flushStage(executeMulStage2 ,cycleCount);
		flushStage(executeDivStage1 ,cycleCount);
		flushStage(executeDivStage2 ,cycleCount);
		flushStage(executeDivStage3 ,cycleCount);
		flushStage(executeDivStage4 ,cycleCount);
		flushStage(memoryStage, cycleCount);
		
		restoreROB(cycleCount, inputInstruction.getRobIndex());
		restoreLSQ(cycleCount);
		restoreIQ(cycleCount);
		restorePhysicalAndRenameTbl(inputInstruction);
		decodeStage.flush();
		fetchStage.flush();
	}
	
	
	//Method to remove rob elements when the BZ, BNZ, JUMP or JAL instruction 
	/*public Set<Integer> removeROBElements(RobElement robElem)
	{
		Set<Integer> branchInstructionToRemove = new HashSet<Integer> ();
		int deleteUntilIndex = robElem.getDeleteUntilROBIndex();
		int startIndex = robObj.getHead();
		if(deleteUntilIndex < startIndex)
			deleteUntilIndex = deleteUntilIndex + robObj.getMaxSize();
		robObj.remElemFromROB();
		for(int i = startIndex+1; i <= deleteUntilIndex; i++)
		{
			RobElement robElemTemp = robObj.remElemFromROB();
			branchInstructionToRemove.add(robElemTemp.getBisIndex());
		}
		return branchInstructionToRemove;	
	}*/
	
	void readInstructionFromFile(InstructionMemory instructionFileObj, String fileName){
	    
		BufferedReader bufferedReader;
		Map<Integer, InstructionInfoFromFile> instructionListMap = new LinkedHashMap<Integer, InstructionInfoFromFile>(); 
		try {
			FileReader fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			String line = null;
			int lineNumber = 0;
			int instructionAddress = instructionBaseAddress;

			while((line = bufferedReader.readLine()) != null) {
			    InstructionInfoFromFile instrnObj = new InstructionInfoFromFile();
			    instrnObj.setInstruction("(I"+lineNumber+")"+line.trim());
			    instrnObj.setLineNumber(lineNumber);
			    instrnObj.setInstructionAddress(instructionAddress);
			    instrnObj.setLatestBranchArithmeticInstn(false);
			    instrnObj.setCompleted(false);
			    String opcode = line.split(",")[0].trim();
			    instrnObj.setOpCode(opcode);
			    if(opcode.equalsIgnoreCase("ADD")||opcode.equalsIgnoreCase("SUB")||opcode.equalsIgnoreCase("MUL")
			    		||opcode.equalsIgnoreCase("DIV")) {
			    	InstructionMemory.latestArithmeticInstructionAddress = instructionAddress;
			    	InstructionMemory.latestArithmeticInstruction = "(I"+lineNumber+")";
			    	
			    }
			    else if(opcode.equalsIgnoreCase("BZ")||opcode.equalsIgnoreCase("BNZ")) {
			    	InstructionInfoFromFile tempInstrnObj = instructionListMap.get(InstructionMemory.latestArithmeticInstructionAddress);
			    	tempInstrnObj.setLatestBranchArithmeticInstn(true);
			    	instrnObj.setBranchInstructionDependentInstrn(InstructionMemory.latestArithmeticInstructionAddress);
			    	instrnObj.setBranchDependenceOnInstrn(InstructionMemory.latestArithmeticInstruction);
			    }
			    else if(opcode.equalsIgnoreCase("JAL")) {
			    	instrnObj.nextInstructionPCValue = instructionAddress + 4;
			    }
			    instructionListMap.put(instructionAddress, instrnObj);
			    lineNumber++;
			    instructionAddress += 4;
			}
			finalInstructionAddress = instructionAddress - 4;
            bufferedReader.close(); 
		} catch (FileNotFoundException e) {
			System.out.println("File not found :"+ fileName);
		} catch (IOException e) {
			System.out.println("File cannot be read :"+ fileName);
		}
		instructionFileObj.setInstructionListMap(instructionListMap);
	}
	
	
	void readOutputFile()
	{
		try {
			FileReader fileReader = new FileReader("Output.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
			    System.out.println(line);
			}
            bufferedReader.close(); 
		} catch (FileNotFoundException e) {
			System.out.println("Result File not found, Simulate the pipeline");
		} catch (IOException e) {
			System.out.println("Result File cannot be read ");
		}
	}
}