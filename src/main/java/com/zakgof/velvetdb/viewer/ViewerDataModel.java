package com.zakgof.velvetdb.viewer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zakgof.db.velvet.entity.IEntityDef;
import com.zakgof.db.velvet.link.IBiLinkDef;
import com.zakgof.db.velvet.link.ILinkDef;
import com.zakgof.db.velvet.link.IMultiLinkDef;
import com.zakgof.db.velvet.link.ISingleLinkDef;

public class ViewerDataModel {
  
//  
//  public static final IFieldRender FILE_EDITOR = new IFieldRender() {
//    @Override
//    public String view(String value) {
//      return value;
//    }
//    
//    @Override
//    public String edit(String name, String value) {
//      return "<input type=\"file\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeHtml(value) + "\" />";
//    }    
//  };
  
  private Map<String, IEntityDef<?,?>> entities = Maps.newHashMap();
  private Map<String, List<ISingleLinkDef<?,?,?,?>>> singles = Maps.newHashMap();
  private Map<String, List<IMultiLinkDef<?,?,?,?>>> multis = Maps.newHashMap();
  
  public ViewerDataModel(Map<String, IEntityDef<?, ?>> entities, Map<String, List<ISingleLinkDef<?, ?, ?, ?>>> singles, Map<String, List<IMultiLinkDef<?, ?, ?, ?>>> multis) {
    this.entities = entities;
    this.singles = singles;
    this.multis = multis;
  }

  public IEntityDef<?,?> getEntity(String kind) {
    return entities.get(kind);
  }

  public List<IMultiLinkDef<?,?,?,?>> multiLinks(String kind) {
    return multis.getOrDefault(kind, Collections.emptyList());
  }
  
  public List<ISingleLinkDef<?,?,?,?>> singleLinks(String kind) {
    return singles.getOrDefault(kind, Collections.emptyList());
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    
    private Map<String, IEntityDef<?,?>> entities = Maps.newHashMap();
    private Map<String, List<IMultiLinkDef<?,?,?,?>>> multis = Maps.newHashMap();
    private Map<String, List<ISingleLinkDef<?,?,?,?>>> singles = Maps.newHashMap();
    
    private Builder() {
    }
    
    public Builder entity(IEntityDef<?, ?> entity) {
      entities.put(entity.getKind(), entity);
      return this;
    }
    
    public Builder link(ILinkDef<?, ?, ?, ?> link) {
      addOneLink(link);
      if (link instanceof IBiLinkDef)
        addOneLink(((IBiLinkDef<?, ?, ?, ?, ?>)link).back());
      return this;
    }
        
    private void addOneLink(ILinkDef<?, ?, ?, ?> link) {      
      if (link instanceof ISingleLinkDef)
        addSingleLink((ISingleLinkDef<?, ?, ?, ?>) link);
      else if (link instanceof IMultiLinkDef)
        addMultiLink((IMultiLinkDef<?, ?, ?, ?>) link);
    }
    
    public void addSingleLink(ISingleLinkDef<?, ?, ?, ?> link) {
      List<ISingleLinkDef<?, ?, ?, ?>> list = singles.getOrDefault(link.getHostEntity().getKind(), Lists.newArrayList());
      if (list.isEmpty())
        singles.put(link.getHostEntity().getKind(), list);
      list.add(link);      
    }

    private void addMultiLink(IMultiLinkDef<?, ?, ?, ?> link) {
      List<IMultiLinkDef<?, ?, ?, ?>> list = multis.getOrDefault(link.getHostEntity().getKind(), Lists.newArrayList());
      if (list.isEmpty())
        multis.put(link.getHostEntity().getKind(), list);
      list.add(link);
    }
    
    public ViewerDataModel build() {
      return new ViewerDataModel(entities, singles, multis);
    }
    
  }

  public Collection<String> entityNames() {
    return entities.keySet();
  }

}
