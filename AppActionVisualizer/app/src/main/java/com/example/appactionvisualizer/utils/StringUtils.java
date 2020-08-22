package com.example.appactionvisualizer.utils;

import android.content.Context;

import com.example.appactionvisualizer.R;

import static com.example.appactionvisualizer.constants.Constant.URL_PARAMETER_INDICATOR;

public class StringUtils {

  /**
   * @param word1
   * @param word2
   * @return Levenshtein distance between two string
   */
  public static int distance(String word1, String word2) {
    int len1 = word1.length();
    int len2 = word2.length();
    // if one of the strings is empty
    if (len1 * len2 == 0)
      return len1 + len2;
    int [][] dp = new int[len1 + 1][len2 + 1];

    // init boundaries
    for (int idx = 0; idx < len1 + 1; idx++) {
      dp[idx][0] = idx;
    }
    for (int idx = 0; idx < len2 + 1; idx++) {
      dp[0][idx] = idx;
    }

    // DP compute
    for (int idx1 = 1; idx1 < len1 + 1; idx1++) {
      for (int idx2 = 1; idx2 < len2 + 1; idx2++) {
        int left = dp[idx1 - 1][idx2] + 1;
        int down = dp[idx1][idx2 - 1] + 1;
        int left_down = dp[idx1 - 1][idx2 - 1];
        if (word1.charAt(idx1 - 1) != word2.charAt(idx2 - 1))
          left_down += 1;
        dp[idx1][idx2] = Math.min(left, Math.min(down, left_down));
      }
    }
    return dp[len1][len2];
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
        score += getScore(tobeMatched[patternIdx], inputWords[startIdx + patternIdx]);
      }
      maxScore = Math.max(maxScore, score);
    }
    return maxScore;
  }

  // Score is (length - Levenshtein Distance between two words)
  public static int getScore(String str1, String str2) {
    int score = Math.max(str1.length(), str2.length()) - distance(str1, str2);
    // If score is 1, only single character matches, doesn't make sense.
    return score > 1 ? score : 0;
  }

  /**
   * @param identifier
   * replace parameter with input from user to construct the url e.g.:
   * https://example.com/test?utm_campaign=appactions{#foo} ==>
   * https://example.com/test?utm_campaign=appactions#foo=123
   * myapp://example/{foo} ==> myapp://example/123
   */
  public static String replaceSingleParameter(Context context, String urlTemplate, String key, String identifier) {
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
