package com.zakgof.velvetdb.viewer;


/*

import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Maps;
import com.zakgof.db.velvet.VelvetUtil;
import com.zakgof.db.velvet.old.BiMultiLinkDef;
import com.zakgof.db.velvet.old.IslandModel;
import com.zakgof.tools.web.FieldInfo;
import com.zakgof.tools.web.FieldType;



public class PojoEditorModel {

  public static final IFieldEditor DEFAULT_EDITOR = new IFieldEditor() {
    
    @Override
    public String view(String value) {
      return value;
    }
    
    @Override
    public String edit(String name, String value) {
      return "<input type=\"text\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeHtml4(value) + "\" />";
    }
  };
  
  public static final IFieldEditor FILE_EDITOR = new IFieldEditor() {
    @Override
    public String view(String value) {
      return value;
    }
    
    @Override
    public String edit(String name, String value) {
      return "<input type=\"file\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeHtml4(value) + "\" />";
    }    
  };
  
  public static IFieldEditor getDefaultEditor(String type) {
    if (type.equals(FieldType.BYTEARRAY.name()))
      return FILE_EDITOR;
    else
      return DEFAULT_EDITOR;
  }
  
  public static class EntityEditorModel {
    private EntityEditorModel(Class<?> clazz, String kind) {
      this.clazz = clazz;
      this.kind = kind;
    }
    private Class<?> clazz;
    private String kind;
    private Map<String, IFieldEditor> fieldEditors = Maps.newHashMap();
    public IslandModel deletor;
    public Map<String, BiMultiLinkDef<?,?>> valuePickers = Maps.newHashMap();
    
    public IFieldEditor editor(FieldInfo fieldInfo) {
      return fieldEditors.getOrDefault(fieldInfo.getName(), getDefaultEditor(fieldInfo.getType()));
    }

    public IslandModel getDeletor() {
      return deletor;
    }
    
    public String getPicker(String edgeKind) {
      BiMultiLinkDef<?, ?> biMultiLinkDef = valuePickers.get(edgeKind);      
      return (biMultiLinkDef != null) ? biMultiLinkDef.getKind() : null;
    }
  }
  
  private Map<String, EntityEditorModel> entities = Maps.newHashMap();
  
  public EntityEditorModel entity(String kind) {    
    return entities.get(kind);
  }

  public static Builder builder() {
    return new Builder();
  }
  
  public static IFieldEditor richText() {    
    return new IFieldEditor() {
      
      @Override
      public String view(String value) {
        return value.substring(0, Math.min(value.length(), 30));
      }
      
      @Override
      public String edit(String name, String value) {
        return "<textarea style=\"height: 500px\" class=\"span12 rich\" name=\"" + name + "\">" + StringEscapeUtils.escapeHtml4(value) + "</textarea>";
      }
    };
        
  }

  public static class Builder {
    
    private final PojoEditorModel model = new PojoEditorModel();
    
    public PojoEditorModel build() {
      return model;
    }

    public EntityBuilder entity(Class<?> clazz) {
      return new EntityBuilder(clazz, VelvetUtil.kindOf(clazz));
    }
    
    public class EntityBuilder {
      
      private final EntityEditorModel model;
      
      public EntityBuilder(Class<?> clazz, String kind) {        
        model = new EntityEditorModel(clazz, kind);
      }

      public EntityBuilder edit(String fieldName, IFieldEditor editor) {
        model.fieldEditors.put(fieldName, editor);  
        return this;
      }

      public Builder done() {
        Builder.this.model.entities.put(model.kind, model);
        return Builder.this;
      }

      public EntityBuilder deleteWith(IslandModel island) {
        model.deletor = island;
        return this;  
      }

      public EntityBuilder deleteSimple() {
        model.deletor = IslandModel.builder().entity(model.clazz).done().build();
        return this;
      }

      public EntityBuilder valuePicker(BiMultiLinkDef<?, ?> parent) {
        model.valuePickers.put(parent.back().getKind(), parent);
        return this;
      }
      
    }
    
  }


}
*/
