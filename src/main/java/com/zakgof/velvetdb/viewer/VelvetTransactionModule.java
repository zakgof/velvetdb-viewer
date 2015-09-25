package com.zakgof.velvetdb.viewer;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import javax.transaction.Transactional;

import com.google.inject.AbstractModule;
import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.IVelvetEnvironment;
import com.zakgof.db.velvet.VelvetFactory;

public class VelvetTransactionModule extends AbstractModule {
  
  private IVelvetEnvironment velvetEnvironment;
  private ThreadLocal<IVelvet> velvetTL = new ThreadLocal<>();
  
  public VelvetTransactionModule(String velvetUrl) {
    velvetEnvironment = VelvetFactory.open(velvetUrl);
  }

  @Override
  public void configure() {
    
    bind(IVelvet.class).toProvider(() -> getVelvet());
    bind(IVelvetEnvironment.class).toInstance(velvetEnvironment);
    bindInterceptor(any(), annotatedWith(Transactional.class), methodInvocation -> {
      Object[] result = new Object[1];
      try {
        velvetEnvironment.execute(velvet -> {
          velvetTL.set(velvet);        
          result[0] = methodInvocation.proceed();        
        });
        return result[0];
      } finally {
        velvetTL.remove();
      }
    });
  }

  private IVelvet getVelvet() {
    IVelvet velvet = velvetTL.get();
    if (velvet == null)
      throw new RuntimeException("Annotate your method by @Transactional to use IVelvet");
    return velvet;
  }

}
