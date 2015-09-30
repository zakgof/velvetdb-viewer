package com.zakgof.tools.web.pojoeditor;

public interface IFieldEditor<P> {
  
  P java(String fieldStr);
  
  IValueRender render(P fieldJava);

}
