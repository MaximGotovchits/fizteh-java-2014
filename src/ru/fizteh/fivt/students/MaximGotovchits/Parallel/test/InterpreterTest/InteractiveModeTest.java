package ru.fizteh.fivt.students.MaximGotovchits.Parallel.test.InterpreterTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.List;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InteractiveModeTest {
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
    public void dollarBillTest() {
        ByteArrayInputStream in = new ByteArrayInputStream("extremely_secret_word_for_test".getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ ", outContent.toString());
    }

    @Test
    public void createAndDropTableTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("create " + TEST_TABLE_NAME + " (int)\n"
                + "extremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ " + TEST_TABLE_NAME + " exists\n$ ", outContent.toString());
        in = new ByteArrayInputStream(("create to_be_dropped (long)\n"
                + "extremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ " + TEST_TABLE_NAME + " exists\n$ $ created\n$ ", outContent.toString());
        in = new ByteArrayInputStream(("drop to_be_dropped\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ " + TEST_TABLE_NAME + " exists\n$ $ created\n$ $ dropped\n$ ", outContent.toString());
    }

    @Test
    public void getTypeListTestFromCreate() {
        java.util.List<Class<?>> exactTypes = new LinkedList<>();
        exactTypes.add(int.class);
        exactTypes.add(String.class);
        exactTypes.add(long.class);
        exactTypes.add(char.class);
        assertEquals(exactTypes, new Create().getTypeList("(int String long char)"));
        assertNull(new Create().getTypeList("(int String long char bugaga)"));
    }

    @Test
    public void putAndGetTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("use " + TEST_TABLE_NAME + "\nput K [100500]\n"
                + "extremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ new\n$ ", outContent.toString());
        in = new ByteArrayInputStream(("get K\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ new\n$ $ found\n[100500]\n$ ", outContent.toString());
    }

    @Test
    public void listTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("use " + TEST_TABLE_NAME + "\nput K [100500]\nlist\n"
                + "extremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ new\n$ K\n$ ", outContent.toString());
    }

    @Test
    public void removeTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("use " + TEST_TABLE_NAME + "\nput K [100500]\n"
                + "remove K\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ new\n$ removed\n$ ", outContent.toString());
        in = new ByteArrayInputStream(("remove K\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ new\n$ removed\n$ $ not found\n$ ", outContent.toString());
    }

    @Test
    public void rollbackTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("use " + TEST_TABLE_NAME + "\nput K [10050]\n"
                + "rollback\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ overwrite\n$ $ ", outContent.toString());
    }

    @Test
    public void showTablesTest() { // Use empty dir.
        ByteArrayInputStream in = new ByteArrayInputStream(("use " + TEST_TABLE_NAME
                + "\nshow tables\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ something 0\n" + TEST_TABLE_NAME
                + " 0\n$ ", outContent.toString());
    }

    @Test
    public void useTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("use " + TEST_TABLE_NAME
                + "\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("$ using " + TEST_TABLE_NAME + "\n$ ", outContent.toString());
    }

    @Test
    public void incorrectCmdTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("bugaga\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("no such command, try again...\n", errContent.toString());
    }

    @Test
    public void incorrectPutTest() {
        ByteArrayInputStream in = new ByteArrayInputStream(("bugaga\nextremely_secret_word_for_test").getBytes());
        interpreter.scan = new Scanner(in);
        interpreter.startUp(null, false);
        assertEquals("no such command, try again...\n", errContent.toString());
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
        testTableProvider.removeTable(TEST_TABLE_NAME);
    }
}
