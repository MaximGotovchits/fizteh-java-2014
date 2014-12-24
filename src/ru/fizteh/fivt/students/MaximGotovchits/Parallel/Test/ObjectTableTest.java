package ru.fizteh.fivt.students.MaximGotovchits.Parallel.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ObjectTableTest {
    public String name;
    public List<Class<?>> columnTypes = new LinkedList<Class<?>>();
    public ObjectStoreable deserializedValue = new ObjectStoreable();
    public ObjectTable testTable;
    @Before
    public void initialization() throws IOException {
        name = "TestTable";
        columnTypes.add(int.class);
        columnTypes.add(long.class);
        columnTypes.add(double.class);
        columnTypes.add(float.class);
        columnTypes.add(byte.class);
        columnTypes.add(boolean.class);
        columnTypes.add(String.class);
        testTable = (ObjectTable) new ObjectTableProvider().createTable(name, columnTypes);
        //tableToCompare.typeKeeper = columnTypes;
        //tableToCompare.tableName = name;
        deserializedValue.subValueList.add(100500);
        deserializedValue.subValueList.add((long) 10000000);
        deserializedValue.subValueList.add(123.456);
        deserializedValue.subValueList.add((float) 12.45);
        deserializedValue.subValueList.add((byte) 100);
        deserializedValue.subValueList.add(true);
        deserializedValue.subValueList.add("\"ValueToTest\"");
        deserializedValue.typeKeeper = columnTypes;
        deserializedValue.serialisedValue = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";
        /*testTable = (ObjectTable) new ObjectTableProvider().createTable(name, columnTypes);
        valueToDeserialize = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";*/
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
        for (Integer i = 0; i < 1000; ++i) {
            testTable.put(i.toString(), deserializedValue);
        }
        assertEquals(1000, testTable.size());
    }
    @Test
    public void listTest() throws Exception {
        new Use().useFunction(name, null);
        List<String> toCompare = new LinkedList<String>();
        for (Integer i = 0; i < 1000; ++i) {
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
        for (Integer i = 0; i < 1000; ++i) {
            testTable.put(i.toString(), deserializedValue);
        }
        assertEquals(1000, testTable.commit());
    }
    @Test
    public void rollbackTest() {
        for (Integer i = 0; i < 1000; ++i) {
            testTable.put(i.toString(), deserializedValue);
        }
        assertEquals(1001, testTable.rollback());
    }
    @Test
    public void getNumberOfUncommittedChangesTest() {
        for (Integer i = 0; i < 1000; ++i) {
            String key = i.toString();
            testTable.put(key, deserializedValue);
        }
        assertEquals(1000, testTable.getNumberOfUncommittedChanges());
        testTable.commit();
        assertEquals(0, testTable.getNumberOfUncommittedChanges());
    }
    @Test
    public void getColumnsCountTest() {
        assertEquals(7, testTable.getColumnsCount());
    }
    @Test
    public void getColumnType() throws Exception {
        new Use().useFunction(name, null);
        assertEquals(int.class, testTable.getColumnType(0));
        assertEquals(long.class, testTable.getColumnType(1));
        assertEquals(double.class, testTable.getColumnType(2));
        assertEquals(float.class, testTable.getColumnType(3));
        assertEquals(byte.class, testTable.getColumnType(4));
        assertEquals(boolean.class, testTable.getColumnType(5));
        assertEquals(String.class, testTable.getColumnType(6));
    }
    @After
    public void cleanUp() {
        new ObjectTableProvider().removeTable(name);
    }
}

