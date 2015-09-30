package com.zakgof.tools.web;

import com.zakgof.db.velvet.properties.IProperty;
import com.zakgof.tools.web.pojoeditor.IFieldEditor;

public interface IField<P, V> {
  
  IFieldEditor<P> editor();
  
  IProperty<P, V> property();
  
}


