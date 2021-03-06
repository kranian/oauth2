package com.david.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;

/**
 * Created by david100gom on 2017. 9. 4.
 *
 * Github : https://github.com/david100gom
 */
@Configuration
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("firstDataSource")
    DataSource dataSource;

    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    //  Secure object: FilterInvocation: URL: /oauth/check_token; Attributes: [denyAll()]
    // TODO application.yml 의 check-token-access: isAuthenticated() 가 작동안함.확인 필요
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("isAuthenticated()");
    }

    @Bean
    public TokenStore jdbcTokenStore() {
        JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource);
        // user_name 을 username 으로 변경하기 위해 스키마 오버라이드함
        jdbcTokenStore.setInsertAccessTokenSql("insert into oauth_access_token (token_id, token, authentication_id, username, client_id, authentication, refresh_token) values (?, ?, ?, ?, ?, ?, ?)");
        jdbcTokenStore.setSelectAccessTokensFromUserNameAndClientIdSql("select token_id, token from oauth_access_token where username = ? and client_id = ?");
        jdbcTokenStore.setSelectAccessTokensFromUserNameSql("select token_id, token from oauth_access_token where username = ?");
        return jdbcTokenStore;
    }

    // TODO spring security oauth2 만 spring security 에서 사용중인 멤버관련와 분리할 수 없나?
    @Bean
    @Primary
    public JdbcClientDetailsService jdbcClientDetailsService() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        return jdbcClientDetailsService;
    }

}