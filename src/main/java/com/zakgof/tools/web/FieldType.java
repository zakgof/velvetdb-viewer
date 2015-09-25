package com.zakgof.tools.web;



public enum FieldType {

	STRING(s -> s),
	INT(s -> Integer.parseInt(s)),
	FLOAT(s -> Float.parseFloat(s)),
	BYTEARRAY(new IFieldTypeConvertor() {

    @Override
    public Object toNative(String displayValue) {      
      throw new RuntimeException("Cannot restore ByteArray from string");
    }
    
    public String toString(Object nativeValue) {
      return "Data " + ((byte[])nativeValue).length + " bytes";
    }
	  
	}),
	LONG(s -> Long.parseLong(s));	
	
	private IFieldTypeConvertor convertor;

	private FieldType(IFieldTypeConvertor convertor) {
		this.convertor = convertor;
	}

	public String toString(Object fieldValue) {
		return fieldValue == null ? "" : convertor.toString(fieldValue);
	}

	public Object toNative(String value) {
		return convertor.toNative(value);
	}

}
