package ru.fizteh.fivt.students.MaximGotovchits.Parallel.test;

import org.junit.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ObjectTableTest {
    private String name;
    private List<Class<?>> columnTypes = new LinkedList<Class<?>>();
    private ObjectStoreable deserializedValue = new ObjectStoreable();
    private ObjectTable testTable;

    @Before
    public void initialization() throws IOException {
        name = "TestTable";
        columnTypes.add(int.class);
        columnTypes.add(long.class);
        testTable = (ObjectTable) new ObjectTableProvider().createTable(name, columnTypes);
        deserializedValue.getSubValueList().add(100500);
        deserializedValue.getSubValueList().add((long) 10000000);
        deserializedValue.setTypeKeeper(columnTypes);
        deserializedValue.setSerialisedValue("[100500, 10000000]");
    }

    @Test
    public void getNameTest() throws Exception {
        assertEquals(name, testTable.getName());
    }

    @Test
    public void putTest() throws Exception {
        assertNull(testTable.put("Key", deserializedValue));
        assertEquals(deserializedValue, testTable.put("Key", deserializedValue));
    }

    @Test
    public void getTest() throws Exception {
        testTable.put("Key", deserializedValue);
        assertEquals(deserializedValue, testTable.get("Key"));
    }

    @Test
    public void sizeTest() throws Exception {
        testTable.put("1", deserializedValue);
        testTable.put("2", deserializedValue);
        assertEquals(2, testTable.size());
    }

    @Test
    public void listTest() throws Exception {
        List<String> toCompare = new LinkedList<String>();
        toCompare.add("23");
        testTable.put("23", deserializedValue);
        assertTrue(toCompare.containsAll(testTable.list()));
    }

    @Test
    public void removeTest() throws Exception {
        testTable.put("Key", deserializedValue);
        assertEquals(deserializedValue, testTable.remove("Key"));
    }

    @Test
    public void commitTest() {
        testTable.put("0", deserializedValue);
        testTable.put("1", deserializedValue);
        assertEquals(deserializedValue, testTable.get("0"));
        assertEquals(deserializedValue, testTable.get("1"));
        assertEquals(2, testTable.commit());
        assertEquals(deserializedValue, testTable.get("0"));
        assertEquals(deserializedValue, testTable.get("1"));
    }

    @Test
    public void rollbackTest() {
        testTable.put("0", deserializedValue);
        testTable.put("1", deserializedValue);
        assertEquals(deserializedValue, testTable.get("0"));
        assertEquals(deserializedValue, testTable.get("1"));
        assertEquals(2, testTable.rollback());
        assertNull(testTable.get("0"));
        assertNull(testTable.get("1"));
        testTable.put("0", deserializedValue);
        testTable.commit();
        testTable.put("1", deserializedValue);
        testTable.rollback();
        assertNull(testTable.get("1"));
        assertNotNull("0");
    }

    @Test
    public void getNumberOfUncommittedChangesTest() {
        testTable.put("1", deserializedValue);
        testTable.put("2", deserializedValue);
        assertEquals(2, testTable.getNumberOfUncommittedChanges());
        testTable.commit();
        assertEquals(0, testTable.getNumberOfUncommittedChanges());
    }

    @Test
    public void getColumnsCountTest() {
        assertEquals(2, testTable.getColumnsCount());
    }

    @Test
    public void getColumnType() throws Exception {
        assertEquals(int.class, testTable.getColumnType(0));
        assertEquals(long.class, testTable.getColumnType(1));
        }

    @After
    public void cleanUp() {
        new ObjectTableProvider().removeTable(name);
    }
}

