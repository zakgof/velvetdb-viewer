package com.zakgof.tools.web;

import java.lang.reflect.Field;

public class FieldInfo {
	public String name;
	public FieldType type;
	public Field field;
	
	public FieldInfo(Field field) {
		this.field = field;
		this.name = field.getName();
		this.type = typeOf(field);		
	}
	
	private static FieldType typeOf(Field field) {
		Class<?> fieldType = field.getType();
		if (fieldType == Integer.class || fieldType == int.class)
			return FieldType.INT;
		else if (fieldType == Float.class || fieldType == float.class)
			return FieldType.FLOAT;
		else if (fieldType == Long.class || fieldType == long.class)
			return FieldType.LONG;		
		else if (fieldType == byte[].class)
      return FieldType.BYTEARRAY;
		else
			return FieldType.STRING;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type.toString();
	}
	
	public void setValue(Object instance, String value) {
		try {
			Object fieldValue = type.toNative(value);
			field.setAccessible(true);
			field.set(instance, fieldValue);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getValue(Object instance) {		
		try {
			field.setAccessible(true);
			Object fieldValue = field.get(instance);
			return fieldValue == null ? "" : type.toString(fieldValue);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public Object getNative(String value) {
		return type.toNative(value);
	}

  public String getDisplayValue(Object nativeFieldValue) {
    return type.toString(nativeFieldValue);
  }
}