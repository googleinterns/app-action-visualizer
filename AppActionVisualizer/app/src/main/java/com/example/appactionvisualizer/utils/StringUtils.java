package com.example.appactionvisualizer.utils;

import android.content.Context;

import com.example.appactionvisualizer.R;

import static com.example.appactionvisualizer.constants.Constant.URL_PARAMETER_INDICATOR;

public class StringUtils {

  /**
   * @param tobeMatched actions array
   * @param inputWords input words array
   * @return a matched "score" by computing matched strings between two arrays
   */
  public static int matchScore(String[] tobeMatched, String[] inputWords) {
    if (tobeMatched.length > inputWords.length) {
      return matchScore(inputWords, tobeMatched);
    }
    int maxScore = 0;
    int len = tobeMatched.length;
    for (int startIdx = 0; startIdx <= inputWords.length - len; ++startIdx) {
      int score = 0;
      for (int patternIdx = 0; patternIdx < len; ++patternIdx) {
        score += getScore(inputWords[startIdx + patternIdx], tobeMatched[patternIdx]);
      }
      maxScore = Math.max(maxScore, score);
    }
    return maxScore;
  }

  /**
   * @param text word to be matched
   * @param pattern pattern of the word
   * @return a final score, which is the pattern's length
   */
  public static int getScore(String text, String pattern) {
    if (pattern.startsWith(text)) {
      return pattern.length();
    }
    return 0;
  }

  /**
   * replace parameter with input from user to construct the url e.g.:
   * https://example.com/test?utm_campaign=appactions{#foo} ==>
   * https://example.com/test?utm_campaign=appactions#foo=123 myapp://example/{foo} ==>
   * myapp://example/123
   *
   * @param identifier
   */
  public static String replaceSingleParameter(
      Context context, String urlTemplate, String key, String identifier) {
    if (key == null) return "";
    int firstPartIdx = urlTemplate.indexOf(URL_PARAMETER_INDICATOR);
    int secondPartIdx = urlTemplate.indexOf("}");
    String curUrl = urlTemplate.substring(0, firstPartIdx);
    if (Character.isAlphabetic(urlTemplate.charAt(firstPartIdx + 1))) {
      curUrl += identifier;
    } else {
      curUrl +=
          urlTemplate.charAt(firstPartIdx + 1)
              + context.getString(R.string.url_parameter, key, identifier);
    }
    curUrl += urlTemplate.substring(secondPartIdx + 1);
    return curUrl;
  }
}
