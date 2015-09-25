package com.zakgof.tools.web;

public interface IFieldTypeConvertor {
	Object toNative(String displayValue);
	
	default String toString(Object nativeValue) {
	  return nativeValue.toString();
	}
	
}