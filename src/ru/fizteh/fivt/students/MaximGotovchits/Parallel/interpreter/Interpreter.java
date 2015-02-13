package ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.*;
import java.util.*;

public class Interpreter {
    private static final String SPLIT_BY_SEMICOLON = "\\s*;\\s*";
    private static final String INVITATIONAL_SYMBOL = "$ ";
    private static final String SPLIT_BY_SPACE = "\\s+";
    private Map<String, Command> commands = new HashMap<>();

    public Interpreter(Set<Command> commandSet) {
        for (Command cmd: commandSet) {
            commands.put(cmd.getCmdName(), cmd);
        }
    }

    public void startUp(String cmdLine, boolean isFromCmdLine) throws Exception {
        boolean correctCmdName = false;
        if (isFromCmdLine) { // Batch mode.
            String[] splittedLine = cmdLine.split(SPLIT_BY_SEMICOLON);
            for (String line : splittedLine) {
                String[] cmd = line.split(SPLIT_BY_SPACE);
                parseStep(commands, cmd, isFromCmdLine);
            }
        } else { // Interactive mode.
            while (true) {
                Scanner scan = new Scanner(System.in);
                System.out.print(INVITATIONAL_SYMBOL);
                String[] cmd = scan.nextLine().split(SPLIT_BY_SPACE);
                parseStep(commands, cmd, isFromCmdLine);
            }

        }
    }

    private void parseStep(Map<String, Command> commands, String[] cmd, boolean isFromCmdLine) throws Exception {
        boolean correctCmdName = false;
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            String key = entry.getKey();
            if (key.equals(cmd[0])) {
                correctCmdName = true;
                Command currentCmd = entry.getValue();
                currentCmd.execute(cmd);
                break;
            }
        }
        if (!correctCmdName) {
            if (isFromCmdLine) {
                System.err.println("no such command, try again...");
            } else {
                String[] exit = new String[1];
                exit[0] = "exit";
                new Exit().execute(exit);
            }
        }
        correctCmdName = false;
    }

    //Interpreter(cmd, fromCmdLine);
}
