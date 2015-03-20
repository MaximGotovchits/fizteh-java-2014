package ru.fizteh.fivt.students.MaximGotovchits.Parallel.test.InterpreterTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.List;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class BatchModeTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private Set<Command> commandSet;
    private Interpreter interpreter;
    private ObjectTableProvider testTableProvider = new ObjectTableProvider();
    private static final String TEST_TABLE_NAME = "test_table";

    @Before
    public void setUpStreams() throws IOException {
        commandSet = new HashSet<>();
        commandSet.add(new Commit());
        commandSet.add(new Create());
        commandSet.add(new Drop());
        commandSet.add(new Exit());
        commandSet.add(new FillTable());
        commandSet.add(new Get());
        commandSet.add(new List());
        commandSet.add(new MakeDirs());
        commandSet.add(new Put());
        commandSet.add(new Remove());
        commandSet.add(new Rollback());
        commandSet.add(new ShowTables());
        commandSet.add(new Use());
        interpreter = new Interpreter(commandSet);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        java.util.List<Class<?>> types = new LinkedList<>();
        types.add(int.class);
        testTableProvider.createTable(TEST_TABLE_NAME, types);
    }

    @Test
    public void useTest() {

    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
        testTableProvider.removeTable(TEST_TABLE_NAME);
    }
}
