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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        commandSet.add(new Get());
        commandSet.add(new List());
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
        String[] cmd = {"use", TEST_TABLE_NAME};
        assertTrue(new Use().execute(cmd));
    }

    @Test
    public void useErrTest() {
        String[] cmd = {"bugaga"};
        assertFalse(new Use().execute(cmd));
    }

    @Test
    public void showTablesTest() {
        String[] cmd = {"show", "tables"};
        assertTrue(new ShowTables().execute(cmd));
    }

    @Test
    public void showTablesErrTest() {
        String[] cmd = {"bugaga"};
        assertFalse(new ShowTables().execute(cmd));
    }

    @Test
    public void rollbackTest() {
        String[] cmd = {"rollback"};
        assertTrue(new Rollback().execute(cmd));
    }

    @Test
    public void rollbackErrTest() {
        String[] cmd = {"bugaga", "fail"};
        assertFalse(new Rollback().execute(cmd));
    }

    @Test
    public void removeTest() {
        String[] cmd = {"remove", "not_existing_table"};
        assertTrue(new Remove().execute(cmd));
    }

    @Test
    public void removeErrTest() {
        String[] cmd = {"bugaga"};
        assertFalse(new Remove().execute(cmd));
    }

    @Test
    public void putTest() {
        String[] cmd = {"put", "K", "[100]"};
        assertTrue(new Put().execute(cmd));
    }

    @Test
    public void putErrTest() {
        String[] cmd = {"bugaga"};
        assertFalse(new Put().execute(cmd));
    }

    @Test
    public void listTest() {
        String[] cmd = {"list"};
        assertTrue(new List().execute(cmd));
    }

    @Test
    public void listErrTest() {
        String[] cmd = {"bugaga", "fail"};
        assertFalse(new List().execute(cmd));
    }

    @Test
    public void dropTest() {
        String[] cmd = {"drop", "something"};
        assertTrue(new Drop().execute(cmd));
    }

    @Test
    public void dropErrTest() {
        String[] cmd = {"bugaga"};
        assertFalse(new Drop().execute(cmd));
    }

    @Test
    public void createTest() {
        String[] cmd = {"create", "something", "(int)"};
        assertTrue(new Create().execute(cmd));
    }

    @Test
    public void createErrTest() {
        String[] cmd = {"bugaga"};
        assertFalse(new Create().execute(cmd));
    }

    @Test
    public void commitTest() {
        String[] cmd = {"commit"};
        assertTrue(new Commit().execute(cmd));
    }

    @Test
    public void commitErrTest() {
        String[] cmd = {"bugaga", "fail"};
        assertFalse(new Commit().execute(cmd));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
        testTableProvider.removeTable(TEST_TABLE_NAME);
    }
}
