package aconex.input;

import aconex.exceptions.InputFormatException;

import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

public interface Parser<T> {
    Map<Integer, Vector<T>> readFilePath(String inputFile);
    Map<Integer, Vector<T>> readFrom(Stream<String> inputStream);

    T convertFrom(String text) throws InputFormatException;
}
