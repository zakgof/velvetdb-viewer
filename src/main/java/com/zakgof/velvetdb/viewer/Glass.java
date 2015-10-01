package com.zakgof.velvetdb.viewer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zakgof.db.velvet.entity.IEntityDef;
import com.zakgof.db.velvet.properties.IProperty;
import com.zakgof.db.velvet.properties.IPropertyAccessor;

public class Glass<K, V> {

  private Map<String, IField<?, V>> fields;
  private IField<K, V> keyField;
  private IEntityDef<K, V> entity;
  private List<String> fieldNames;

  public static <K, V> Glass<K, V> of(IEntityDef<K, V> entity) {
    return new Glass<K, V>(entity);
  }

  @SuppressWarnings("unchecked")
  private Glass(IEntityDef<K, V> entity) {
    this.entity = entity;
    IPropertyAccessor<K, V> pa = entity.propertyAccessor();
    fields = new LinkedHashMap<>();
    fields.put(pa.getKey().getName(), createField(pa.getKey()));
    fields.putAll(pa.getProperties().stream().collect(Collectors.toMap(propName -> propName, propName -> createField(pa.get(propName)))));
    keyField = (IField<K, V>) createField(pa.getKey());
    fieldNames = new ArrayList<>(fields.keySet());
  }

  private IField<?, V> createField(IProperty<?, V> property) {
    return new PropertyField<>(property);
  }

  // public Stream<IField<?, V>> fields() {
  // return Stream.concat(Stream.of(keyField), fields.values().stream());
  // }

  public String keyName() {
    return keyField.property().getName();
  }

  public K keyToNative(String key) {
    return keyField.editor().java(key);
  }

  public String keyToDisplay(K key) {
    return keyField.editor().render(key).view();
  }

  public String getKeyStringForValue(V o) {
    return render(keyField, o).view();
  }

  public Class<V> getValueClass() {
    return entity.getValueClass();
  }

  public void setField(V instance, String fieldName, String fieldStr) {
    IField<?, V> field = fields.get(fieldName);
    setField(field, instance, fieldStr);
  }

  private <P> void setField(IField<P, V> field, V instance, String fieldStr) {
    P fieldJava = field.editor().java(fieldStr);
    field.property().put(instance, fieldJava);
  }

  // // TODO : remove
  // public V fetchValueFromStringKey(IVelvet velvet, String key) {
  // return entity.get(velvet, (K)keyToNative(key));
  // }

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

  private class PropertyField<P> implements IField<P, V> {

    private IProperty<P, V> property;
    private IFieldEditor<P> editor;

    public PropertyField(IProperty<P, V> property) {
      this.property = property;
      this.editor = FieldEditors.editor(property.getName(), property.getType(), property.isSettable());
    }

    @Override
    public IFieldEditor<P> editor() {
      return editor;
    }

    @Override
    public IProperty<P, V> property() {
      return property;
    }

  }

  public Map<String, IValueRender> renderMap(V value) {

    return fields.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> render(entry.getValue(), value)));
  }

  private <P> IValueRender render(IField<P, V> field, V value) {
    P fieldJava = field.property().get(value);
    return field.editor().render(fieldJava);
  }

  public Collection<String> fields() {
    return fieldNames;
  }
}
