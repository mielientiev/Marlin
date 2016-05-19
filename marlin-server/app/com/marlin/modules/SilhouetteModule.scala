package com.marlin.modules

import com.google.inject.{AbstractModule, Provides}
import com.marlin.config.DefaultEnv
import com.marlin.db.dao.{MongoUserDao, UserDao}
import com.marlin.service.{UserService, UserServiceImpl}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, PlayCacheLayer, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration

import scala.concurrent.ExecutionContext.Implicits._

//import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}

/**
 * @author ntviet18@gmail.com
 */
class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    //    bind[UnsecuredErrorHandler].to[CustomUnsecuredErrorHandler]
    //    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[UserService].to[UserServiceImpl]
    bind[UserDao].to[MongoUserDao]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

//  /**
//   * Provides the HTTP layer implementation.
//   *
//   * @param client Play's WS client.
//   * @return The HTTP layer implementation.
//   */
//  @Provides
//  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
   * Provides the Silhouette environment.
   *
   * @param userService The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(
    userService: UserService,
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus): Environment[DefaultEnv] = {

    Environment[DefaultEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus)
  }

  /**
   * Provides the social provider registry.
   *
   * @return The Silhouette environment.
   */
  @Provides
  def provideSocialProviderRegistry(): SocialProviderRegistry = {

    SocialProviderRegistry(Seq())
  }

  /**
   * Provides the authenticator service.
   *
   * @param fingerprintGenerator The fingerprint generator implementation.
   * @param idGenerator The ID generator implementation.
   * @param configuration The Play configuration.
   * @param clock The clock instance.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(
    fingerprintGenerator: FingerprintGenerator,
    idGenerator: IDGenerator,
    configuration: Configuration,
    clock: Clock): AuthenticatorService[CookieAuthenticator] = {

    val config = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    new CookieAuthenticatorService(config, None, fingerprintGenerator, idGenerator, clock)
  }

//  /**
//   * Provides the avatar service.
//   *
//   * @param httpLayer The HTTP layer implementation.
//   * @return The avatar service implementation.
//   */
//  @Provides
//  def provideAvatarService(httpLayer: HTTPLayer): AvatarService = new GravatarService(httpLayer)

//  /**
//   * Provides the OAuth1 token secret provider.
//   *
//   * @param configuration The Play configuration.
//   * @param clock The clock instance.
//   * @return The OAuth1 token secret provider implementation.
//   */
//  @Provides
//  def provideOAuth1TokenSecretProvider(configuration: Configuration, clock: Clock): OAuth1TokenSecretProvider = {
//    val settings = configuration.underlying.as[CookieSecretSettings]("silhouette.oauth1TokenSecretProvider")
//    new CookieSecretProvider(settings, clock)
//  }

//  /**
//   * Provides the OAuth2 state provider.
//   *
//   * @param idGenerator The ID generator implementation.
//   * @param configuration The Play configuration.
//   * @param clock The clock instance.
//   * @return The OAuth2 state provider implementation.
//   */
//  @Provides
//  def provideOAuth2StateProvider(idGenerator: IDGenerator, configuration: Configuration, clock: Clock): OAuth2StateProvider = {
//    val settings = configuration.underlying.as[CookieStateSettings]("silhouette.oauth2StateProvider")
//    new CookieStateProvider(settings, idGenerator, clock)
//  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoRepository The auth info repository implementation.
   * @param passwordHasher The default password hasher implementation.
   * @return The credentials provider.
   */
  @Provides
  def provideCredentialsProvider(
    authInfoRepository: AuthInfoRepository,
    passwordHasher: PasswordHasher): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))
  }
}
