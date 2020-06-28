package com.zakgof.velvetdb.viewer.got;

import com.zakgof.db.velvet.annotation.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Location {

    private final String location;
    private final String sublocation;

    public Location(String location, String sublocation) {
        super();
        this.location = location == null ? "" : location.trim();
        this.sublocation = sublocation == null ? "" : sublocation.trim();
    }

    @Key
    @Override
    public String toString() {
        return location + (sublocation.isEmpty() ? "" : " / " + sublocation);
    }

}
