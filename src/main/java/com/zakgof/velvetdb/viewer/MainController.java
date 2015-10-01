package com.zakgof.velvetdb.viewer;

import static spark.Spark.get;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.ImmutableMap;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class MainController {
  
  private static final String ROOT_URL = "/";
  
  @Inject
  private VelvetViewerService service;
  
  private ThymeleafTemplateEngine engine = createTemplateEngine();
  
  public MainController() {
    
    // Spark.before(new VelvetSparkFilter());
    
    get("/", (request, response) -> {
      Collection<String> kinds = service.getModel().entityNames();
      return new ModelAndView(ImmutableMap.of("kinds", kinds), "kinds");
    } , engine);
    
    process("/kind/:kind", "kind", (request, response) -> {
      String kind = request.params("kind");
      return service.kind(kind, 0, 100);      
    });
       
    process("/kind/:kind/:offset/:limit", "kind", (request, response) -> {
      String kind = request.params("kind");
      return service.kind(kind, Integer.parseInt(request.params("offset")), Integer.parseInt(request.params("limit")));
    });
    
    process("/record/:kind/:id", "record", (request, response) -> {
      String kind = request.params("kind");
      String id = request.params("id");
      return service.record(kind, id);
    });
    
  }
  
  @FunctionalInterface
  interface IProcessor{
    Map<String, Object> process(Request request, Response response);
  }

  private void process(String template, String viewName, IProcessor processor) {
    get(template, (request, response) -> {      
      Map<String, Object> model = processor.process(request, response);
      Map<String, Object> all = new HashMap<>(model);
      all.put("rootUrl", ROOT_URL);
      return new ModelAndView(all, viewName);
    } , engine);
  }

  // Debug mode
  private ThymeleafTemplateEngine createTemplateEngine() {
    TemplateResolver defaultTemplateResolver =
        new TemplateResolver();
    defaultTemplateResolver.setTemplateMode("XHTML");
    defaultTemplateResolver.setPrefix("templates/");
    defaultTemplateResolver.setSuffix(".html");
    defaultTemplateResolver.setCacheTTLMs(1L);
    defaultTemplateResolver.setResourceResolver(new ClassLoaderResourceResolver());
    ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine(defaultTemplateResolver);
    return engine;
  }


}
