package innowise.bankingApp.auth;

import innowise.bankingApp.auth.model.entity.CredentialsEntity;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialsEntityTest {

    @Test
    void shouldUsePersistedIdentityAndKeepStableHashCode() {
        CredentialsEntity account = new CredentialsEntity();
        CredentialsEntity copy = new CredentialsEntity();

        assertThat(account.equals(account)).isTrue();
        assertThat(account).isNotEqualTo(copy).isNotEqualTo(new Object());
        Set<CredentialsEntity> accounts = new HashSet<>();
        accounts.add(account);

        account.setId(1L);
        assertThat(accounts).contains(account);
        assertThat(account).isNotEqualTo(copy);

        copy.setId(1L);
        assertThat(account).isEqualTo(copy).hasSameHashCodeAs(copy);
        assertThat(accounts).contains(copy);

        copy.setId(2L);
        assertThat(account).isNotEqualTo(copy);
    }
}
