package com.zakgof.velvetdb.viewer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class VelvetViewer {

  public static Injector start(String velvetUri, ViewerDataModel model, int port) {
    Injector injector = Guice.createInjector(binder -> {
      binder.bind(VelvetViewerService.class);
      binder.bind(MainController.class).toInstance(new MainController(port));
      binder.bind(ViewerDataModel.class).toInstance(model);
    }, new VelvetTransactionModule(velvetUri));
    injector.getInstance(MainController.class);
    return injector;
  }
}
