package com.zakgof.velvetdb.viewer;

public interface IFieldDeflator<P> {
  
  default String str(P property)  {
    return property == null ? "" : property.toString();
  }
  
  P java (String str);

}
