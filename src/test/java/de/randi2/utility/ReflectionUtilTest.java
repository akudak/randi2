/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.randi2.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 *
 * @author jthoenes
 */
public class ReflectionUtilTest {

    public ReflectionUtilTest() {
    }

	public class TestClass1{
		private int field1;
		private String field2;

		public int getField1() {
			return field1;
		}

		public void setField1(int field1) {
			this.field1 = field1;
		}

		public String getField2() {
			return field2;
		}

		public void setField2(String field2) {
			this.field2 = field2;
		}

	}

	/**
	 * Test of getPropertyFields method, of class ReflectionUtil.
	 */
	@Test
	public void testGetPropertyFields() throws NoSuchFieldException {
		Set<Field> reference = new HashSet<Field>();
		reference.add(TestClass1.class.getDeclaredField("field1"));
		reference.add(TestClass1.class.getDeclaredField("field2"));
		
		Set<Field> fields = ReflectionUtil.getPropertyFields(new TestClass1());

		assertEquals(reference, fields);
	}

	@Test
	public void testGetGetters() throws NoSuchMethodException{
		Set<Method> reference = new HashSet<Method>();
		reference.add(TestClass1.class.getMethod("getField1"));
		reference.add(TestClass1.class.getMethod("getField2"));

		Set<Method> getters = ReflectionUtil.getGetters(TestClass1.class);

		assertEquals(reference, getters);
	}

	@Test
	public void testGetSetters() throws NoSuchMethodException{
		Set<Method> reference = new HashSet<Method>();
		reference.add(TestClass1.class.getMethod("setField1", int.class));
		reference.add(TestClass1.class.getMethod("setField2", String.class));

		Set<Method> setters = ReflectionUtil.getSetters(TestClass1.class);

		assertEquals(reference, setters);
	}

	@Test
	public void TestGetPropertyName() throws NoSuchMethodException{
		assertEquals("field1",
				ReflectionUtil.getPropertyName(TestClass1.class.getMethod("getField1")));
		assertEquals("field2",
				ReflectionUtil.getPropertyName(TestClass1.class.getMethod("getField2")));
		assertEquals("field1",
				ReflectionUtil.getPropertyName(TestClass1.class.getMethod("setField1", int.class)));
		assertEquals("field2",
				ReflectionUtil.getPropertyName(TestClass1.class.getMethod("setField2", String.class)));
	}

}