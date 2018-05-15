package kps.tests;

import org.junit.Assert;
import org.junit.Test;
import kps.server.logs.LogItem;
import kps.util.XMLFormatException;
import kps.util.XMLUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ParametersAreNonnullByDefault
public class LogTests {
    private final String VALID_LOG_FILE = "testdata/testlogs.xml";
    private final String OUTPUT_LOG_FILE = "testdata/testlogs-output.xml";
    private final String INTERMEDIATE_LOG_FILE = "testdata/testlogs-intermediate.xml";

    /**
     * Makes sure that valid logs are parsed correctly.
     */
    @Test
    public void testValidLogReading() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(VALID_LOG_FILE)));
            LogItem.parse(content);
        } catch (IOException e) {
            Assert.fail("Failed to read test log file; " + e);
        } catch (XMLFormatException e) {
            Assert.fail("Incorrect XML format; " + e);
        }
    }

    /**
     * Makes sure that valid logs are parsed and then written correctly.
     */
    @Test
    public void testValidLogWriting() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(VALID_LOG_FILE)));
            LogItem[] logs = LogItem.parse(content);
            XMLUtil.writeToFile(LogItem.toXML(logs), OUTPUT_LOG_FILE);
        } catch (IOException e) {
            Assert.fail("Failed to read or write test log file; " + e);
        } catch (XMLFormatException e) {
            Assert.fail("Incorrect XML format; " + e);
        }
    }

    /**
     * Makes sure that valid logs are parsed and then written then parsed correctly
     */
    @Test
    public void testValidLogReadingAndWriting() {
        try {
            String firstContent = new String(Files.readAllBytes(Paths.get(VALID_LOG_FILE)));
            LogItem[] firstLogs = LogItem.parse(firstContent);
            XMLUtil.writeToFile(LogItem.toXML(firstLogs), INTERMEDIATE_LOG_FILE);

            System.err.println("Done reading");

            String secondContent = new String(Files.readAllBytes(Paths.get(INTERMEDIATE_LOG_FILE)));
            LogItem[] secondLogs = LogItem.parse(secondContent);

            for (LogItem l : secondLogs) {
            	System.err.println(l);
            }

            Assert.assertEquals(firstLogs.length, secondLogs.length);

            for (int i = 0; i < firstLogs.length; i++) {
                Assert.assertEquals(firstLogs[i], secondLogs[i]);
            }
        } catch (IOException e) {
            Assert.fail("Failed to read or write test log file; " + e);
        } catch (XMLFormatException e) {
            Assert.fail("Incorrect XML format; " + e);
        }
    }
}
