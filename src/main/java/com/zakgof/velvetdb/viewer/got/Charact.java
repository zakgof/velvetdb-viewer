package com.zakgof.velvetdb.viewer.got;

import com.zakgof.db.velvet.annotation.Key;
import com.zakgof.db.velvet.annotation.Kind;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Kind("character")
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = {"name"})
public class Charact {
    @Key
    private final String name;
    private final Gender gender;
    private final String house;

    private final String actor;
    private final String characterLink;
    private final String actorLink;

    @Override
    public String toString() {
        return name;
    }
}
