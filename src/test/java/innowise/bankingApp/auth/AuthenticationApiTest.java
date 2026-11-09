package innowise.bankingApp.auth;

import innowise.bankingApp.auth.model.Role;
import innowise.bankingApp.auth.model.dto.request.CreateCredentialsDto;
import innowise.bankingApp.auth.model.dto.request.LoginDto;
import innowise.bankingApp.auth.model.dto.request.RefreshTokenDto;
import innowise.bankingApp.auth.model.dto.response.ResponseTokenPairDto;
import innowise.bankingApp.auth.model.entity.CredentialsEntity;
import innowise.bankingApp.auth.repository.CredentialsRepository;
import innowise.bankingApp.auth.service.AuthService;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationApiTest {

    private static final String PASSWORD = "test-password-123";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private AuthService authService;
    @Autowired private UserRepository users;
    @Autowired private CredentialsRepository credentials;
    @Autowired private PasswordEncoder passwordEncoder;

    private final List<UserEntity> createdUsers = new ArrayList<>();
    private UserEntity user;
    private String login;

    @BeforeEach
    void createUser() {
        user = saveUser();
        login = "member-" + UUID.randomUUID();
        authService.saveCredentials(new CreateCredentialsDto(user.getId(), login, PASSWORD, Role.USER));
    }

    @AfterEach
    void removeTestAccounts() {
        for (UserEntity created : createdUsers) {
            credentials.findByUserId(created.getId()).ifPresent(credentials::delete);
            users.deleteById(created.getId());
        }
    }

    @Test
    void shouldLoginWithNormalizedLoginAndValidateAccessToken() throws Exception {
        ResponseTokenPairDto tokens = readTokens(login("  " + login.toUpperCase(Locale.ROOT) + " ", PASSWORD));

        assertThat(tokens.tokenType()).isEqualTo("Bearer");
        assertThat(tokens.expiresIn()).isPositive();
        assertThat(tokens.accessToken()).isNotEqualTo(tokens.refreshToken());

        mvc.perform(bearer(get("/api/auth/validate"), tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.expiresAt").exists());

        mvc.perform(bearer(get("/api/me"), tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrong-password", "unknown-login", "overlong-utf8"})
    void shouldReturnTheSameErrorForInvalidLogin(String reason) throws Exception {
        String inputLogin = reason.equals("unknown-login") ? "missing-" + UUID.randomUUID() : login;
        String password = switch (reason) {
            case "unknown-login" -> PASSWORD;
            case "overlong-utf8" -> "\u0105".repeat(37);
            default -> "incorrect-password";
        };

        login(inputLogin, password)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid credentials or token"));
        assertThat(account().getRefreshTokenHash()).isNull();
    }

    @Test
    void shouldRotateRefreshTokenAndRejectReplayWithoutInvalidatingTheNewToken() throws Exception {
        ResponseTokenPairDto first = readTokens(login(login, PASSWORD));
        String firstHash = account().getRefreshTokenHash();
        assertThat(firstHash).matches("[0-9a-f]{64}").isNotEqualTo(first.refreshToken());

        ResponseTokenPairDto next = readTokens(refresh(first.refreshToken()));

        assertThat(next.refreshToken()).isNotEqualTo(first.refreshToken());
        assertThat(account().getRefreshTokenHash()).isNotEqualTo(firstHash);
        refresh(first.refreshToken()).andExpect(status().isUnauthorized());
        refresh(next.refreshToken()).andExpect(status().isOk());
    }

    @Test
    void shouldRejectWrongTokenPurposeMissingAndMalformedTokens() throws Exception {
        ResponseTokenPairDto tokens = readTokens(login(login, PASSWORD));

        refresh(tokens.accessToken()).andExpect(status().isUnauthorized());
        refresh("malformed-token").andExpect(status().isUnauthorized());
        mvc.perform(bearer(get("/api/auth/validate"), tokens.refreshToken()))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    @Test
    void shouldRejectLoginAccessAndRefreshAfterUserDeactivation() throws Exception {
        ResponseTokenPairDto tokens = readTokens(login(login, PASSWORD));
        user.setActive(false);
        users.saveAndFlush(user);

        login(login, PASSWORD).andExpect(status().isUnauthorized());
        refresh(tokens.refreshToken()).andExpect(status().isUnauthorized());
        mvc.perform(bearer(get("/api/auth/validate"), tokens.accessToken()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldRejectIssuedTokensAfterCredentialsAreDeletedOrRoleChanges(boolean delete) throws Exception {
        ResponseTokenPairDto tokens = readTokens(login(login, PASSWORD));
        CredentialsEntity account = account();
        if (delete) {
            credentials.delete(account);
        } else {
            account.setRole(Role.ADMIN);
            credentials.saveAndFlush(account);
        }

        refresh(tokens.refreshToken()).andExpect(status().isUnauthorized());
        mvc.perform(bearer(get("/api/auth/validate"), tokens.accessToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldOnlyLetAdminCreateCredentialsAndStoreBcryptHash() throws Exception {
        UserEntity target = saveUser();
        String targetLogin = "New-" + UUID.randomUUID();
        CreateCredentialsDto dto = new CreateCredentialsDto(target.getId(), targetLogin, PASSWORD, Role.USER);

        mvc.perform(json(post("/api/auth/credentials"), dto)).andExpect(status().isUnauthorized());
        String userToken = readTokens(login(login, PASSWORD)).accessToken();
        mvc.perform(bearer(json(post("/api/auth/credentials"), dto), userToken))
                .andExpect(status().isForbidden());
        assertThat(credentials.findByUserId(target.getId())).isEmpty();

        String admin = adminToken();
        mvc.perform(bearer(json(post("/api/auth/credentials"), dto), admin))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(target.getId()))
                .andExpect(jsonPath("$.login").value(targetLogin.toLowerCase(Locale.ROOT)))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        String hash = credentials.findByUserId(target.getId()).orElseThrow().getPasswordHash();
        assertThat(hash).isNotEqualTo(PASSWORD).isNotEqualTo(account().getPasswordHash());
        assertThat(passwordEncoder.matches(PASSWORD, hash)).isTrue();
        mvc.perform(bearer(json(post("/api/auth/credentials"), dto), admin))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectCredentialsForMissingUserAndPasswordAboveBcryptByteLimit() throws Exception {
        String token = adminToken();
        UserEntity target = saveUser();

        CreateCredentialsDto missingUser = new CreateCredentialsDto(Long.MAX_VALUE, "missing.user", PASSWORD, Role.USER);
        mvc.perform(bearer(json(post("/api/auth/credentials"), missingUser), token))
                .andExpect(status().isNotFound());

        CreateCredentialsDto longPassword = new CreateCredentialsDto(
                target.getId(), "long.password", "\u0105".repeat(37), Role.USER);
        mvc.perform(bearer(json(post("/api/auth/credentials"), longPassword), token))
                .andExpect(status().isBadRequest());
        assertThat(credentials.findByUserId(target.getId())).isEmpty();
    }

    private UserEntity saveUser() {
        UserEntity entity = new UserEntity();
        entity.setName("Alex");
        entity.setSurname("Tester");
        entity.setBirthDate(LocalDate.of(2000, 1, 1));
        entity.setEmail(UUID.randomUUID() + "@example.com");
        entity.setActive(true);
        UserEntity saved = users.saveAndFlush(entity);
        createdUsers.add(saved);
        return saved;
    }

    private String adminToken() throws Exception {
        String adminLogin = "admin-" + UUID.randomUUID();
        authService.saveCredentials(new CreateCredentialsDto(saveUser().getId(), adminLogin, PASSWORD, Role.ADMIN));
        return readTokens(login(adminLogin, PASSWORD)).accessToken();
    }

    private CredentialsEntity account() {
        return credentials.findByUserId(user.getId()).orElseThrow();
    }

    private ResultActions login(String name, String password) throws Exception {
        return mvc.perform(json(post("/api/auth/login"), new LoginDto(name, password)));
    }

    private ResultActions refresh(String token) throws Exception {
        return mvc.perform(json(post("/api/auth/refresh"), new RefreshTokenDto(token)));
    }

    private ResponseTokenPairDto readTokens(ResultActions result) throws Exception {
        String body = result.andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andReturn().getResponse().getContentAsString();
        return mapper.readValue(body, ResponseTokenPairDto.class);
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, Object body) {
        return request.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body));
    }

    private static MockHttpServletRequestBuilder bearer(MockHttpServletRequestBuilder request, String token) {
        return request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
