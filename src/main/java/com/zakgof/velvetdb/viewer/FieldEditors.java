package com.zakgof.velvetdb.viewer;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.zakgof.tools.web.pojoeditor.IFieldEditor;
import com.zakgof.tools.web.pojoeditor.ViewerDataModel;

public class FieldEditors {

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
    return (IFieldDeflator<P>) deflators.get(type);
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

  public static IFieldEditor editor(Class<?> type) {
    return ViewerDataModel.DEFAULT_EDITOR; // TODO
  }
  
  
}
