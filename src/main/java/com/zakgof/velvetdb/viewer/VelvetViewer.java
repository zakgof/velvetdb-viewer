package com.zakgof.velvetdb.viewer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.zakgof.tools.web.pojoeditor.VelvetViewerService;
import com.zakgof.tools.web.pojoeditor.ViewerDataModel;

public class VelvetViewer {
  
  public static Injector start(String velvetUri, ViewerDataModel model) {
    Injector injector = Guice.createInjector(binder -> {
      binder.bind(VelvetViewerService.class).toInstance(new VelvetViewerService(model));
      binder.bind(MainController.class);
    }, new VelvetTransactionModule(velvetUri));    
    injector.getInstance(MainController.class);
    return injector;
  }
}
