package aconex;

import aconex.input.ImpactParser;
import aconex.input.PneumaticImpact;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static aconex.TwoAxleVehicleSample.Direction.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class VehicleSurveyorTest {

    @Mock
    private ImpactParser parser;
    private VehicleSurveyor survey;

    private final String MOCK_FILE = "file.txt";

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void load() {
        survey = new VehicleSurveyor(parser);
        Mockito.reset(parser);
    }

    @Test
    public void readEmptyInput() {
        Map<Integer, Vector<PneumaticImpact>> data = new HashMap<>();
        data.put(0, new Vector<>());
        when(parser.readFilePath(eq(MOCK_FILE))).thenReturn(data);

        survey.readData(MOCK_FILE);
        assertTrue(survey.getDaySamples(0, NORTH_BOUND).isEmpty());
    }

    @DataProvider
    public Object[][] getInputSampleNorth() {
        return new Object[][]{
                // input && expected
                { new Vector<>(Arrays.asList(new PneumaticImpact("A", 0L), new PneumaticImpact("A", 1L))), 1 },
                { new Vector<>(Arrays.asList(
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
                    ), 2
                },
                { new Vector<>(Arrays.asList(
                        new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
                    ), 1
                },
                { new Vector<>(Arrays.asList(
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L))
                    ), 1
                },
                { new Vector<>(Arrays.asList(new PneumaticImpact("A", 0L))), 0 },
                { new Vector<>(Arrays.asList(
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
                    ), 3
                },
                { new Vector<>(Arrays.asList(
                        new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
                    ), 1
                },
                { new Vector<>(), 0 }
        };
    }

    @DataProvider
    public Object[][] getInputSampleSouth() {
        return new Object[][]{
                // input && expected
                { new Vector<>(Arrays.asList(new PneumaticImpact("A", 0L))), 0 },
                { new Vector<>(Arrays.asList(
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
                    ), 0
                },
                { new Vector<>(Arrays.asList(
                        new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
                        new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
                    ), 0
                },
                { new Vector<>(), 0 }
        };
    }

    @Test(dataProvider = "getInputSampleNorth")
    public void readInvalidInputA(Vector<PneumaticImpact> input, int numExpected) {
        Map<Integer, Vector<PneumaticImpact>> data = new HashMap<>();
        data.put(0, input);

        when(parser.readFilePath(eq(MOCK_FILE))).thenReturn(data);
        survey.readData(MOCK_FILE);
        assertEquals(survey.getDaySamples(0, NORTH_BOUND).size(), numExpected);
    }

    @Test(dataProvider = "getInputSampleSouth")
    public void readInvalidInputB(Vector<PneumaticImpact> input, int numExpected) {
        Map<Integer, Vector<PneumaticImpact>> data = new HashMap<>();
        when(parser.readFilePath(eq(MOCK_FILE))).thenReturn(data);

        data.put(0, input);
        survey.readData(MOCK_FILE);
        assertEquals(survey.getDaySamples(0, SOUTH_BOUND).size(), numExpected);
    }

    @Test
    public void testCalculateTotalDay() {
        Map<Integer, Vector<PneumaticImpact>> data = new HashMap<>();
        final int dayIndex = 0;
        data.put(dayIndex, new Vector<>(Arrays.asList(
             new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L))
        ));
        when(parser.readFilePath(eq(MOCK_FILE))).thenReturn(data);
        survey.readData(MOCK_FILE);
        assertEquals(survey.getDaySamples(dayIndex, NORTH_BOUND).size(), 3);
        assertEquals(survey.getDaySamples(dayIndex, SOUTH_BOUND).size(), 1);
    }

    @Test
    public void testCalculateSessionSamples() {
        Map<Integer, Vector<PneumaticImpact>> data = new HashMap<>();
        final int dayIndex = 0;
        data.put(0, new Vector<>(Arrays.asList(
             new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
        ));
        data.put(4, new Vector<>(Arrays.asList(
             new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("B", 0L),
             new PneumaticImpact("A", 0L), new PneumaticImpact("A", 0L))
        ));
        when(parser.readFilePath(eq(MOCK_FILE))).thenReturn(data);
        survey.readData(MOCK_FILE);
        assertEquals(survey.getSessionSamples(NORTH_BOUND).size(), 4);
        assertEquals(survey.getSessionSamples(SOUTH_BOUND).size(), 2);
    }
}