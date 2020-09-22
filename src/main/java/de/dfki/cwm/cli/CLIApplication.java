package de.dfki.cwm.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class CLIApplication implements CommandLineRunner {

//	@Autowired
//	private CwmCliCommand cliCommand;
	 
	public CLIApplication(CwmCliCommand cliCommand) {
//		this.cliCommand = cliCommand;
	}

    public static void main(String[] args) {
//        SpringApplication.run(CLIApplication.class, args);
    }
 
	@Override
	public void run(String... args) {
//		cliCommand.run();
//	    CommandLine commandLine = new CommandLine(cliCommand);
////	    commandLine.addSubcommand("add", addCommand);
////	    commandLine.addSubcommand("commit", commitCommand);
////	    commandLine.addSubcommand("config", configCommand);
//	    commandLine.parseWithHandler(new CommandLine.RunLast(), args);
	}

}
