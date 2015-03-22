package ru.fizteh.fivt.students.MaximGotovchits.Parallel.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ObjectStoreableTest {
    private String name;
    private List<Class<?>> columnTypes = new LinkedList<Class<?>>();
    private ObjectStoreable deserializedValue = new ObjectStoreable();
    public ObjectTable table;
    private String valueToDeserialize;
    private ObjectTable tempTable;

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
        deserializedValue.getSubValueList().add(100500);
        deserializedValue.getSubValueList().add((long) 10000000);
        deserializedValue.getSubValueList().add(123.456);
        deserializedValue.getSubValueList().add((float) 12.45);
        deserializedValue.getSubValueList().add((byte) 100);
        deserializedValue.getSubValueList().add(true);
        deserializedValue.getSubValueList().add("\"ValueToTest\"");
        deserializedValue.setTypeKeeper(columnTypes);
        deserializedValue.setSerialisedValue("[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]");
        tempTable = (ObjectTable) new ObjectTableProvider().createTable(name, columnTypes);
        valueToDeserialize = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";
    }

    @Test
    public void setColumnAtTest() {
        ObjectStoreable tempStoreable = new ObjectStoreable();
        tempStoreable.getSubValueList().add(100500);
        tempStoreable.getSubValueList().add((long) 10000000);
        tempStoreable.getSubValueList().add(123.456);
        tempStoreable.getSubValueList().add((float) 12.45);
        tempStoreable.getSubValueList().add((byte) 100);
        tempStoreable.getSubValueList().add(true);
        tempStoreable.getSubValueList().add("\"ValueToTest\"");
        tempStoreable.setTypeKeeper(columnTypes);
        tempStoreable.setSerialisedValue("[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]");
        assertEquals(tempStoreable, deserializedValue);
        tempStoreable.getSubValueList().set(6, "\"AnotherValue\"");
        tempStoreable.setSerialisedValue("[100500, 10000000, 123.456, 12.45, 100, true, \"AnotherValue\"]");
        assertNotSame(tempStoreable, deserializedValue);
        deserializedValue.setColumnAt(6, "\"AnotherValue\"");
        assertEquals(tempStoreable, deserializedValue);
    }

    @Test
    public void getColumnAtTest() {
        assertEquals((long) 10000000, deserializedValue.getColumnAt(1));
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
