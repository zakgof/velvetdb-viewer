package com.zakgof.velvetdb.viewer;

public interface IFieldEditor<P> {
  
  P java(String fieldStr);
  
  IValueRender render(P fieldJava);

}
