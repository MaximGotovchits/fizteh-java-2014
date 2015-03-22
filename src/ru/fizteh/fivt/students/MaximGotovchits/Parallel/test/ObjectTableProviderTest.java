package ru.fizteh.fivt.students.MaximGotovchits.Parallel.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class ObjectTableProviderTest extends ObjectTableProvider {
    private String name;
    private List<Class<?>> columnTypes = new LinkedList<>();
    private ObjectStoreable deserializedValue = new ObjectStoreable();
    private ObjectTable testTable;
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
        deserializedValue.getSubValueList().add(100500);
        deserializedValue.getSubValueList().add((long) 10000000);
        deserializedValue.getSubValueList().add(123.456);
        deserializedValue.getSubValueList().add((float) 12.45);
        deserializedValue.getSubValueList().add((byte) 100);
        deserializedValue.getSubValueList().add(true);
        deserializedValue.getSubValueList().add("\"ValueToTest\"");
        deserializedValue.setTypeKeeper(columnTypes);
        deserializedValue.setSerialisedValue("[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]");
    }

    @Test
    public void createAndGetTableTest() throws IOException {
        name = "TestTable";
        assertEquals(null, createTable(name, columnTypes));
        assertEquals(testTable, getTable(name));
    }

    @Test
    public void deserializeTest() throws IOException {
        try {
            assertEquals(deserializedValue, deserialize(testTable,
                    deserializedValue.getSerialisedValue()));
        } catch (ParseException e) {
            assertTrue(false);
        }
    }

    @Test
    public void deserializeExceptionTest() throws IOException {
        try {
            assertEquals(deserializedValue, deserialize(testTable,
                    "we are waiting for exception here >:["));
        } catch (ParseException e) {
            assertTrue(true);
        }
    }

    @Test
    public void serializeTest() {
        assertEquals(deserializedValue.getSerialisedValue(), serialize(testTable, deserializedValue));
    }

    @Test
    public void createForFirstTest() {
        assertEquals(deserializedValue, createFor(testTable, deserializedValue.getSubValueList()));
    }

    @Test
    public void getTableNameTest() throws IOException { // Конечно же, стоит удостовериться,
    // что в рабочей директории на момент тестирования нет таблиц.
        assertEquals("TestTable", testTable.getName());
    }

    @After
    public void cleanUp() { // Поясню. Теста на removeTable нет. Если бы метод не работал, то было бы
        // сообщение "table_name exists". Его нет.
        removeTable(name);
    }
}
