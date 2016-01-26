package com.reizes.shiva2.utils;

import static org.junit.Assert.*;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CollectionUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToSet() {
		String keywordStr = "e-Learning|원격대학|기업";
		Set<String> keywords = CollectionUtil.toSet(StringUtils.split(keywordStr, '|'));
		assertTrue(keywords.contains("e-Learning"));
		assertTrue(keywords.contains("원격대학"));
		assertTrue(keywords.contains("기업"));
	}

	@Test
	public void testUnion() {
		String keywordStr1 = "e-Learning|원격대학|기업";
		Set<String> keywords1 = CollectionUtil.toSet(StringUtils.split(keywordStr1, '|'));
		String keywordStr2 = "기업|남기훈|계인호";
		Set<String> keywords2 = CollectionUtil.toSet(StringUtils.split(keywordStr2, '|'));
		Set<String> union = CollectionUtil.union(keywords1, keywords2);
		assertTrue(union.contains("e-Learning"));
		assertTrue(union.contains("원격대학"));
		assertTrue(union.contains("기업"));
		assertTrue(union.contains("남기훈"));
		assertTrue(union.contains("계인호"));
	}

	@Test
	public void testUnionTo() {
		String keywordStr1 = "e-Learning|원격대학|기업";
		Set<String> keywords1 = CollectionUtil.toSet(StringUtils.split(keywordStr1, '|'));
		String keywordStr2 = "기업|남기훈|계인호";
		Set<String> keywords2 = CollectionUtil.toSet(StringUtils.split(keywordStr2, '|'));
		CollectionUtil.unionTo(keywords1, keywords2);
		System.out.println(keywords1);
		assertTrue(keywords1.contains("e-Learning"));
		assertTrue(keywords1.contains("원격대학"));
		assertTrue(keywords1.contains("기업"));
		assertTrue(keywords1.contains("남기훈"));
		assertTrue(keywords1.contains("계인호"));
	}

	@Test
	public void testIntersect() {
		String keywordStr1 = "e-Learning|원격대학|기업";
		Set<String> keywords1 = CollectionUtil.toSet(StringUtils.split(keywordStr1, '|'));
		String keywordStr2 = "기업|남기훈|계인호";
		Set<String> keywords2 = CollectionUtil.toSet(StringUtils.split(keywordStr2, '|'));
		Set<String> intersect = CollectionUtil.intersect(keywords1, keywords2);
		assertTrue(intersect.contains("기업"));
		assertFalse(intersect.contains("e-Learning"));
		assertFalse(intersect.contains("원격대학"));
		assertFalse(intersect.contains("남기훈"));
		assertFalse(intersect.contains("계인호"));
	}

}
