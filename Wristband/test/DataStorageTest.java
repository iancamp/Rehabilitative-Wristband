import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.util.LinkedList;

import org.junit.*;
/**
 * Created by kevok on 4/13/15.
 */
public class DataStorageTest {
    @Before public void setUp() throws Exception {
        try {
            java.nio.file.Files.delete(FileSystems.getDefault().getPath("test.db"));
        } catch (NoSuchFileException e) {}
    }
    @Test public void testGetInstance() throws Exception {
        DataStorage testObject = DataStorage.getInstance("test.db");
        Assert.assertEquals(testObject,DataStorage.getInstance("test.db"));
    }

    @Test public void testClose() throws Exception {
        DataStorage testObject = DataStorage.getInstance("test.db");
        testObject.close();
        Assert.assertNotEquals(testObject, DataStorage.getInstance("test.db"));
    }

    @Test public void testWriteData() throws Exception {
        DataStorage testObject = DataStorage.getInstance("test.db");
        LinkedList<DataPoint> testData = new LinkedList<DataPoint>();
        testData.add(new DataPoint(1.0f,1.0d));
        testData.add(new DataPoint(2.0f,2.0d));
        testData.add(new DataPoint(3.0f,3.0d));
        testData.add(new DataPoint(4.0f,4.0d));
        testData.add(new DataPoint(5.0f,5.0d));
        testObject.writeData(testData);
    }
}
