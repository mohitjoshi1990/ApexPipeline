import java.io.File;
import java.util.Scanner;

public class Simulator {

	public static void main(String[] args) {		
		Scanner inputScanner = new Scanner(System.in);
		PipelineExecutor pipelineExecutorObj = new PipelineExecutor();
		//File file = new File("Output.txt");
		String fileName = args[0];

		String instruction = "";
		boolean pipelineInitialized = false;
		do {
			System.out.println("Enter the instruction to execute, or type exit to quit" );
			
			instruction = inputScanner.next();
			if(instruction.equalsIgnoreCase("Initialize")) {
				pipelineExecutorObj.initialize(fileName);
				File file = new File("Output.txt");
				file.delete();
				pipelineInitialized = true;
				System.out.println("System initialized successfully..");
			}else if(instruction.startsWith("Simulate")){
				if(!pipelineInitialized) {
					System.out.println("Initialize the simulator first");
				}else {
					instruction = instruction.trim();
					if(instruction.equalsIgnoreCase("Simulate") ) {
						pipelineExecutorObj.initialize(fileName);
						File file = new File("Output.txt");
						file.delete();
						pipelineExecutorObj.executeInstructionFromFile(inputScanner.nextInt(), fileName);
					}
					System.out.println("pipeline simulation complete..");
				}
			}else if(instruction.equalsIgnoreCase("Display")){
				if(!pipelineInitialized) {
					System.out.println("Initialize the simulator first..");
				}else {
					pipelineExecutorObj.readOutputFile();
				}
			}else if(!instruction.equalsIgnoreCase("exit")){
				System.out.println("Not a recognized instruction..");
			}
		}while(!instruction.equalsIgnoreCase("exit"));
	}

}
