package eu.interedition.collatex.simple;

import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class WhitespaceAndPunctuationTokenizer implements Function<String, List<String>> {

  @Override
  public List<String> apply(String input) {
    final List<String> tokens = Lists.newArrayList();
    final StringTokenizer tokenizer = new StringTokenizer(input, " ,.-[]()¿?¡!;:\n#<>|+", true);
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken().trim();
      if (token.length() > 0) {
        tokens.add(token);
      }
    }
    return tokens;
  }
}
