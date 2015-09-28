package com.zakgof.tools.web.pojoeditor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.zakgof.db.velvet.entity.IEntityDef;
import com.zakgof.db.velvet.properties.IProperty;
import com.zakgof.db.velvet.properties.IPropertyAccessor;
import com.zakgof.tools.web.IField;
import com.zakgof.velvetdb.viewer.FieldEditors;

public class Glass<K, V> {
  
  private Map<String, IField<?, V>> fields;  
  private IField<K, V> keyField;
  private IEntityDef<K, V> entity;
    
  public static <K, V> Glass<K, V> of(IEntityDef<K, V> entity) {
    return new Glass<K, V>(entity);
  }
  
  @SuppressWarnings("unchecked")
  private Glass(IEntityDef<K, V> entity) {
    this.entity = entity;
    IPropertyAccessor<K, V> pa = entity.propertyAccessor();
    fields = pa.getProperties().stream().collect(Collectors.toMap(propName -> propName, propName -> createField(pa.get(propName))));
    keyField = (IField<K, V>) createField(pa.getKey());
  }
  
  private IField<?, V> createField(IProperty<?, V> property) {
    return new PropertyField<>(property);
  }

  public K keyToNative(String key) {
    return keyField.java(key);
  }
  
  public String getKeyStringForValue(V o) {
    K keyJava = entity.keyOf(o);
    return keyField.str(keyJava);
  }

  public Class<V> getValueClass() {
    return entity.getValueClass();
  }

//  // TODO : remove
//  public V fetchValueFromStringKey(IVelvet velvet, String key) {
//    return entity.get(velvet, (K)keyToNative(key));
//  }

  public Stream<IField<?, V>> fields() {
    return Stream.concat(Stream.of(keyField), fields.values().stream());
  }

  public String keyName() {
    return keyField.getName();
  }

  public String keyToDisplay(K key) {
    return keyField.str(key);
  }

  public V instantiate() {
    try {
      Constructor<? extends V> constructor = getValueClass().getDeclaredConstructor(); // TODO : objenesis ?
          constructor.setAccessible(true);
          V object = constructor.newInstance();
          return object;
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public void setField(V instance, String key, String value) {
    IField<?, V> field = fields.get(key);
    field.setStr(instance, value);
  }
  
  private class PropertyField<P> implements IField<P, V> {

    private IProperty<P, V> property;

    public PropertyField(IProperty<P, V> property) {
      this.property = property;
    }

    @Override
    public String getName() {
      return property.getName();
    }

    @Override
    public boolean isSettable() {
      return property.isSettable();
    }

    @Override
    public String str(P fieldJava) {
      return FieldEditors.deflator(property.getType()).str(fieldJava);
    }
    
    @Override
    public String strFromInstance(V instance) {
      return str(property.get(instance));
    }

    @Override
    public P java(String fieldStr) {
      return FieldEditors.deflator(property.getType()).java(fieldStr);
    }
    
    @Override
    public void setStr(V instance, String fieldStr) {
      property.put(instance, java(fieldStr));
    }

    @Override
    public IFieldEditor editor() {
      return FieldEditors.editor(property.getType());
    }
    
  }

}
