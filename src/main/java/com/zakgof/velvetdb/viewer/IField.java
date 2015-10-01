package com.zakgof.velvetdb.viewer;

import com.zakgof.db.velvet.properties.IProperty;

public interface IField<P, V> {
  
  IFieldEditor<P> editor();
  
  IProperty<P, V> property();
  
}


