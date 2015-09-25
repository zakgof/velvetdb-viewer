package com.zakgof.tools.web.pojoeditor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.annotation.Key;
import com.zakgof.db.velvet.annotation.SortedKey;
import com.zakgof.db.velvet.entity.IEntityDef;
import com.zakgof.tools.web.FieldInfo;

public class Glass<K, V> {
  
  private final Map<String, FieldInfo> fields;
  private final Class<V> clazz;
  private IEntityDef<K, V> entity;
  private Field keyField;
  private String keyFieldName;
  
  public static <K, V> Glass<K, V> of(IEntityDef<K, V> entity) {
    return new Glass<K, V>(entity);
  }
  
  private Glass(IEntityDef<K, V> entity) {
    this.entity = entity;
    this.clazz = entity.getValueClass();
    List<Field> reflectionFields = getFields(clazz);
    this.fields = reflectionFields.stream().
        map(field -> new FieldInfo(field)).
        collect(Collectors.toMap(FieldInfo::getName, o -> o, (u,v)->u, LinkedHashMap::new));
    
//    if (entity instanceof AnnoEntityDef) {
//      String keyFieldName = 
//    }

    // TODO
    this.keyField = reflectionFields.stream().filter(f -> f.getAnnotation(Key.class) != null ||  f.getAnnotation(SortedKey.class) != null) .findFirst().orElse(null);
    this.keyFieldName = keyField == null ? "#key" : keyField.getName();
  }
  
  // TODO property fetcher
  private List<Field> getFields(Class<?> type) {    
    List<Field> fields = Arrays.stream(type.getDeclaredFields()).
      filter(field -> (field.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC)) == 0).
      collect(Collectors.toList());   
    if (type.getSuperclass() != null)
      fields.addAll(getFields(type.getSuperclass()));
    return fields;
  }

  public K keyToNative(String key) {
    return (K)fields.get(keyFieldName).type.toNative(key);
  }
  
  public String getKeyStringForValue(V o) {
    Object keyNative = entity.keyOf(o);
    return fields.get(keyFieldName).type.toString(keyNative);
  }

  public Class<V> getValueClass() {
    return clazz;
  }

  // TODO : remove
  public V fetchValueFromStringKey(IVelvet velvet, String key) {
    return entity.get(velvet, (K)keyToNative(key));
  }

  public Stream<FieldInfo> fields() {
    return fields.values().stream();
  }

  public String keyName() {
    return keyFieldName;
  }

  public String keyToDisplay(Object key) {
    return fields.get(keyFieldName).type.toString(key);
  }

  public V instantiate() {
    try {
      Constructor<? extends V> constructor = clazz.getDeclaredConstructor();
          constructor.setAccessible(true);
          V object = constructor.newInstance();
          return object;
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public void setField(V instance, String key, String value) {
    FieldInfo fieldInfo = fields.get(key);
    fieldInfo.setValue(instance, value);
  }

}
