package com.zakgof.velvetdb.viewer;

import java.util.Collection;
import java.util.Comparator;
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
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Singleton;
import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.entity.IEntityDef;
import com.zakgof.db.velvet.entity.ISortableEntityDef;
import com.zakgof.db.velvet.link.IBiSingleLinkDef;
import com.zakgof.db.velvet.link.ILinkDef;
import com.zakgof.db.velvet.link.IMultiLinkDef;
import com.zakgof.db.velvet.link.ISingleLinkDef;
import com.zakgof.db.velvet.query.IRangeQuery;
import com.zakgof.db.velvet.query.Queries;

/**
 *
 * Picker logics:
 *
 * single link - show all
 *
 */
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
    public Map<String, Object> record(String kind, String key) {
        IEntityDef<?, ?> entity = model.getEntity(kind);
        return record(model, kind, key, entity);
    }

    private <K, V> Map<String, Object> record(ViewerDataModel model, String kind, String key, IEntityDef<K, V> entity) {
        Glass<K, V> glass = Glass.of(entity);
        V value = entity.get(velvetProvider.get(), glass.keyToNative(key));
        Map<String, IValueRender> row = glass.renderMap(value);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<IMultiLinkDef<K, V, ?, ?>> multiLinks = (List) model.multiLinks(kind);

        List<Map<String, ?>> multiLinkData = multiLinks.stream().map(link -> ImmutableMap.<String, Object> builder().put("edgeKind", link.getKind()).put("kind", link.getChildEntity().getKind()).put("needsKey", true). // !VelvetUtil.isAutoKeyed(link.getChildClass())).
                put("picker", pickerData(link)).put("keyz", childrenData(link, value)).put("candidates", candidatesData(link, value)).build()).collect(Collectors.toList());

        List<Map<String, ?>> singleLinkData = model.singleLinks(kind)
             .stream()
             .map(link -> ImmutableMap.of(
                "edgeKind", link.getKind(),
                "kind", link.getChildEntity().getKind(),
                "needsKey", true,
                // !VelvetUtil.isAutoKeyed(link.getChildClass()))
                "picker", pickerData(link),
                "value", childData(link, value)))
             .collect(Collectors.toList());

        Map<String, Object> jmodel = ImmutableMap.<String, Object> builder()
            .put("kind", kind)
            .put("fields", glass.fields())
            .put("row", row)
            .put("key", key)
            .put("keyField", glass.keyName())
            .put("multiLinks", multiLinkData)
            .put("singleLinks", singleLinkData)
            .build();

        return jmodel;

    }

    private <HK, HV, CK, CV> List<Map<String, Object>> pickerData(ILinkDef<HK, HV, CK, CV> link) {

        Glass<CK, CV> childGlass = Glass.of(link.getChildEntity());
        Glass<HK, HV> hostGlass = Glass.of(link.getHostEntity());

        IBiSingleLinkDef<CK, CV, HK, HV> backLink = (link instanceof IBiSingleLinkDef) ? ((IBiSingleLinkDef<HK, HV, CK, CV>) link).back() : null;

        // TODO: need pagination here as there could be too many records.
        List<Map<String, Object>> candidateList = link.getChildEntity().keys(velvetProvider.get()).stream().map(key -> pickerEntry(hostGlass, childGlass, key, link, backLink)).collect(Collectors.toList());
        return candidateList;
    }

    private <CK, CV, HK, HV> Map<String, Object> pickerEntry(Glass<HK, HV> hostGlass, Glass<CK, CV> childGlass, CK key, ILinkDef<HK, HV, CK, CV> link, IBiSingleLinkDef<CK, CV, HK, HV> backLink) {
        Builder<String, Object> builder = ImmutableMap.<String, Object> builder().put("key", childGlass.keyToDisplay(key));
        if (backLink != null) {
            HK hostKey = backLink.singleKey(velvetProvider.get(), key);
            if (hostKey != null)
                builder.put("hostKey", hostGlass.keyToDisplay(hostKey));
        }
        return builder.build();
    }

    private <HV, CK, CV> List<String> childrenData(IMultiLinkDef<?, HV, CK, CV> multiLink, HV node) {
        Glass<CK, CV> childGlass = Glass.of(multiLink.getChildEntity());
        List<String> childKeyList = multiLink.multi(velvetProvider.get(), node).stream().map(o -> childGlass.getKeyStringForValue(o)).collect(Collectors.toList());
        return childKeyList;
    }

    private <HV, CK, CV> String childData(ISingleLinkDef<?, HV, CK, CV> singleLink, Object hostValue) {
        Glass<CK, CV> childGlass = Glass.of(singleLink.getChildEntity());
        CV childNode = singleLink.single(velvetProvider.get(), (HV) hostValue);
        if (childNode == null)
            return "";
        String childKeyDisplay = childGlass.getKeyStringForValue(childNode);
        return childKeyDisplay;
    }

    private <PK, PV, CK, CV> Object candidatesData(IMultiLinkDef<PK, PV, CK, CV> link, PV entry) {
        // TODO
        // MultiLinkDef - all except mine
        // BiMultiLinkDef - all except mine, mark busy
        // ManyToMany - all except mine, mark busy in another way

        IEntityDef<CK, CV> childEntity = link.getChildEntity();
        Collection<CK> keys = childEntity.keys(velvetProvider.get());
        Glass<CK, CV> glass = Glass.of(childEntity);

        Predicate<Object> filter = (key -> true);
        Predicate<Object> mark = (key -> false);

        /*
         * TODO if (link instanceof BiMultiLinkDef) { BiParentLinkDef<?,?> parentLink = ((BiMultiLinkDef<?,?>)link).back(); filter = key -> !keyObject.equals(parentLink.singleKey(velvetProvider.get(), key)); mark = key ->
         * (parentLink.singleKey(velvetProvider.get(), key) != null); }
         */

        // TODO limit
        List<String> keyStrings = keys.stream().filter(Objects::nonNull).filter(filter).limit(200).map(key -> glass.keyToDisplay(key)).sorted().collect(Collectors.toList());

        return keyStrings;
    }

    @Transactional
    public Map<String, Object> kind(String kind, int offset, int limit) {
        IEntityDef<?, ?> entity = model.getEntity(kind);
        return kind(model, kind, entity, offset, limit);
    }

    private <K, V> Map<String, Object> kind(ViewerDataModel model, String kind, IEntityDef<K, V> entity, int offset, int limit) {

        List<V> objects = range(entity, offset, limit);
        Glass<K, V> glass = Glass.of(entity);

        Collection<String> fields = glass.fields();
        List<Map<String, ?>> rows =

                objects.stream().sorted(this.by(o -> entity.keyOf(o))).map(obj -> glass.renderMap(obj)).collect(Collectors.toList());

        long total = entity.size(velvetProvider.get());

        Map<String, Object> jspmodel = ImmutableMap.<String, Object> builder().put("fields", fields).put("rows", rows).put("keyField", glass.keyName()).put("kind", kind).put("newRecordUrl", "/viewer/submitnew/"
                                                                                                                                                                                              + kind).put("rootUrl", "/viewer").put("baseEditUrl", "/viewer/entry/"
                                                                                                                                                                                                                                                   + kind).put("limit", limit).put("offset", offset).put("total", total).put("lastIndex", Math.min(offset
                                                                                                                                                                                                                                                                                                                                                   + limit, total)).build();

        return jspmodel;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <K, V> List<V> range(IEntityDef<K, V> entity, int offset, int limit) {
        if (entity instanceof ISortableEntityDef) {
            IRangeQuery query = Queries.range(offset, limit);
            return ((ISortableEntityDef) entity).get(velvetProvider.get(), query);
        } else {
            List<K> keys = entity.keys(velvetProvider.get()).subList(offset, offset + limit);
            return entity.get(velvetProvider.get(), keys);
        }
    }

    @SuppressWarnings("unchecked")
    private <T, K> Comparator<T> by(Function<? super T, K> func) {
        return (T u, T v) -> ((Comparable<K>) func.apply(u)).compareTo(func.apply(v));
    }

    @Transactional
    public void submitEdited(ViewerDataModel model, String kind, String key, Map<String, String> map) {
        IEntityDef<?, ?> entity = model.getEntity(kind);
        submitEdited(model, entity, key, map);
    }

    private <K, V> void submitEdited(ViewerDataModel model, IEntityDef<K, V> entity, String key, Map<String, String> map) {
        Glass<K, V> glazz = Glass.of(entity);
        V object = createPojo(glazz, map);
        entity.put(velvetProvider.get(), object);
    }

    private <K, V> K persistNewObject(String key, Glass<K, V> glass, IEntityDef<K, V> entity) {
        K objectKey = null;
        // if (!VelvetUtil.isAutoKeyed(ci.getType())) { // TODO
        objectKey = glass.keyToNative(key);
        V object = createPojo(glass, key);
        entity.put(velvetProvider.get(), object);
        // } else {
        // Object object = ci.instantiate();
        // velvetProvider.get().put(object);
        // objectKey = VelvetUtil.keyOf(object);
        // }
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
         * IslandModel deletor = model.getDeletor(); if (deletor == null) throw new RuntimeException("No deletor found for " + kind);
         *
         * IEntityDef<?, ?> entity = model.getEntity(kind); Glass<?> glass = Glass.of(entity); Object keyObject = glass.keyToNative(key); deletor.deleteByKey(velvetProvider.get(), keyObject, entity.getValueClass());
         */
    }

    @Transactional
    public String submitNew(ViewerDataModel model, String kind, String key, String linkHostKey, String linkEdgeKind) {
        IEntityDef<?, ?> entity = model.getEntity(kind);
        return submitNew(model, entity, key, linkHostKey, linkEdgeKind);
    }

    private <K, V> String submitNew(ViewerDataModel model, IEntityDef<K, V> entity, String key, String linkHostKey, String linkEdgeKind) {
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
         * ILinkDef<?, ?, ?, ?> linkDef = model.getLink(linkEdgeKind); attachToParentHost(model, linkHostKey, linkDef, objectKey);
         */
    }

    private <HK, CK> void attachToParentHost(ViewerDataModel model, String linkHostKey, ILinkDef<HK, ?, CK, ?> linkDef, Object objectKey) {
        Class<?> linkHostClass = linkDef.getHostEntity().getValueClass();
        Glass<HK, ?> glass = Glass.of(linkDef.getHostEntity());
        Object hostKey = glass.keyToNative(linkHostKey);
        linkDef.connectKeys(velvetProvider.get(), (HK) hostKey, (CK) objectKey);
    }

    @Transactional
    public List<? extends Map<String, ?>> kinds() {
        return model.entityNames().stream().map(kind ->
            ImmutableMap.of("kind", kind,
             "count", model.getEntity(kind).size(velvetProvider.get()))

         ).collect(Collectors.toList());
    }

}
