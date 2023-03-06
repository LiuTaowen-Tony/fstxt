package huffman;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

  public static List<String> getWords(String filePath) {
    List<String> words = null;
    try (Stream<String> linesStream = Files.lines(Paths.get(filePath))) {
      words = linesStream.flatMap(line -> Arrays.stream(line.split(" "))).map(word -> word.trim())
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return words;
  }

  public static String sequenceOfBitsAsNumber(String binaryEncoding) {
    final String binaryEncodingWithHeading1 =
        "1" + binaryEncoding; // Prepending 1 not to lose heading zeroes
    BigInteger result = new BigInteger(binaryEncodingWithHeading1, 2);
    return result.toString();
  }

  public static String numberAsSequenceOfBits(String numberRepresentation) {
    BigInteger number = new BigInteger(numberRepresentation);
    String binaryRepresentation = number.toString(2);
    return binaryRepresentation.substring(1); // Removing previously prepended 1
  }

  public static long totalLength(List<String> words) {
    long length = words.size() - 1; // White spaces
    length += words.stream().mapToLong(w -> w.length()).sum();
    return length;
  }

  public static Map<String, Integer> countWords(List<String> words) {
    var result = new HashMap<String, Integer>();
    final var NUMBER_OF_THREADS = 10;
    var threads = new ArrayList<Thread>();
    var collectors = new ArrayList<Map<String, Integer>>();

    for (int i = 0; i != NUMBER_OF_THREADS; i++) {
      collectors.add(new HashMap<>());
    }

    for (int i = 0; i != NUMBER_OF_THREADS; i++) {
      Map<String, Integer> map = collectors.get(i);
      final var start = words.size() * i / NUMBER_OF_THREADS;
      final int end;
      if (i == NUMBER_OF_THREADS - 1) {
        end = words.size();
      } else {
        end = words.size() * (i + 1) / NUMBER_OF_THREADS;
      }

      var thread = new Thread(() -> {
        for (var j = start; j != end; j++) {
          String word = words.get(j);
          var oldValue = map.getOrDefault(word, 0);
          map.put(word, oldValue + 1);
        }
      });
      threads.add(thread);
    }

    threads.forEach(Thread::start);

    for (var thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    for (var map : collectors) {
      map.forEach((k, v) -> {
        var oldValue = result.getOrDefault(k, 0);
        result.put(k, oldValue + v);
      });
    }

    return result;
  }
}
