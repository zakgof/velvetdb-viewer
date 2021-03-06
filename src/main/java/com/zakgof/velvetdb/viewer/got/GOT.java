package com.zakgof.velvetdb.viewer.got;

import com.zakgof.db.velvet.entity.Entities;
import com.zakgof.db.velvet.entity.ISortableEntityDef;
import com.zakgof.db.velvet.link.IBiManyToManyLinkDef;
import com.zakgof.db.velvet.link.IBiMultiLinkDef;
import com.zakgof.db.velvet.link.IMultiLinkDef;
import com.zakgof.db.velvet.link.Links;
import com.zakgof.velvetdb.viewer.ViewerDataModel;

public class GOT {
    public static final ISortableEntityDef<String, Charact> CHARACTER = Entities.sorted(Charact.class);
    public static final ISortableEntityDef<String, Episode> EPISODE = Entities.sorted(Episode.class);
    public static final ISortableEntityDef<?, Location> LOCATION = Entities.sorted(Location.class);

    public static final IBiMultiLinkDef<String, Charact, String, Charact> FATHER_CHILDREN = Links.biMulti(CHARACTER, CHARACTER, "father_children", "father");
    public static final IBiMultiLinkDef<String, Charact, String, Charact> MOTHER_CHILDREN = Links.biMulti(CHARACTER, CHARACTER, "mother_children", "mother");
    public static final IMultiLinkDef<String, Charact, String, Charact> CHARACTER_SIBLING = Links.multi(CHARACTER, CHARACTER, "siblings");
    public static final IBiMultiLinkDef<String, Charact, String, Charact> CHARACTER_KILLS = Links.biMulti(CHARACTER, CHARACTER, "killed", "killed_by");
    public static final IMultiLinkDef<String, Charact, String, Charact> HAD_SEX = Links.multi(CHARACTER, CHARACTER, "had_sex");

    public static final IBiManyToManyLinkDef<String, Episode, String, Charact> EPISODE_CHARACTERS = Links.biManyToMany(EPISODE, CHARACTER, "episode_characters", "appears_in_episodes");
    public static final IBiManyToManyLinkDef<String, Episode, ?, Location> EPISODE_LOCATIONS = Links.biManyToMany(EPISODE, LOCATION, "episode_locations", "location_episodes");


    public static final ViewerDataModel MODEL = ViewerDataModel.builder().
        entity(CHARACTER).
        entity(EPISODE).
        entity(LOCATION).
        link(FATHER_CHILDREN).
        link(MOTHER_CHILDREN).
        link(CHARACTER_SIBLING).
        link(HAD_SEX).
        link(EPISODE_CHARACTERS).
        link(EPISODE_LOCATIONS).
        build();

}
