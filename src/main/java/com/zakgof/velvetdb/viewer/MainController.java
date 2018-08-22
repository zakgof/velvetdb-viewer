package com.zakgof.velvetdb.viewer;

import static spark.Spark.get;

import javax.inject.Inject;

import com.google.gson.Gson;

import spark.Spark;

public class MainController {

    @Inject
    private VelvetViewerService service;

    public MainController() {

        Spark.staticFileLocation("/website");
        Spark.before( (request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));

        // Spark.before(new VelvetSparkFilter());

        get("/api", (request, response) -> service.allMetadata(), new Gson()::toJson);

        get("/api/kind/:kind", "kind", (request, response) -> {
            String kind = request.params("kind");
            return service.kind(kind, 0, 100);
        }, new Gson()::toJson);

        get("/api/kind/:kind/:offset/:limit", "kind", (request, response) -> {
            String kind = request.params("kind");
            return service.kind(kind, Integer.parseInt(request.params("offset")), Integer.parseInt(request.params("limit")));
        }, new Gson()::toJson);

        get("/api/record/:kind/:id", (request, response) -> {
            String kind = request.params("kind");
            String id = request.params("id");
            return service.record(kind, id);
        }, new Gson()::toJson);

    }

}
