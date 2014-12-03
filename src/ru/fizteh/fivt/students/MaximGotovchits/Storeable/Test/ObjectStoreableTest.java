package ru.fizteh.fivt.students.MaximGotovchits.Storeable.Test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Storeable.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Storeable.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Storeable.ObjectTableProvider;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ObjectStoreableTest {
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
    public void setColumnAtTest() {
        ObjectStoreable tempStoreable = new ObjectStoreable();
        tempStoreable.subValueList.add(100500);
        tempStoreable.subValueList.add((long) 10000000);
        tempStoreable.subValueList.add(123.456);
        tempStoreable.subValueList.add((float) 12.45);
        tempStoreable.subValueList.add((byte) 100);
        tempStoreable.subValueList.add(true);
        tempStoreable.subValueList.add("\"ValueToTest\"");
        tempStoreable.typeKeeper = columnTypes;
        tempStoreable.serialisedValue = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";
        assertEquals(tempStoreable, deserializedValue);
        tempStoreable.subValueList.set(6, "\"AnotherValue\"");
        tempStoreable.serialisedValue = "[100500, 10000000, 123.456, 12.45, 100, true, \"AnotherValue\"]";
        assertNotSame(tempStoreable, deserializedValue);
        deserializedValue.setColumnAt(6, "\"AnotherValue\"");
        assertEquals(tempStoreable, deserializedValue);
    }
    @Test
    public void getColumnAtTest() {
        assertEquals(100500, deserializedValue.getColumnAt(0));
        assertEquals((long) 10000000, deserializedValue.getColumnAt(1));
        assertEquals(123.456, deserializedValue.getColumnAt(2));
        assertEquals((float) 12.45, deserializedValue.getColumnAt(3));
        assertEquals((byte) 100, deserializedValue.getColumnAt(4));
        assertEquals(true, deserializedValue.getColumnAt(5));
        assertEquals("\"ValueToTest\"", deserializedValue.getColumnAt(6));
    }
    @Test
    public void getIntAtTest() {
        assertEquals((Integer) 100500, deserializedValue.getIntAt(0));
    }
    @Test
    public void getLongAtTest() {
        assertEquals((Long) Long.parseLong("10000000"), deserializedValue.getLongAt(1));
    }
    @Test
    public void getByteAtTest() {
        assertEquals((Byte) Byte.parseByte("100"), deserializedValue.getByteAt(4));
    }
    @Test
    public void getFloatAtTest() {
        assertEquals((Float) Float.parseFloat("12.45"), deserializedValue.getFloatAt(3));
    }
    @Test
    public void getDoubleAtTest() {
        assertEquals((Double) 123.456, deserializedValue.getDoubleAt(2));
    }
    @Test
    public void getBooleanAtTest() {
        assertEquals(true, deserializedValue.getBooleanAt(5));
    }
    @Test
    public void getStringAtTest() {
        assertEquals("\"ValueToTest\"", deserializedValue.getStringAt(6));
    }
    @After
    public void cleanUp() {
        new ObjectTableProvider().removeTable(name);
    }
}
