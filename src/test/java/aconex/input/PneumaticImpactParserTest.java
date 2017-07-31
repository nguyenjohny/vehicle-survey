package aconex.input;

import aconex.exceptions.InputFormatException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;

public class PneumaticImpactParserTest {

    private Parser<PneumaticImpact> parser = new ImpactParser();

    @DataProvider
    public Object[][] getValidInputSample() {
        return new Object[][]{
                // input & expected
                { "A0", new PneumaticImpact("A", 0L) },
                { "A1231", new PneumaticImpact("A", 1231L) },
                { "B" + TimeUnit.DAYS.toMillis(1), new PneumaticImpact("B", TimeUnit.DAYS.toMillis(1)) }
        };
    }

    @DataProvider
    public Object[][] getInvalidInputSample() {
        return new Object[][]{  // normally I specialise further, but lets just do FormatException
                { "A" },
                { "A-1231" },
                { "BA21" },
                { "B" + (TimeUnit.DAYS.toMillis(1) + 1L) }
        };
    }

    @Test(dataProvider = "getValidInputSample")
    public void testConvertInput(String input, PneumaticImpact expected) throws InputFormatException {
        assertEquals(parser.convertFrom(input), expected, "Input string must convert to expected vehicle");
    }

    @Test(dataProvider = "getInvalidInputSample", expectedExceptions = InputFormatException.class)
    public void testConvertInvalidInput(String input) throws InputFormatException {
        parser.convertFrom(input);
    }

    @Test
    public void testParseToListSingleDay() {
        Map<Integer, Vector<PneumaticImpact>> data = parser.readFrom(Stream.of("A123", "A0123"));
        assertEquals(data.get(0).size(), 2, "every item should be parsed");
    }

    @Test
    public void testParseToListFiveDays() {
        Map<Integer, Vector<PneumaticImpact>> data = parser.readFrom(
                Stream.of("A5000", "A5500", "A4000", "A4400", "A3000", "A3300", "A2000", "A2200", "A1000", "A1100")
        );
        assertEquals(data.get(0).size(), 2, "day 1 every item should be parsed");
        assertEquals(data.get(1).size(), 2, "day 2 every item should be parsed");
        assertEquals(data.get(2).size(), 2, "day 3 every item should be parsed");
        assertEquals(data.get(3).size(), 2, "day 4 every item should be parsed");
        assertEquals(data.get(4).size(), 2, "day 5 every item should be parsed");
    }

    @Test
    public void testInfiniteListIsCapped() {
        Stream<String> stringStream = Stream.generate(() -> "A123"); // infinite stream;

        Map<Integer, Vector<PneumaticImpact>> data = parser.readFrom(stringStream);
        assertEquals(data.get(0).size(), ImpactParser.INPUT_DAY_LIMIT, "every item should be parsed until the limit");
    }

}