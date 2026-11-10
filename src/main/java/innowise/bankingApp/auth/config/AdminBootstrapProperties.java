package innowise.bankingApp.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.bootstrap-admin")
public record AdminBootstrapProperties(
        Long userId,
        String login,
        String password
) {
    @Override
    public String toString() {
        return "AdminBootstrapProperties[userId=" + userId
                + ", login=" + login + ", password=***]";
    }
}