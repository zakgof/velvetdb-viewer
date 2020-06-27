package com.zakgof.velvetdb.viewer.example;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Injector;
import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.IVelvetEnvironment;
import com.zakgof.velvetdb.viewer.VelvetViewer;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.commons.io.IOUtils;

public class Main {

    private static final String VELVET_PATH = "velvetdb://xodus/D:/Pr/mapdbtest/xodus";

    public static void main(String[] args) throws Throwable {

        Injector injector = VelvetViewer.start(VELVET_PATH, Defs.MODEL, 4567);

        injector.getInstance(IVelvetEnvironment.class).execute(velvet -> initDb(velvet));

    }

    private static void initDb(IVelvet velvet) throws IOException {

        if (Defs.BOOK.size(velvet) > 0)
            return;

        String url = "http://openlibrary.org/search.json?subject=java+programming";
        String jsontext = IOUtils.toString(URI.create(url), Charsets.UTF_8);
        JsonElement root = new JsonParser().parse(jsontext);
        JsonArray docs = root.getAsJsonObject().get("docs").getAsJsonArray();
        for (JsonElement bookElement : docs) {
            JsonObject bookObj = bookElement.getAsJsonObject();
            String title = bookObj.get("title").getAsString();
            JsonElement yearElement = bookObj.get("first_publish_year");
            int year = yearElement == null ? 1800 : yearElement.getAsInt();
            List<String> authorNames = strarray(bookObj, "author_name");
            List<String> publisherNames = strarray(bookObj, "publisher");
            List<String> isbns = strarray(bookObj, "isbn");
            if (isbns.isEmpty())
                continue;
            String isbn = isbns.get(0);

            Book book = new Book(isbn, title, year);
            Defs.BOOK.put(velvet, book);
            for (String authorName : authorNames) {
                Person author = new Person(authorName);
                if (!Defs.AUTHOR.containsKey(velvet, authorName)) {
                    Defs.AUTHOR.put(velvet, author);
                }
                Defs.AUTHOR_BOOK.connect(velvet, author, book);
            }
            for (String publisherName : publisherNames) {
                if (!Defs.PUBLISHER.containsKey(velvet, publisherName)) {
                    Defs.PUBLISHER.put(velvet, publisherName);
                }
                Defs.PUBLISHER_BOOK.connect(velvet, publisherName, book);
            }

            System.out.println(book + " " + authorNames);

        }

    }

    private static List<String> strarray(JsonObject obj, String field) {
        JsonElement fieldElement = obj.get(field);
        if (fieldElement == null)
            return Collections.emptyList();
        return StreamSupport.stream(fieldElement.getAsJsonArray().spliterator(), false)
            .map(JsonElement::getAsString)
            .collect(toList());
    }
}