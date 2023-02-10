package io.github.bewaremypower;

import java.io.IOException;
import javax.naming.AuthenticationException;
import org.apache.pulsar.broker.ServiceConfiguration;
import org.apache.pulsar.broker.authentication.AuthenticationDataSource;
import org.apache.pulsar.broker.authentication.AuthenticationProvider;
import org.apache.pulsar.broker.authentication.AuthenticationProviderToken;

public class CustomTokenAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationProviderToken provider = new AuthenticationProviderToken();

    @Override
    public void initialize(ServiceConfiguration config) throws IOException {
        // No ops
    }

    @Override
    public String getAuthMethodName() {
        return "token";
    }

    @Override
    public String authenticate(AuthenticationDataSource authData) throws AuthenticationException {
        return provider.authenticate(authData);
    }

    @Override
    public void close() throws IOException {
        // No ops
    }
}
