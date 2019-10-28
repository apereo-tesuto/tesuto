# Common-Identity
## Common implementation of OpenID Connect built for CCC Spring services

This package is intended to provide easy access to standardized CCC identity
information, such as user id, roles, eppn, cccid, misCode, and potentially more
as needs grow.

There are also several convenience methods, annotations, and classes being
created for more easily retrieving CCC user information and making requests on
behalf of a user (that is, using a bearer token).

Please see - https://bitbucket.org/cccnext/common-identity/branches/ to identify the current release version as well as
the pom.xml file for the current in-development version and maven coordinates

## Service Accounts

For accessing functionality server-to-server, we have build a few convenient classes to make things much easier.

The first is a ServiceAccountManager, which identifies itself using client_credentials and provides an API for 
retrieving a valid JWT which may be serialized to use as the `Authorization: Bearer <serialized>` header.

The ServiceAccountManager can be used in conjunction with the ccc-web-common code to make authenticated REST calls.
SEE: https://bitbucket.org/cccnext/ccc-web-common/src/develop/

### Example ServiceAccountManager configuration

```java

  @Bean
  public ServiceAccountManager getServiceAccount(
                    @Value("${net.cccnext.openidc.server.url:http://mitre.test/f}") String baseUrl,
                    @Value("${net.cccnext.openidc.client.id:client}") String clientId,
                    @Value("${net.cccnext.openidc.client.secret:secret}") String clientSecret) throws Exception {

        log.info("Using openid client id {} with server {}", clientId, baseUrl);

        try {
            ServiceAccountManager.ServiceAccountManagerBuilder builder = new ServiceAccountManager.ServiceAccountManagerBuilder();
            return builder.baseEndpoint(baseUrl).clientId(clientId).clientSecret(clientSecret).build();
        }
        catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CredentialsException("OIDC Service Account has incorrect credentials. Please check oidc params.");
            }
            throw new Exception("Could not create ServiceAccountManager", e);
        }
    }
    
```

## CryptoBearerFilter
Wiring with Spring Security is fairly simple. To validate that the incoming tokens are from the known issuer, use the 
CryptoBearerFilter. The example here shows building the filter with optional role mapping.

You can supply a comma separated list of cert URLs in order to accept tokens from both a mitre oidc and keycloak oidc provider.

```java

   // map STAFF and FACULTY to admin roles
   private static final ImmutableMap<String, Collection<String>> ROLE_MAP = ImmutableMap.<String, Collection<String>> builder()
                    .put("STAFF", Collections.singletonList("ROLE_ADMIN"))
                    .put("FACULTY", Collections.singletonList("ROLE_ADMIN")).build();

    // Set net.cccnext.openidc.server.jwk.urls as a comma separated list of cert urls
    @Bean
    public CryptoBearerFilter getBearer(@Value("${net.cccnext.openidc.server.jwk.urls}") String[] jwkUrls) {
        CryptoBearerFilter filter = new CryptoBearerFilter(new SimpleMapEnhancer(ROLE_MAP), jwkUrls);
        return filter;
    }
    
```

OR

```java

    // Set net.cccnext.openidc.server.jwk.urls as a comma separated list of cert urls
    @Bean
    public CryptoBearerFilter getBearer(@Value("${net.cccnext.openidc.server.jwk.urls}") String[] jwkUrls) {
        CryptoBearerFilter filter = new CryptoBearerFilter(jwkUrls);
        return filter;
    }
    
```

The filter is then added to a security config (an extension of  org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter). The configuration below
should of course be setup appropriately for your service. Note, the below setup ALSO includes a Spring CorsFilter using 
the default CorsConfigurationSourceImpl from the ccc-web-common library. 

```java

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  CryptoBearerFilter bearerFilter = null;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .addFilterBefore(bearerFilter, BasicAuthenticationFilter.class)
      .addFilterBefore(new CorsFilter(new CorsConfigurationSourceImpl()), BasicAuthenticationFilter.class)
      .authorizeRequests()
        .antMatchers("/ccc/api/v1/service/healthcheck/").permitAll()
        .antMatchers("/metrics").permitAll()
        .antMatchers("/prometheus").permitAll()
        .antMatchers("/swagger/**").permitAll()
        .antMatchers("/health",
                     "/info",
                     "/v1/api-docs",
                     "/configuration/ui",
                     "/swagger-resources",
                     "/configuration/security",
                     "/swagger-ui.html",
                     "/webjars/**"
        ).permitAll()
        .antMatchers(HttpMethod.OPTIONS).permitAll()
        .anyRequest().authenticated();
  }
}

```
