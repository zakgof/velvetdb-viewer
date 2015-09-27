package com.zakgof.tools.web.pojoeditor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.zakgof.db.velvet.entity.IEntityDef;
import com.zakgof.db.velvet.entity.IProperty;
import com.zakgof.db.velvet.entity.IPropertyAccessor;
import com.zakgof.tools.web.IField;

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
    
    if (entity instanceof IPropertyAccessor) {
      IPropertyAccessor<K, V> pa = (IPropertyAccessor<K, V>)entity;
      fields = pa.getProperties().stream().collect(Collectors.toMap(propName -> propName, propName -> createField(pa.get(propName))));
      keyField = (IField<K, V>) createField(pa.getKey());
    } else {
      // ??
    }
    
  }
  
  private IField<?, V> createField(IProperty<?, V> property) {
    return new PropertyField(property);
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
    return fields.values().stream();
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
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public P java(String fieldStr) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void setStr(V instance, String fieldStr) {
      property.put(instance, java(fieldStr));
    }
    
  }

}
