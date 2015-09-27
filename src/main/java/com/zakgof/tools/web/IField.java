package com.zakgof.tools.web;

public interface IField<T, E> {
  
  String getName();
  
  boolean isSettable();
  
  String str(T fieldJava); // TODO: custom editors
  
  T java(String fieldStr);

  void setStr(E instance, String fieldStr);
  
  
}


