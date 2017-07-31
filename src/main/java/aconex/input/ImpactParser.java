package aconex.input;

import aconex.exceptions.InputFormatException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ImpactParser implements Parser<PneumaticImpact> {
    public static final long INPUT_DAY_LIMIT = 1000000L;

    private static final long MAX_MS_DAY = TimeUnit.DAYS.toMillis(1);
    private static final Pattern pattern = Pattern.compile("^(A|B)([0-9]+)");

    @Override
    public Map<Integer, Vector<PneumaticImpact>> readFilePath(String inputFile) {
        try (Stream<String> stream = Files.lines(Paths.get(inputFile))) {
            return readFrom(stream);
        } catch (IOException e) {
            e.printStackTrace(); // just print error, recoverable error.
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<Integer, Vector<PneumaticImpact>> readFrom(Stream<String> inputStream) {

        final Map<Integer, Vector<PneumaticImpact>> data = new HashMap<>();
        data.put(0, new Vector<>()); // seed day 1

        inputStream.limit(INPUT_DAY_LIMIT)
            .forEach(str -> {
                try {
                    final PneumaticImpact nextPneumaticImpact = convertFrom(str);

                    // how do you know there is a new day? you will need to assume
                    // that there are no consecutive days where the next day's traffic
                    // starts after the last car from the day before.

                    Vector<PneumaticImpact> currentDay = data.get(data.size() - 1);
                    if (!currentDay.isEmpty() &&
                            currentDay.lastElement().getMillisOnDay() > nextPneumaticImpact.getMillisOnDay()) {
                        currentDay = new Vector<>();
                        data.put(data.size(), currentDay);
                    }
                    currentDay.add(nextPneumaticImpact);

                } catch (InputFormatException e) {
                    e.printStackTrace(); // or send to third party logger.
                }
            });

        return data;
    }

    @Override
    public PneumaticImpact convertFrom(String text) throws InputFormatException {
        final Matcher result = pattern.matcher(text);
        if (!result.find()) {
            throw new InputFormatException();
        }

        final long dayMs = Long.parseLong(result.group(2));
        if (dayMs > MAX_MS_DAY) {
            throw new InputFormatException();
        }

        return new PneumaticImpact(result.group(1), dayMs);
    }
}
