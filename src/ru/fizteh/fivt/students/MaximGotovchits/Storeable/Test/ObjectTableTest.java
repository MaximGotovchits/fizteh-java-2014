package ru.fizteh.fivt.students.MaximGotovchits.Storeable.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.*;
import org.junit.*;
import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Storeable.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ObjectTableTest {
    public String name;
    public List<Class<?>> columnTypes = new LinkedList<Class<?>>();
    public ObjectTable tableToCompare = new ObjectTable();
    public ObjectStoreable deserializedValue = new ObjectStoreable();
    public ObjectTable table;
    public String valueToDeserialize;
    public ObjectTable tempTable;
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
        tableToCompare.typeKeeper = columnTypes;
        tableToCompare.tableName = name;
        deserializedValue.subValueList.add(100500);
        deserializedValue.subValueList.add((long) 10000000);
        deserializedValue.subValueList.add(123.456);
        deserializedValue.subValueList.add((float) 12.45);
        deserializedValue.subValueList.add((byte) 100);
        deserializedValue.subValueList.add(true);
        deserializedValue.subValueList.add("\"ValueToTest\"");
        deserializedValue.typeKeeper = columnTypes;
        deserializedValue.serialisedValue = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";
        tempTable = (ObjectTable) new ObjectTableProvider().createTable(name, columnTypes);
        valueToDeserialize = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";
    }
    @Test
    public void getNameTest() throws Exception {
        new Use().useFunction(name, null);
        assertEquals(name, new ObjectTable(name).getName());
    }
    @Test
    public void putTest() throws Exception {
        new Use().useFunction(name, null);
        assertNull(tempTable.put("Key", deserializedValue));
        assertEquals(deserializedValue, tempTable.put("Key", deserializedValue));
    }
    @Test
    public void getTest() throws Exception {
        new Use().useFunction(name, null);
        tempTable.put("Key", deserializedValue);
        assertEquals(deserializedValue, tempTable.get("Key"));
    }
    @Test
    public void sizeTest() throws Exception {
        new Use().useFunction(name, null);
        for (Integer i = 0; i < 1000; ++i) {
            tempTable.put(i.toString(), deserializedValue);
        }
        assertEquals(1000, tempTable.size());
    }
    @Test
    public void listTest() throws Exception {
        new Use().useFunction(name, null);
        List<String> toCompare = new LinkedList<String>();
        for (Integer i = 0; i < 1000; ++i) {
            toCompare.add(i.toString());
            tempTable.put(i.toString(), deserializedValue);
        }
        assertTrue(toCompare.containsAll(tempTable.list()));
    }
    @Test
    public void removeTest() throws Exception {
        new Use().useFunction(name, null);
        tempTable.put("Key", deserializedValue);
        assertEquals(deserializedValue, tempTable.remove("Key"));
    }
    @Test
    public void commitTest() {
        for (Integer i = 0; i < 1000; ++i) {
            tempTable.put(i.toString(), deserializedValue);
        }
        assertEquals(1000, tempTable.commit());
    }
    @Test
    public void rollbackTest() {
        for (Integer i = 0; i < 1000; ++i) {
            tempTable.put(i.toString(), deserializedValue);
        }
        assertEquals(1000, tempTable.rollback());
    }
    @Test
    public void getNumberOfUncommittedChangesTest() {
        for (Integer i = 0; i < 1000; ++i) {
            String key = i.toString();
            tempTable.put(key, deserializedValue);
        }
        assertEquals(1000, tempTable.getNumberOfUncommittedChanges());
        tempTable.commit();
        assertEquals(0, tempTable.getNumberOfUncommittedChanges());
    }
    @Test
    public void getColumnsCountTest() {
        assertEquals(7, tempTable.getColumnsCount());
    }
    @Test
    public void getColumnType() throws Exception {
        new Use().useFunction(name, null);
        assertEquals(int.class, tempTable.getColumnType(0));
        assertEquals(long.class, tempTable.getColumnType(1));
        assertEquals(double.class, tempTable.getColumnType(2));
        assertEquals(float.class, tempTable.getColumnType(3));
        assertEquals(byte.class, tempTable.getColumnType(4));
        assertEquals(boolean.class, tempTable.getColumnType(5));
        assertEquals(String.class, tempTable.getColumnType(6));
    }
    @After
    public void cleanUp() {
        new ObjectTableProvider().removeTable(name);
    }

}

