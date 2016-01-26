package com.reizes.shiva2.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExplode() {
		String src = "abc.def.qer|adf.adsf|asdf**adfqwZ||asd..asdf";
		String[] comma = new String[] {"abc", "def", "qer|adf", "adsf|asdf**adfqwZ||asd", "", "asdf"};
		String[] vert = new String[] {"abc.def.qer", "adf.adsf", "asdf**adfqwZ", "", "asd..asdf"};
		String[] star = new String[] {"abc.def.qer|adf.adsf|asdf", "", "adfqwZ||asd..asdf"};
		String[] star2 = new String[] {"abc.def.qer|adf.adsf|asdf", "adfqwZ||asd..asdf"};

		assertArrayEquals(comma, StringUtil.explode(src, "."));
		assertArrayEquals(vert, StringUtil.explode(src, "|"));
		assertArrayEquals(star, StringUtil.explode(src, "*"));
		assertArrayEquals(star2, StringUtil.explode(src, "**"));
		assertArrayEquals(new String[] {"N", "2", "2", "2"}, StringUtil.explode("N|2|2|2", "|"));
		assertArrayEquals(new String[] {"N", "3", "0", "3"}, StringUtil.explode("N|3|0|3", "|"));
	}

	@Test
	public void testToJSON() {
		//fail("Not yet implemented");
	}

	@Test
	public void testXmlToJSStr() {
		//fail("Not yet implemented");
	}

	@Test
	public void testJoinStringArrayString() {
		assertEquals("abc/d/ew/as", StringUtil.join(new String[] {"abc", "d", "ew", "as"}, "/"));
		assertEquals("abc//d//ew//as", StringUtil.join(new String[] {"abc", "d", "ew", "as"}, "//"));
		assertEquals("abc\nd\new\nas", StringUtil.join(new String[] {"abc", "d", "ew", "as"}, "\n"));
		assertEquals("abccdcewcas", StringUtil.join(new String[] {"abc", "d", "ew", "as"}, "c"));
	}

	@Test
	public void testJoinListOfQextendsObjectString() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("abc");
		list.add("d");
		list.add("ew");
		list.add("as");
		assertEquals("abc/d/ew/as", StringUtil.join(list, "/"));
		assertEquals("abc//d//ew//as", StringUtil.join(list, "//"));
		assertEquals("abc\nd\new\nas", StringUtil.join(list, "\n"));
		assertEquals("abccdcewcas", StringUtil.join(list, "c"));
	}

	@Test
	public void testJoinLongArrayString() {
		assertEquals("12/0/5501305/1544610", StringUtil.join(new Long[] {12l, 0l, 5501305l, 1544610l}, "/"));
		assertEquals("12//0//5501305//1544610", StringUtil.join(new Long[] {12l, 0l, 5501305l, 1544610l}, "//"));
		assertEquals("12\n0\n5501305\n1544610", StringUtil.join(new Long[] {12l, 0l, 5501305l, 1544610l}, "\n"));
		assertEquals("12c0c5501305c1544610", StringUtil.join(new Long[] {12l, 0l, 5501305l, 1544610l}, "c"));
	}

	@Test
	public void testStripTag() {
		assertEquals("as", StringUtil.stripTag("<span>as</span>"));
		assertEquals("as\n", StringUtil.stripTag("<span>as\n</span>"));
		assertEquals("a\ns\n", StringUtil.stripTag("<span>a\ns\n</span>"));
		assertEquals("as", StringUtil.stripTag("<span style=\"color:#669999;font-weight:bold;\">as</span>"));
		assertEquals("as", StringUtil.stripTag("<span style=\"color:#669999;font-weight:bold;\">as<dfs<dfsadg</span>"));
	}

	@Test
	public void testNormalize() {
		assert (StringUtil.normalize("") == null);
		assert (StringUtil.normalize("　") == null);
		assert (StringUtil.normalize(null) == null);
		assert (StringUtil.normalize(" abc").equals("abc"));
		assert (StringUtil.normalize("　abc　").equals("abc"));
		assert (StringUtil.normalize("　*　").equals("*"));
		assert (StringUtil.normalize("　abc").equals("abc"));
		assert (StringUtil.normalize(" abc  \n\n").equals("abc"));
		assert (StringUtil.normalize("abc").equals("abc"));
		assert (StringUtil.normalize("ab dcd\n sdfsdf c  ").equals("ab dcd\n sdfsdf c"));
	}

	@Test
	public void testTruncate() {
		assertEquals(null, StringUtil.truncate(null, 10));
		assertEquals("012", StringUtil.truncate("012", 10));
		assertEquals("012345678", StringUtil.truncate("012345678", 10));
		assertEquals("0123456789", StringUtil.truncate("0123456789", 10));
		assertEquals("0123456789", StringUtil.truncate("01234567890", 10));
		assertEquals("0123456789", StringUtil.truncate("012345678901234", 10));
	}
}
