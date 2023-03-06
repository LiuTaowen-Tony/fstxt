package huffman;

import java.util.*;

public class HuffmanEncoder {

  final HuffmanNode root;
  final Map<String, String> word2bitSequence;
  final static private char LEFT_ENCODING = '0';
  final static private char RIGHT_ENCODING = '1';

  private HuffmanEncoder(HuffmanNode root,
      Map<String, String> word2bitSequence) {
    this.root = root;
    this.word2bitSequence = word2bitSequence;
  }

  public static HuffmanEncoder buildEncoder(Map<String, Integer> wordCounts) {
    if (wordCounts == null) {
      throw new HuffmanEncoderException("wordCounts cannot be null");
    }
    if (wordCounts.size() < 2) {
      throw new HuffmanEncoderException("This encoder requires at least two different words");
    }

    // fixing the order in which words will be processed: this determinize the execution and makes
    // tests reproducible.
    TreeMap<String, Integer> sortedWords = new TreeMap<>(wordCounts);
    PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(sortedWords.size());
    sortedWords.forEach( (word, count) -> queue.add(new HuffmanLeaf(count, word)) );

    while (queue.size() >= 2) {
      var left = queue.poll();
      var right =  queue.poll();
      queue.add(new HuffmanInternalNode(left, right));
    }

    HuffmanNode root = queue.poll();

    Map<String, String> word2bitSequence = new HashMap<>(sortedWords.size());
    getWord2BitSequence(root, word2bitSequence, new StringBuilder());
    return new HuffmanEncoder(root, word2bitSequence);
  }
  private static void getWord2BitSequence(HuffmanNode node, Map<String, String> collector, StringBuilder acc) {
    switch (node) {
      case HuffmanLeaf leaf -> {
        collector.put(leaf.word, acc.toString());
      }
      case HuffmanInternalNode internalNode -> {
        acc.append(LEFT_ENCODING);
        getWord2BitSequence(internalNode.left, collector, acc);
        acc.deleteCharAt(acc.length() - 1);
        acc.append(RIGHT_ENCODING);
        getWord2BitSequence(internalNode.right, collector, acc);
        acc.deleteCharAt(acc.length() - 1);
      }
      default -> throw new IllegalStateException("Unexpected value: " + node);
    }

  }


  public String compress(List<String> text) {
    assert text != null && text.size() > 0;
    var stringBuilder = new StringBuilder();
    for (var word : text) {
      var bitSequence= word2bitSequence.get(word);
      if (bitSequence == null) {
        throw new HuffmanEncoderException();
      }
      stringBuilder.append(bitSequence);
    }
    return stringBuilder.toString();
  }


  public List<String> decompress(String compressedText) {
    assert compressedText != null && compressedText.length() > 0;

    var result = new ArrayList<String>();
    var i = 0;
    var curr = root;
    while (i != compressedText.length()){
      curr = root;
      while (curr instanceof HuffmanInternalNode internalNode) {
        if (i >= compressedText.length()) {
          throw new HuffmanEncoderException();
        }
        if (compressedText.charAt(i) == LEFT_ENCODING) {
          curr = internalNode.left;
        } else {
          curr = internalNode.right;
        }
        i++;
      }
      result.add(((HuffmanLeaf) curr).word);
    }
    return result;
  }

  // Below the classes representing the tree's nodes. There should be no need to modify them, but
  // feel free to do it if you see it fit

  private static abstract class HuffmanNode implements Comparable<HuffmanNode> {

    private final int count;

    public HuffmanNode(int count) {
      this.count = count;
    }

    @Override
    public int compareTo(HuffmanNode otherNode) {
      return count - otherNode.count;
    }
  }


  private static class HuffmanLeaf extends HuffmanNode {

    private final String word;

    public HuffmanLeaf(int frequency, String word) {
      super(frequency);
      this.word = word;
    }
  }


  private static class HuffmanInternalNode extends HuffmanNode {

    private final HuffmanNode left;
    private final HuffmanNode right;

    public HuffmanInternalNode(HuffmanNode left, HuffmanNode right) {
      super(left.count + right.count);
      this.left = left;
      this.right = right;
    }
  }
}
