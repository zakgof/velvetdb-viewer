package com.zakgof.tools.web.pojoeditor;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.transaction.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.entity.IEntityDef;
import com.zakgof.db.velvet.link.ILinkDef;
import com.zakgof.db.velvet.link.IMultiLinkDef;
import com.zakgof.db.velvet.link.ISingleLinkDef;
import com.zakgof.tools.web.FieldInfo;
import com.zakgof.tools.web.IField;

@Singleton
public class VelvetViewerService {

  @Inject
  private Provider<IVelvet> velvetProvider;
  
  @Inject
  private ViewerDataModel model;
  
  public ViewerDataModel getModel() {
    return model;
  }

  @Transactional
  public  Map<String, Object> edit(String kind, String key) {
    IEntityDef<?, ?> entity = model.getEntity(kind);
    return edit(model, kind, key, entity);
  }
    
  private <K, V> Map<String, Object> edit(ViewerDataModel model, String kind, String key, IEntityDef<K, V> entity) {
    Glass<K, V> glass = Glass.of(entity);
    
    Object value = glass.fetchValueFromStringKey(velvetProvider.get(), key);

    List<Map<String, ?>> multiLinkData = model.multiLinks(kind).stream().map(
       link -> ImmutableMap.<String, Object>builder().
         put("edgeKind", link.getKind()).
         put("kind", link.getChildEntity().getKind()).
         put("needsKey", true).                               //  !VelvetUtil.isAutoKeyed(link.getChildClass())).
         put("picker", pickerData(model, link)).
         put("values", childrenData(link, value)).
         put("candidates", candidatesData(link, value)).build())
        .collect(Collectors.toList());

    List<Map<String, ?>> singleLinkData = model.singleLinks(kind).stream().map(
     link -> ImmutableMap.of(
       "edgeKind", link.getKind(),
       "kind", link.getChildEntity().getKind(),
       "needsKey", true,   //  !VelvetUtil.isAutoKeyed(link.getChildClass()))
       "picker", pickerData(model, link),
       "value", childData(link, value))
      ).collect(Collectors.toList());

    Map<FieldInfo, ?> fields = glass.fields().collect(Collectors.toMap(f -> f, f ->
      ImmutableMap.<String, Object>of(
        "value", f.getValue(value),
        "editor", model == null ? ViewerDataModel.getDefaultEditor(f.getType()) : model.editor(f)
        ),
      (u,v)->u, LinkedHashMap::new));

    Map<String, Object> jmodel = ImmutableMap.<String, Object>builder().
      put("kind", kind).
      put("fields", fields).
      put("key", key).
      put("keyField", glass.keyName()).
      put("multiLinks", multiLinkData).
      put("singleLinks", singleLinkData).
      put("deleteUrl", "/viewer/delete/" + kind).
      put("submitUrl", "/viewer/submitchanges/" + kind + "/" + key).
      put("newRecordUrl", "/viewer/submitnew").
      put("pickLinkUrl", "/viewer/submitPicklink").
      put("editUrl",  "/viewer/entry").
      put("kindUrl",  "/viewer/kind/").
      put("rootUrl", "/viewer").
      build();

    return jmodel;

  }

  private Object pickerData(ViewerDataModel model, ILinkDef<?, ?, ?, ?> link) {
    
    return "";

    /*
    if (model == null)
      return "";

    String parentLinkEdgeKind = model.getPicker(link.getKind());
    if (parentLinkEdgeKind == null)
      return "";

    IEntityDef<?, ?> childEntity = link.getChildEntity();
    Glass<?> childGlass = Glass.of(childEntity);
    List<Object> values = childEntity.getAll(velvetProvider.get()).stream().map(o -> childGlass.getKeyStringForValue(o)).collect(Collectors.toList());
    values.add("");
    return ImmutableMap.of("keys", values, "parentLinkKind", parentLinkEdgeKind);
    */
  }

  private <A, CK, CV> List<String> childrenData(IMultiLinkDef<?, A, CK, CV> multiLink, Object node) {
    Glass<CK, CV> childGlass = Glass.of(multiLink.getChildEntity());
    List<String> childKeyList = multiLink.multi(velvetProvider.get(), (A)node).stream().map(o -> childGlass.getKeyStringForValue(o)).collect(Collectors.toList());
    return childKeyList;
  }

  private <A, CK, CV> String childData(ISingleLinkDef<?, A, CK, CV> singleLink, Object entry) {
    Glass<CK, CV> childGlass = Glass.of(singleLink.getChildEntity());
    CV childNode = singleLink.single(velvetProvider.get(), (A)entry);
    if (childNode == null)
      return "";
    String childKeyDisplay = childGlass.getKeyStringForValue(childNode);
    return childKeyDisplay;
  }

  private Object candidatesData(IMultiLinkDef<?, ?, ?, ?> link, Object entry) {
    // TODO
    // MultiLinkDef - all except mine
    // BiMultiLinkDef - all except mine, mark busy
    // ManyToMany - all except mine, mark busy in another way
    
    IEntityDef<?, ?> childEntity = link.getChildEntity();
    Collection<?> keys = childEntity.keys(velvetProvider.get());
    Glass<?, ?> glass = Glass.of(childEntity);

    Predicate<Object> filter = (key -> true);
    Predicate<Object> mark = (key -> false);
    

    /*
     * TODO
    if (link instanceof BiMultiLinkDef) {
      BiParentLinkDef<?,?> parentLink = ((BiMultiLinkDef<?,?>)link).back();
      filter = key -> !keyObject.equals(parentLink.singleKey(velvetProvider.get(), key));
      mark = key -> (parentLink.singleKey(velvetProvider.get(), key) != null);
    }
    */

    // TODO limit
    List<String> keyStrings = keys.stream().filter(Objects::nonNull).filter(filter).limit(200).map(key -> glass.keyToDisplay(key)).sorted().collect(Collectors.toList());

    return keyStrings;
  }

  @Transactional
  public Map<String, Object> kindTable(String kind) {
    IEntityDef<?, ?> entity = model.getEntity(kind);
    return kindTable(model, kind, entity);
  }
    
   private <K, V> Map<String, Object> kindTable(ViewerDataModel model, String kind, IEntityDef<K, V> entity) {
     
     List<V> objects = entity.get(velvetProvider.get());
     Glass<K, V> glass = Glass.of(entity);
     

    List<IField<?, V>> fields = glass.fields().collect(Collectors.toList());
    List<Map<String, ?>> rows = 
        
        objects.stream()
          .sorted(this.by(o -> entity.keyOf(o)))
          .map(obj -> fieldMap(obj, fields, model))
          .collect(Collectors.toList());

      Map<String, Object> jspmodel = ImmutableMap.<String, Object>builder().
        put("fields", fields).
        put("rows", rows).
        put("keyField", glass.keyName()).
        put("kind", kind).
        put("newRecordUrl", "/viewer/submitnew/" + kind).
        put("rootUrl", "/viewer").
        put("baseEditUrl", "/viewer/entry/" + kind).
        build();

      return jspmodel;
    }

  private Map<String, ?> fieldMap(Object obj, List<IField<?, ?>> fields, ViewerDataModel model) {
    return fields.stream().collect(Collectors.toMap(FieldInfo::getName, f ->
      ImmutableMap.<String, Object>of(
                "value", f.getValue(obj),
                "editor", model == null ? ViewerDataModel.DEFAULT_EDITOR : model.editor(f)
                ),
      (u,v) -> u, () -> new LinkedHashMap<>()
    ));
  }

  @SuppressWarnings("unchecked")
  private <T, K> Comparator<T> by(Function<? super T, K> func) {
    return (T u, T v) -> ((Comparable<K>)func.apply(u)).compareTo(func.apply(v));
  }

  @Transactional
  public void submitEdited(ViewerDataModel model, String kind, String key, Map<String, String> map) {
    IEntityDef<?, ?> entity = model.getEntity(kind);
    submitEdited(model, entity, key, map);
  }
  
  private <K, V> void submitEdited(ViewerDataModel model,  IEntityDef<K, V> entity, String key, Map<String, String> map) {
    Glass<K, V> glazz = Glass.of(entity);
    V object = createPojo(glazz, map);
    entity.put(velvetProvider.get(), object);
  }

  private <K, V> K persistNewObject(String key, Glass<K, V> glass, IEntityDef<K, V> entity) {
    K objectKey = null;
    // if (!VelvetUtil.isAutoKeyed(ci.getType())) { // TODO
      objectKey = (K) glass.keyToNative(key);
      V object = createPojo(glass, key);
      entity.put(velvetProvider.get(), object);
//    } else {
//      Object object = ci.instantiate();
//      velvetProvider.get().put(object);
//      objectKey = VelvetUtil.keyOf(object);
//    }
    return objectKey;
  }
  
  private static <K, V> V createPojo(Glass<K, V> glass, String key) {
    V instance = glass.instantiate();
    glass.setField(instance, glass.keyName(), key);
    return instance;
  }

  private static <K, V> V createPojo(Glass<K, V> glass, Map<String, String> request) {
    V instance = glass.instantiate();
    for (Entry<String, String> entry : request.entrySet()) {
      if (entry.getKey().startsWith("-"))
        continue;
      glass.setField(instance, entry.getKey(), entry.getValue());
    }
    return instance;
  }

  @Transactional
  public void delete(ViewerDataModel model, String kind, String key) {
    /*
    IslandModel deletor = model.getDeletor();
    if (deletor == null)
      throw new RuntimeException("No deletor found for " + kind);
    
    IEntityDef<?, ?> entity = model.getEntity(kind);
    Glass<?> glass = Glass.of(entity);
    Object keyObject = glass.keyToNative(key);
    deletor.deleteByKey(velvetProvider.get(), keyObject, entity.getValueClass());
    */
  }

  @Transactional
  public String submitNew(ViewerDataModel model, String kind, String key, String linkHostKey, String linkEdgeKind) {
    IEntityDef<?, ?> entity = model.getEntity(kind);
    return submitNew(model, entity, key, linkHostKey, linkEdgeKind);
  }
  
  private <K, V> String submitNew(ViewerDataModel model, IEntityDef<K, V> entity , String key, String linkHostKey, String linkEdgeKind) {
    Glass<K, V> glass = Glass.of(entity);
    K objectKey = persistNewObject(key, glass, entity);
    if (linkHostKey != null) {
      attachToParentHost(model, linkHostKey, linkEdgeKind, objectKey);
    }
    return glass.keyToDisplay(objectKey);
  }

  @Transactional
  public void submitPickLink(ViewerDataModel model, String kind, String keyDisplay, String parentLinkKind, String parentKey) {
    IEntityDef<?, ?> entity = model.getEntity(kind);
    Glass<?, ?> glass = Glass.of(entity);
    Object objectKey = glass.keyToNative(keyDisplay);
    attachToParentHost(model, parentKey, parentLinkKind, objectKey);
  }

  private void attachToParentHost(ViewerDataModel model, String linkHostKey, String linkEdgeKind, Object objectKey) {
    /*
    ILinkDef<?, ?, ?, ?> linkDef = model.getLink(linkEdgeKind);
    attachToParentHost(model, linkHostKey, linkDef, objectKey);
    */
  }
  
  private <HK, CK> void attachToParentHost(ViewerDataModel model, String linkHostKey,  ILinkDef<HK, ?, CK, ?> linkDef, Object objectKey) {
    Class<?> linkHostClass = linkDef.getHostEntity().getValueClass();
    Glass<HK, ?> glass = Glass.of(linkDef.getHostEntity());
    Object hostKey = glass.keyToNative(linkHostKey);
    linkDef.connectKeys(velvetProvider.get(), (HK)hostKey, (CK)objectKey);
  }

}
