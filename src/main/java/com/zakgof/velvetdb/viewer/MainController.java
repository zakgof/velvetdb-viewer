package com.zakgof.velvetdb.viewer;

import static spark.Spark.get;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.ImmutableMap;
import com.zakgof.tools.web.pojoeditor.VelvetViewerService;

import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class MainController {
  
  @Inject
  private VelvetViewerService service;
  
  public MainController() {
    
    Spark.before(new VelvetSparkFilter());
    
    ThymeleafTemplateEngine engine = createTemplateEngine();
    
    get("/", (request, response) -> {
      Collection<String> kinds = service.getModel().entityNames();
      return new ModelAndView(ImmutableMap.of("kinds", kinds), "kinds");
    } , engine);
    
    get("/kind/:kind", (request, response) -> {
      String kind = request.params("kind");
      Map<String, Object> model = service.kindTable(kind, 0, 100);
      return new ModelAndView(model, "kind");
    } , engine);
    
    get("/kind/:kind/:offset/:limit", (request, response) -> {
      String kind = request.params("kind");
      Map<String, Object> model = service.kindTable(kind, Integer.parseInt(request.params("offset")), Integer.parseInt(request.params("limit")));
      return new ModelAndView(model, "kind");
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
