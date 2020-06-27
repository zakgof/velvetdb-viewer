package com.zakgof.velvetdb.viewer.got;

import com.zakgof.db.velvet.annotation.Key;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = { "season", "episode" })
public class Episode {

    private final int season;
    private final int episode;
    private final LocalDate airDate;
    private final String link;
    private final String title;
    private final String episodeDescription;

    @Key
    @Override
    public String toString() {
        return String.format("S%dE%d", season, episode);
    }

}
