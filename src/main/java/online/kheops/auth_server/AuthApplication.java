package online.kheops.auth_server;

import online.kheops.auth_server.token.TokenAuthenticationContext;
import online.kheops.auth_server.token.TokenAuthenticationContextFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/*")
public class AuthApplication extends ResourceConfig {

  public AuthApplication() {
    register(
        new AbstractBinder() {
          @Override
          protected void configure() {
            bindFactory(TokenAuthenticationContextFactory.class)
                .to(TokenAuthenticationContext.class)
                .proxy(true)
                .in(Singleton.class);
          }
        });

    property(
        "jersey.config.server.provider.classnames",
        "org.glassfish.jersey.media.multipart.MultiPartFeature,online.kheops.auth_server.filter.CacheFilterFactory");
    packages(true, "online.kheops.auth_server");
  }
}
