package io.github.bewaremypower;

import java.io.IOException;
import java.net.SocketAddress;
import javax.naming.AuthenticationException;
import javax.net.ssl.SSLSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.broker.ServiceConfiguration;
import org.apache.pulsar.broker.authentication.AuthenticationDataSource;
import org.apache.pulsar.broker.authentication.AuthenticationProvider;
import org.apache.pulsar.broker.authentication.AuthenticationProviderToken;
import org.apache.pulsar.broker.authentication.AuthenticationState;
import org.apache.pulsar.common.api.AuthData;

@Slf4j
public class CustomTokenAuthenticationProvider implements AuthenticationProvider {

    private static final String NAME = CustomTokenAuthenticationProvider.class.getName();
    private final AuthenticationProviderToken provider = new AuthenticationProviderToken();

    @Override
    public void initialize(ServiceConfiguration config) throws IOException {
        log.info("{} initialize", NAME);
        provider.initialize(config);
    }

    @Override
    public String getAuthMethodName() {
        return "token";
    }

    @Override
    public String authenticate(AuthenticationDataSource authData) throws AuthenticationException {
        // NOTE: You should not print these sensitive info, this log is only for test
        log.info("{} authenticate | peer address: {} | authorization header: {}",
                NAME, authData.getPeerAddress(), authData.getHttpHeader("Authorization"));
        try {
            return provider.authenticate(authData);
        } catch (AuthenticationException e) {
            log.error("Failed to authenticate", e);
            throw e;
        }
    }

    @Override
    public AuthenticationState newAuthState(AuthData authData, SocketAddress remoteAddress, SSLSession sslSession)
            throws AuthenticationException {
        log.info("{} newAuthState");
        return provider.newAuthState(authData, remoteAddress, sslSession);
    }

    @Override
    public void close() throws IOException {
        // No ops
    }
}
