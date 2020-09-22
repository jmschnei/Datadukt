package de.dfki.cwm.cli;

import java.util.Scanner;

import org.springframework.stereotype.Component;

//@Component
public class CwmCliCommand implements Runnable {

	String appName = "";
	
    @Override
    public void run() {
		System.out.println("-------------------------------------");
		System.out.println("---   CWM Command Line Interface  ---");
		System.out.println("-------------------------------------");
		Scanner scanner = new Scanner(System.in);
//		System.out.print("CWM_CLI $ ");
		String line = "";
		boolean stop = false;
		while(!stop) {
			System.out.print("CWM_CLI $ ");
			line = scanner.nextLine();
//			System.out.println("\t--"+line+"--");
			if(line.isEmpty() || line.equalsIgnoreCase("")) {
			}
			else {
				String args[] = line.split(" ");
				if(args.length<=0) {
				}
				else {
					switch (args[0]) {
					case "create":
						createCommand();
						break;
					case "delete":
						deleteCommand();
						break;
					case "run":
						runCommand();
						break;
					case "help":
						printHelp();
						break;
					case "exit":
						stop=true;
						break;
					default:
						System.out.println("Command not supported: "+args[0]+". Use help to get a list of available commands.");
						break;
					}				
//					if(line.contains("exit()") || line.contains("exit") || line.contains("stop") ) {
//						break;
//					}
				}
			}
		}
		scanner.close();
		System.out.println("-------------------------------------");
		System.out.println("See you soon using the CWM_CLI again!");
		System.out.println("-------------------------------------");
	}

    // create
    public void createCommand() {
        System.out.println("Create command");
    }
        
    public void deleteCommand() {
        System.out.println("Delete Command");
    }

    public void runCommand() {
        System.out.println("Run command");
    }

    public void printHelp() {
    	System.out.println("The available commands are:");
    	System.out.println("\tcreate");
    	System.out.println("\tdelete");
    	System.out.println("\trun");
    }
//	public static void main(String[] args) {
//		
//	}
//		
	
}
