package com.example.appactionvisualizer.utils;

import android.content.Context;

import com.example.appactionvisualizer.R;

import static com.example.appactionvisualizer.constants.Constant.URL_PARAMETER_INDICATOR;

public class StringUtils {

  /**
   * @param text text to be matched
   * @param pattern match pattern
   * @return Levenshtein distance between two string
   */
  public static int distance(String text, String pattern) {
    int textLength = text.length();
    int patternLength = pattern.length();
    if(textLength <= patternLength - 2) {
      return patternLength;
    }
    // if one of the strings is empty
    if (textLength * patternLength == 0)
      return textLength + patternLength;
    int [][] dp = new int[textLength + 1][patternLength + 1];

    // init boundaries
    for (int idx = 0; idx < textLength + 1; idx++) {
      dp[idx][0] = idx;
    }
    for (int idx = 0; idx < patternLength + 1; idx++) {
      dp[0][idx] = idx;
    }

    // DP compute
    for (int idx1 = 1; idx1 < textLength + 1; idx1++) {
      for (int idx2 = 1; idx2 < patternLength + 1; idx2++) {
        int left = dp[idx1 - 1][idx2] + 1;
        int down = dp[idx1][idx2 - 1] + 1;
        int left_down = dp[idx1 - 1][idx2 - 1];
        if (text.charAt(idx1 - 1) != pattern.charAt(idx2 - 1))
          left_down += 1;
        dp[idx1][idx2] = Math.min(left, Math.min(down, left_down));
      }
    }
    return dp[textLength][patternLength];
  }

  /**
   * @param tobeMatched actions array
   * @param inputWords input words array
   * @return a matched "score" by computing matched strings between two arrays
   */
  public static int matchScore(String[] tobeMatched, String[] inputWords) {
    if(tobeMatched.length > inputWords.length) {
      return matchScore(inputWords, tobeMatched);
    }
    int maxScore = 0;
    int len = tobeMatched.length;
    for(int startIdx = 0; startIdx <= inputWords.length - len; ++startIdx) {
      int score = 0;
      for(int patternIdx = 0; patternIdx < len; ++patternIdx) {
        score += getScore(inputWords[startIdx + patternIdx], tobeMatched[patternIdx]);
      }
      maxScore = Math.max(maxScore, score);
    }
    return maxScore;
  }

  /**
   * @param text word to be matched
   * @param pattern pattern of the word
   * @return a final score, which is length - Levenshtein Distance between two words
   */
  public static int getScore(String text, String pattern) {
    int score = Math.max(text.length(), pattern.length()) - distance(text, pattern);
    int minSize = Math.min(text.length(), pattern.length());
    // Can only tolerate with two mismatch characters.
    return score >= (pattern.length() - 1) ? score : 0;
  }

  /**
   * replace parameter with input from user to construct the url e.g.:
   * https://example.com/test?utm_campaign=appactions{#foo} ==>
   * https://example.com/test?utm_campaign=appactions#foo=123
   * myapp://example/{foo} ==> myapp://example/123
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
          urlTemplate.charAt(firstPartIdx + 1) + context.getString(R.string.url_parameter, key, identifier);
    }
    curUrl += urlTemplate.substring(secondPartIdx + 1);
    return curUrl;
  }
}
