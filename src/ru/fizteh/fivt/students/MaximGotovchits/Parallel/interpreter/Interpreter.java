package ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.*;

import java.util.*;

public class Interpreter {
    private static final int VAR_ARGS_NUM = 0;
    private static final String SPLIT_BY_SEMICOLON = "\\s*;\\s*";
    private static final String SECRET_TEST_WORD = "extremely_secret_word_for_test";
    private static final String INVITATIONAL_SYMBOL = "$ ";
    private static final String SPLIT_BY_SPACE = "\\s+";
    private Map<String, Command> commands = new HashMap<>();
    private static final Map<Command, Integer> ARGS_NUM = new HashMap<>();
    static {
        ARGS_NUM.put(new Commit(), 1);
        ARGS_NUM.put(new Drop(), 2);
        ARGS_NUM.put(new Get(), 2);
        ARGS_NUM.put(new ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.List(), 1);
        ARGS_NUM.put(new Remove(), 2);
        ARGS_NUM.put(new Rollback(), 1);
        ARGS_NUM.put(new ShowTables(), 2);
        ARGS_NUM.put(new Use(), 2);
    }

    public Scanner scan = new Scanner(System.in);

    public Interpreter(Set<Command> commandSet) {
        for (Command cmd: commandSet) {
            commands.put(cmd.getCmdName(), cmd);
        }
    }

    public void startUp(String cmdLine, boolean isFromCmdLine) {
        if (isFromCmdLine) { // Batch mode.
            String[] splittedLine = cmdLine.split(SPLIT_BY_SEMICOLON);
            for (String line : splittedLine) {
                String[] cmd = line.split(SPLIT_BY_SPACE);
                parseStep(commands, cmd, isFromCmdLine);
            }
        } else { // Interactive mode.
            while (true) {
                System.out.print(INVITATIONAL_SYMBOL);
                String[] cmd = scan.nextLine().split(SPLIT_BY_SPACE);
                if (cmd[0].equals(SECRET_TEST_WORD)) {
                    break;
                }
                parseStep(commands, cmd, isFromCmdLine);
            }
        }
    }

    private void parseStep(Map<String, Command> commands, String[] cmd, boolean isFromCmdLine) {
        boolean correctCmdName = false;
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            String key = entry.getKey();
            if (key.equals(cmd[0])) {
                correctCmdName = true;
                Command currentCmd = entry.getValue();
                if (ARGS_NUM.get(currentCmd) != null) {
                    currentCmd.execute(cmd, ARGS_NUM.get(currentCmd));
                } else {
                    currentCmd.execute(cmd, VAR_ARGS_NUM);
                }
                break;
            }
        }
        if (!correctCmdName) {
            if (!isFromCmdLine) {
                System.err.println("no such command, try again...");
            } else {
                String[] exit = new String[1];
                exit[0] = "exit";
                System.exit(1);
            }
        }
    }
}
