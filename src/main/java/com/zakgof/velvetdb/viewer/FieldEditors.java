package com.zakgof.velvetdb.viewer;

import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.collect.ImmutableMap;
import com.zakgof.tools.web.pojoeditor.IFieldEditor;
import com.zakgof.tools.web.pojoeditor.IValueRender;

public class FieldEditors {

  private static final IFieldDeflator<?> DEFAULT_DEFLATOR = deflator(str -> null);
  
  private static Map<Class<?>, IFieldDeflator<?>> deflators = ImmutableMap.<Class<?>, IFieldDeflator<?>>builder().
      put(Integer.class, deflator(s -> Integer.parseInt(s))).
      put(Long.class, deflator(s -> Long.parseLong(s))).
      put(Short.class, deflator(s -> Short.parseShort(s))).
      put(Byte.class, deflator(s -> Byte.parseByte(s))).
      put(String.class, deflator(s -> s)).
      put(Boolean.class, deflator(s -> s.equalsIgnoreCase("true"))).
      build();
                                                                  

  @SuppressWarnings("unchecked")
  public static <P> IFieldDeflator<P> deflator(Class<P> type) {
    return (IFieldDeflator<P>) deflators.getOrDefault(type, DEFAULT_DEFLATOR);
  }
  
  private static <P> IFieldDeflator<P> deflator(Function<String, P> toJava, Function<P, String> toStr) {
    return new IFieldDeflator<P>() {
      @Override
      public P java(String str) {
        return toJava.apply(str);
      }
      @Override
      public String str(P property) {
        return toStr.apply(property);
      }
    };
  }
  
  private static <P> IFieldDeflator<P> deflator(Function<String, P> toJava) {
    return new IFieldDeflator<P>() {
      @Override
      public P java(String str) {
        return toJava.apply(str);
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  public static <P, V> IFieldEditor<P> editor(String name, Class<P> type, boolean isSettable) {
    IFieldDeflator<P> deflator = (IFieldDeflator<P>)deflators.get(type);
    if (deflator == null)
      return new TextEditor<P>(name, (IFieldDeflator<P>) DEFAULT_DEFLATOR, false);
    else
      return new TextEditor<P>(name, deflator, isSettable);
  }
  
  private static class TextEditor<P> implements IFieldEditor<P> {
    
    private IFieldDeflator<P> deflator;
    private boolean isSettable;
    private String name;

    TextEditor(String name, IFieldDeflator<P> deflator, boolean isSettable) {
      this.name = name;
      this.deflator = deflator;
      this.isSettable = isSettable;
    }

    @Override
    public P java(String fieldStr) {
      return deflator.java(fieldStr); // TODO: escape/unescape !
    }

    @Override
    public IValueRender render(P fieldJava) {
      return new TextRender<P>(name, deflator.str(fieldJava), isSettable);
    }
    
  }
  
  private static class TextRender<P> implements IValueRender {

    private boolean isSettable;
    private String value;
    private String name;

    public TextRender(String name, String value, boolean isSettable) {
      this.name = name;
      this.value = value;
      this.isSettable = isSettable;
    }

    @Override
    public String view() {
      return value;
    }

    @Override
    public String edit() {
      if (!isSettable)
        return view();
      return "<input type=\"text\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeHtml(value) + "\" />";
    }
    
    @Override
    public String toString() {
      return view();
    }
    
  }
  
  
}
