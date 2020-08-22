package com.example.appactionvisualizer.utils;

import org.junit.Test;

import static com.example.appactionvisualizer.constants.Constant.WHITESPACE;
import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

  private static final String TAG = "StringUtilsTest";

  @Test
  public void testMatchScore() {
    String[] words1 = "creat money".split(WHITESPACE);
    String[] words2 = "create money transfer".split(WHITESPACE);
    assertEquals(10, StringUtils.matchScore(words1, words2));
  }

  @Test
  public void testMatchScore2() {
    String[] words1 = "create money transfer".split(WHITESPACE);
    String[] words2 = "creat money".split(WHITESPACE);
    assertEquals(10, StringUtils.matchScore(words1, words2));
  }

  @Test
  public void testMatchScore3() {
    String[] words1 = "latte".split(WHITESPACE);
    String[] words2 = "iced latte".split(WHITESPACE);
    assertEquals(5, StringUtils.matchScore(words1, words2));
  }

  @Test
  public void testMatchScore4() {
    String[] words1 = "latte".split(WHITESPACE);
    String[] words2 = "latte".split(WHITESPACE);
    assertEquals(5, StringUtils.matchScore(words1, words2));
  }

  @Test
  public void testMatchScore5() {
    String[] words1 = "iced latte".split(WHITESPACE);
    String[] words2 = "iced latte".split(WHITESPACE);
    assertEquals(9, StringUtils.matchScore(words1, words2));
  }
}
