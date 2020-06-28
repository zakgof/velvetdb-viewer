package com.zakgof.velvetdb.viewer.got;

import static java.util.stream.Collectors.toSet;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.Injector;
import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.IVelvetEnvironment;
import com.zakgof.db.velvet.VelvetFactory;
import com.zakgof.velvetdb.viewer.VelvetViewer;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.apache.commons.io.IOUtils;

public class ExampleMain {

    private static final Path VELVET_PATH = Paths.get(System.getProperty("user.home"), ".velvetdb-viewer-example", "1.0");
    private static final String VELVET_URL = VelvetFactory.urlFromPath("xodus", VELVET_PATH);

    public static void main(String[] args) throws Throwable {
        Injector injector = VelvetViewer.start(VELVET_URL, GOT.MODEL, 4567);
        injector.getInstance(IVelvetEnvironment.class).execute(velvet -> initDb(velvet));
    }

    private static void initDb(IVelvet velvet) throws IOException {
        if (GOT.CHARACTER.size(velvet) > 0)
            return;
        loadGOTDatabase(velvet);
    }

    private static void loadGOTDatabase(IVelvet velvet) throws IOException {

        System.out.println("Loading test database...");
        // Gender
        JsonElement genderRoot = loadJson("https://raw.githubusercontent.com/jeffreylancaster/game-of-thrones/master/data/characters-gender-all.json");
        Set<String> males = StreamSupport.stream(jsonArray(genderRoot, "male").spliterator(), false).map(JsonElement::getAsString).collect(toSet());
        Set<String> females = StreamSupport.stream(jsonArray(genderRoot, "female").spliterator(), false).map(JsonElement::getAsString).collect(toSet());
        JsonElement charRoot = loadJson("https://raw.githubusercontent.com/jeffreylancaster/game-of-thrones/master/data/characters.json");

        // Pass 1 : Characters
        for (JsonElement characterElement : jsonArray(charRoot, "characters")) {
            String name = jsonStr(characterElement, "characterName");
            JsonElement houses = jsonElement(characterElement, "houseName");
            String house = (houses != null && houses.isJsonArray()) ? houses.getAsJsonArray().get(0).getAsString() : jsonStr(characterElement, "houseName");
            String actor = jsonStr(characterElement, "actorName");
            String characterLink = jsonStr(characterElement, "characterLink");
            String actorLink = jsonStr(characterElement, "actorLink");
            Gender gender = gender(name, males, females);
            Charact ch = new Charact(name, gender, house, actor, characterLink, actorLink);
            GOT.CHARACTER.put(velvet, ch);
            System.out.println("  " + ch);
        }

        // Pass 2 : Character relations
        for (JsonElement characterElement : jsonArray(charRoot, "characters")) {
            String name = jsonStr(characterElement, "characterName");
            for (JsonElement victim : jsonArray(characterElement, "killed")) {
                String victimName = victim.getAsString();
                if (GOT.CHARACTER.containsKey(velvet, victimName)) {
                    GOT.CHARACTER_KILLS.connectKeys(velvet, name, victimName);
                }
            }
            for (JsonElement sibling : jsonArray(characterElement, "siblings")) {
                String siblingName = sibling.getAsString();
                if (GOT.CHARACTER.containsKey(velvet, siblingName)) {
                    GOT.CHARACTER_SIBLING.connectKeys(velvet, name, siblingName);
                }
            }
            for (JsonElement parent : jsonArray(characterElement, "parents")) {
                String parentName = parent.getAsString();
                if (GOT.CHARACTER.containsKey(velvet, parentName)) {
                    if (gender(parentName, males, females) == Gender.Male)
                        GOT.FATHER_CHILDREN.connectKeys(velvet, parentName, name);
                    if (gender(parentName, males, females) == Gender.Female)
                        GOT.MOTHER_CHILDREN.connectKeys(velvet, parentName, name);
                }
            }
        }

        // Episodes
        JsonElement epiRoot = loadJson("https://raw.githubusercontent.com/jeffreylancaster/game-of-thrones/master/data/episodes.json");
        for (JsonElement episodeElement : jsonArray(epiRoot, "episodes")) {

            int season = jsonElement(episodeElement, "seasonNum").getAsInt();
            int episode = jsonElement(episodeElement, "episodeNum").getAsInt();
            LocalDate airDate = LocalDate.parse(jsonStr(episodeElement, "episodeAirDate"));
            String link = jsonStr(episodeElement, "episodeLink");
            String title = jsonStr(episodeElement, "episodeTitle");
            String episodeDescription = jsonStr(episodeElement, "episodeDescription");

            Episode epi = new Episode(season, episode, airDate, link, title, episodeDescription);
            GOT.EPISODE.put(velvet, epi);
            System.out.println("  Episode " + epi);

            Set<String> characters = new HashSet<>();
            Set<Location> locations = new HashSet<>();

            for (JsonElement sceneElement : jsonArray(episodeElement, "scenes")) {
                String location = jsonStr(sceneElement, "location");
                String subLocation = jsonStr(sceneElement, "subLocation");
                locations.add(new Location(location, subLocation));
                for (JsonElement sceneCharElement : jsonArray(sceneElement, "characters")) {
                    String charName = jsonStr(sceneCharElement, "name");
                    if (GOT.CHARACTER.containsKey(velvet, charName)) {
                        characters.add(charName);
                        for (JsonElement sexPartner : jsonArray(sceneCharElement, "sex/with")) {
                            String partnerName = sexPartner.getAsString();
                            if (GOT.CHARACTER.containsKey(velvet, partnerName)) {
                                GOT.HAD_SEX.connectKeys(velvet, charName, partnerName);
                                GOT.HAD_SEX.connectKeys(velvet, partnerName, charName);
                            }
                        }
                    }
                }
            }
            for (Location loc : locations) {
                GOT.LOCATION.put(velvet, loc);
                GOT.EPISODE_LOCATIONS.connect(velvet, epi, loc);
            }
            for (String ch : characters) {
                GOT.EPISODE_CHARACTERS.connectKeys(velvet, epi.toString(), ch);
            }

        }
        System.out.println("Database loaded.");
    }

    private static Gender gender(String name, Set<String> males, Set<String> females) {
        return males.contains(name) ? Gender.Male : (females.contains(name) ? Gender.Female : Gender.Unknown);
    }

    private static JsonElement loadJson(String url) throws IOException {
        String jsontext = IOUtils.toString(URI.create(url), Charsets.UTF_8);
        return new JsonParser().parse(jsontext);
    }

    private static String jsonStr(JsonElement json, String query) {
        json = jsonElement(json, query);
        return json == null ? null : json.getAsString();
    }

    public static JsonArray jsonArray(JsonElement json, String query) {
        json = jsonElement(json, query);
        return json == null ? new JsonArray() : json.getAsJsonArray();
    }

    private static JsonElement jsonElement(JsonElement json, String query) {
        String[] frags = query.split("/");
        for (String frag : frags) {
            if (json == null || json.isJsonNull())
                return null;
            json = json.getAsJsonObject().get(frag);
        }
        return json;
    }
}