package com.zakgof.tools.web.pojoeditor;

public interface IFieldEditor {
  
  public String view(String value);
  
  public String edit(String name, String value);
}
