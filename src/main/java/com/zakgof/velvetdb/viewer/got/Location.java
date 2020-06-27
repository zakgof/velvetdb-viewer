package com.zakgof.velvetdb.viewer.got;

import com.zakgof.db.velvet.annotation.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Location {

    private final String location;
    private final String sublocation;

    @Key
    @Override
    public String toString() {
        return location + (sublocation == null ? "" : " / " + sublocation);
    }
}
