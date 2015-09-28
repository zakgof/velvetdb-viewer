package com.zakgof.tools.web;

import com.zakgof.tools.web.pojoeditor.IFieldEditor;

public interface IField<P, V> {
  
  String getName();
  
  boolean isSettable();
  
  String str(P fieldJava);
  
  String strFromInstance(V instance);
  
  P java(String fieldStr);
  
  IFieldEditor editor();

  void setStr(V instance, String fieldStr);
  
}


