package ru.fizteh.fivt.students.MaximGotovchits.Parallel.test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.Use;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
        deserializedValue.subValueList.add(100500);
        deserializedValue.subValueList.add((long) 10000000);
        deserializedValue.typeKeeper = columnTypes;
        deserializedValue.serialisedValue = "[100500, 10000000]";
    }

    @Test
    public void getNameTest() throws Exception {
        assertEquals(name, testTable.getName());
    }

    @Test
    public void putTest() throws Exception {
        new Use().useFunction(name, null);
        assertNull(testTable.put("Key", deserializedValue));
        assertEquals(deserializedValue, testTable.put("Key", deserializedValue));
    }

    @Test
    public void getTest() throws Exception {
        new Use().useFunction(name, null);
        testTable.put("Key", deserializedValue);
        assertEquals(deserializedValue, testTable.get("Key"));
    }

    @Test
    public void sizeTest() throws Exception {
        new Use().useFunction(name, null);
        for (Integer i = 0; i < 3; ++i) {
            testTable.put(i.toString(), deserializedValue);
        }
        assertEquals(3, testTable.size());
    }

    @Test
    public void listTest() throws Exception {
        new Use().useFunction(name, null);
        List<String> toCompare = new LinkedList<String>();
        for (Integer i = 0; i < 3; ++i) {
            toCompare.add(i.toString());
            testTable.put(i.toString(), deserializedValue);
        }
        assertTrue(toCompare.containsAll(testTable.list()));
    }

    @Test
    public void removeTest() throws Exception {
        new Use().useFunction(name, null);
        testTable.put("Key", deserializedValue);
        assertEquals(deserializedValue, testTable.remove("Key"));
    }

    @Test
    public void commitTest() {
        for (Integer i = 0; i < 3; ++i) {
            testTable.put(i.toString(), deserializedValue);
        }
        assertEquals(deserializedValue, testTable.get("0"));
        assertEquals(deserializedValue, testTable.get("1"));
        assertEquals(deserializedValue, testTable.get("2"));
        assertEquals(3, testTable.commit());
        assertEquals(deserializedValue, testTable.get("0"));
        assertEquals(deserializedValue, testTable.get("1"));
        assertEquals(deserializedValue, testTable.get("2"));
    }

    @Test
    public void rollbackTest() {
        for (Integer i = 0; i < 3; ++i) {
            testTable.put(i.toString(), deserializedValue);
        }
        assertEquals(deserializedValue, testTable.get("0"));
        assertEquals(deserializedValue, testTable.get("1"));
        assertEquals(deserializedValue, testTable.get("2"));
        assertEquals(3, testTable.rollback());
        assertNull(testTable.get("0"));
        assertNull(testTable.get("1"));
        assertNull(testTable.get("2"));
    }

    @Test
    public void getNumberOfUncommittedChangesTest() {
        for (Integer i = 0; i < 2; ++i) {
            String key = i.toString();
            testTable.put(key, deserializedValue);
        }
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
        new Use().useFunction(name, null);
        assertEquals(int.class, testTable.getColumnType(0));
        assertEquals(long.class, testTable.getColumnType(1));
        }

    @After
    public void cleanUp() {
        new ObjectTableProvider().removeTable(name);
    }
}

