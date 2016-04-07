package com.reizes.shiva2.utils;

import static org.junit.Assert.*;

import org.apache.commons.beanutils.ConvertUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BeanUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNumberCast() throws SecurityException, InstantiationException, IllegalAccessException {
		assertEquals(BeanUtil.numberCast(new Long(1), Integer.class).getClass(), Integer.class);
		assertEquals(BeanUtil.numberCast(new Long(1), Float.class).getClass(), Float.class);
		assertEquals(BeanUtil.numberCast(new Long(1), Double.class).getClass(), Double.class);
		assertEquals(BeanUtil.numberCast(new Long(1), Short.class).getClass(), Short.class);
		assertEquals(BeanUtil.numberCast(new Long(1), Byte.class).getClass(), Byte.class);
		assertEquals(BeanUtil.numberCast(new Long(1), String.class).getClass(), String.class);
		assertEquals(BeanUtil.numberCast(new Integer(1), Long.class).getClass(), Long.class);
		assertEquals(BeanUtil.numberCast(new Integer(1), Float.class).getClass(), Float.class);
		assertEquals(BeanUtil.numberCast(new Integer(1), Double.class).getClass(), Double.class);
		assertEquals(BeanUtil.numberCast(new Integer(1), Short.class).getClass(), Short.class);
		assertEquals(BeanUtil.numberCast(new Integer(1), Byte.class).getClass(), Byte.class);
		assertEquals(BeanUtil.numberCast(new Integer(1), String.class).getClass(), String.class);
		assertEquals(BeanUtil.numberCast(new Float(1), Integer.class).getClass(), Integer.class);
		assertEquals(BeanUtil.numberCast(new Float(1), Long.class).getClass(), Long.class);
		assertEquals(BeanUtil.numberCast(new Float(1), Double.class).getClass(), Double.class);
		assertEquals(BeanUtil.numberCast(new Float(1), Short.class).getClass(), Short.class);
		assertEquals(BeanUtil.numberCast(new Float(1), Byte.class).getClass(), Byte.class);
		assertEquals(BeanUtil.numberCast(new Float(1), String.class).getClass(), String.class);
		assertEquals(BeanUtil.numberCast(new Double(1), Integer.class).getClass(), Integer.class);
		assertEquals(BeanUtil.numberCast(new Double(1), Float.class).getClass(), Float.class);
		assertEquals(BeanUtil.numberCast(new Double(1), Long.class).getClass(), Long.class);
		assertEquals(BeanUtil.numberCast(new Double(1), Short.class).getClass(), Short.class);
		assertEquals(BeanUtil.numberCast(new Double(1), Byte.class).getClass(), Byte.class);
		assertEquals(BeanUtil.numberCast(new Double(1), String.class).getClass(), String.class);
		assertEquals(BeanUtil.numberCast(new Short((short)1), Integer.class).getClass(), Integer.class);
		assertEquals(BeanUtil.numberCast(new Short((short)1), Float.class).getClass(), Float.class);
		assertEquals(BeanUtil.numberCast(new Short((short)1), Double.class).getClass(), Double.class);
		assertEquals(BeanUtil.numberCast(new Short((short)1), Long.class).getClass(), Long.class);
		assertEquals(BeanUtil.numberCast(new Short((short)1), Byte.class).getClass(), Byte.class);
		assertEquals(BeanUtil.numberCast(new Short((short)1), String.class).getClass(), String.class);
		assertEquals(BeanUtil.numberCast(new Byte((byte)1), Integer.class).getClass(), Integer.class);
		assertEquals(BeanUtil.numberCast(new Byte((byte)1), Float.class).getClass(), Float.class);
		assertEquals(BeanUtil.numberCast(new Byte((byte)1), Double.class).getClass(), Double.class);
		assertEquals(BeanUtil.numberCast(new Byte((byte)1), Short.class).getClass(), Short.class);
		assertEquals(BeanUtil.numberCast(new Byte((byte)1), Long.class).getClass(), Long.class);
		assertEquals(BeanUtil.numberCast(new Byte((byte)1), String.class).getClass(), String.class);
		assertEquals(BeanUtil.numberCast(new String("1"), Integer.class).getClass(), Integer.class);
		assertEquals(BeanUtil.numberCast(new String("1"), Float.class).getClass(), Float.class);
		assertEquals(BeanUtil.numberCast(new String("1"), Double.class).getClass(), Double.class);
		assertEquals(BeanUtil.numberCast(new String("1"), Short.class).getClass(), Short.class);
		assertEquals(BeanUtil.numberCast(new String("1"), Byte.class).getClass(), Byte.class);
		assertEquals(BeanUtil.numberCast(new String("1"), Long.class).getClass(), Long.class);
	}
	
	@Test
	public void testNumberCast2() {
		assertEquals(ConvertUtils.convert(new Long(1), Integer.class).getClass(), Integer.class);
		assertEquals(ConvertUtils.convert(new Long(1), Float.class).getClass(), Float.class);
		assertEquals(ConvertUtils.convert(new Long(1), Double.class).getClass(), Double.class);
		assertEquals(ConvertUtils.convert(new Long(1), Short.class).getClass(), Short.class);
		assertEquals(ConvertUtils.convert(new Long(1), Byte.class).getClass(), Byte.class);
		assertEquals(ConvertUtils.convert(new Long(1), String.class).getClass(), String.class);
		assertEquals(ConvertUtils.convert(new Integer(1), Long.class).getClass(), Long.class);
		assertEquals(ConvertUtils.convert(new Integer(1), Float.class).getClass(), Float.class);
		assertEquals(ConvertUtils.convert(new Integer(1), Double.class).getClass(), Double.class);
		assertEquals(ConvertUtils.convert(new Integer(1), Short.class).getClass(), Short.class);
		assertEquals(ConvertUtils.convert(new Integer(1), Byte.class).getClass(), Byte.class);
		assertEquals(ConvertUtils.convert(new Integer(1), String.class).getClass(), String.class);
		assertEquals(ConvertUtils.convert(new Float(1), Integer.class).getClass(), Integer.class);
		assertEquals(ConvertUtils.convert(new Float(1), Long.class).getClass(), Long.class);
		assertEquals(ConvertUtils.convert(new Float(1), Double.class).getClass(), Double.class);
		assertEquals(ConvertUtils.convert(new Float(1), Short.class).getClass(), Short.class);
		assertEquals(ConvertUtils.convert(new Float(1), Byte.class).getClass(), Byte.class);
		assertEquals(ConvertUtils.convert(new Float(1), String.class).getClass(), String.class);
		assertEquals(ConvertUtils.convert(new Double(1), Integer.class).getClass(), Integer.class);
		assertEquals(ConvertUtils.convert(new Double(1), Float.class).getClass(), Float.class);
		assertEquals(ConvertUtils.convert(new Double(1), Long.class).getClass(), Long.class);
		assertEquals(ConvertUtils.convert(new Double(1), Short.class).getClass(), Short.class);
		assertEquals(ConvertUtils.convert(new Double(1), Byte.class).getClass(), Byte.class);
		assertEquals(ConvertUtils.convert(new Double(1), String.class).getClass(), String.class);
		assertEquals(ConvertUtils.convert(new Short((short)1), Integer.class).getClass(), Integer.class);
		assertEquals(ConvertUtils.convert(new Short((short)1), Float.class).getClass(), Float.class);
		assertEquals(ConvertUtils.convert(new Short((short)1), Double.class).getClass(), Double.class);
		assertEquals(ConvertUtils.convert(new Short((short)1), Long.class).getClass(), Long.class);
		assertEquals(ConvertUtils.convert(new Short((short)1), Byte.class).getClass(), Byte.class);
		assertEquals(ConvertUtils.convert(new Short((short)1), String.class).getClass(), String.class);
		assertEquals(ConvertUtils.convert(new Byte((byte)1), Integer.class).getClass(), Integer.class);
		assertEquals(ConvertUtils.convert(new Byte((byte)1), Float.class).getClass(), Float.class);
		assertEquals(ConvertUtils.convert(new Byte((byte)1), Double.class).getClass(), Double.class);
		assertEquals(ConvertUtils.convert(new Byte((byte)1), Short.class).getClass(), Short.class);
		assertEquals(ConvertUtils.convert(new Byte((byte)1), Long.class).getClass(), Long.class);
		assertEquals(ConvertUtils.convert(new Byte((byte)1), String.class).getClass(), String.class);
		assertEquals(ConvertUtils.convert(new String("1"), Integer.class).getClass(), Integer.class);
		assertEquals(ConvertUtils.convert(new String("1"), Float.class).getClass(), Float.class);
		assertEquals(ConvertUtils.convert(new String("1"), Double.class).getClass(), Double.class);
		assertEquals(ConvertUtils.convert(new String("1"), Short.class).getClass(), Short.class);
		assertEquals(ConvertUtils.convert(new String("1"), Byte.class).getClass(), Byte.class);
		assertEquals(ConvertUtils.convert(new String("1"), Long.class).getClass(), Long.class);
	}

}
