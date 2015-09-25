package com.zakgof.velvetdb.viewer;

import static spark.Spark.get;

import java.util.HashMap;

import javax.inject.Inject;

import com.zakgof.tools.web.pojoeditor.VelvetViewerService;

import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class MainController {
  
  @Inject
  private VelvetViewerService service;
  
  public MainController() {
    
    Spark.before(new VelvetSparkFilter());
    
    get("/", (request, response) -> {
      
      service.
    
      
      return new ModelAndView(new HashMap<>(), "kinds");
       
    } , new ThymeleafTemplateEngine());
    


}
