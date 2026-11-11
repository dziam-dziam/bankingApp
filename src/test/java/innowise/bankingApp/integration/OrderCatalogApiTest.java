package innowise.bankingApp.integration;

import innowise.bankingApp.model.dto.request.CreateItemDto;
import innowise.bankingApp.model.dto.response.ResponseItemDto;
import innowise.bankingApp.model.entity.ItemEntity;
import innowise.bankingApp.model.entity.OrderEntity;
import innowise.bankingApp.model.entity.OrderItemEntity;
import innowise.bankingApp.model.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
@Transactional
class OrderCatalogApiTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @PersistenceContext(unitName = "banking")
    private EntityManager entityManager;

    @Test
    void shouldCreateUpdateAndDeleteUnusedCatalogItem() throws Exception {
        String response = mvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateItemDto("Free sample", BigDecimal.ZERO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Free sample"))
                .andExpect(jsonPath("$.price").value(0))
                .andReturn().getResponse().getContentAsString();
        Long id = mapper.readValue(response, ResponseItemDto.class).id();

        mvc.perform(put("/api/items/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Notebook","price":12.50}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        flushAndClear();
        mvc.perform(get("/api/items/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.price").value(12.50));

        mvc.perform(delete("/api/items/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/items/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void shouldPageAndSortCatalogItems() throws Exception {
        ItemEntity cheaper = item("Pen", "2.00");
        ItemEntity costlier = item("Book", "12.00");
        flushAndClear();

        mvc.perform(get("/api/items").param("size", "1").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(costlier.getId()));
        mvc.perform(get("/api/items").param("size", "1").param("page", "1").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cheaper.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", """
            {"name":"Item","price":-1}
            """, """
            {"name":"Item","price":1.234}
            """})
    void shouldRejectInvalidCatalogDataWithoutWritingChanges(String body) throws Exception {
        ItemEntity existing = item("Original", "5.00");
        flushAndClear();

        mvc.perform(post("/api/items").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/items/{id}", existing.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertThat(entityManager.find(ItemEntity.class, existing.getId()).getName()).isEqualTo("Original");
        assertThat(entityManager.createQuery("select count(i) from ItemEntity i", Long.class).getSingleResult())
                .isEqualTo(1L);
    }

    @Test
    void shouldReturnNotFoundForMissingOrdersAndItems() throws Exception {
        mvc.perform(get("/api/orders/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(get("/api/items/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(put("/api/items/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Item","price":1.00}
                                """))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/items/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void shouldListOrdersFromDifferentUsersAndHideDeletedOrders() throws Exception {
        // given
        OrderEntity first = order(owner(), "NEW", false);
        OrderEntity deleted = order(owner(), "CANCELLED", true);
        OrderEntity second = order(owner(), "PROCESSING", false);
        flushAndClear();

        // when / then
        mvc.perform(get("/api/orders").param("size", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(first.getId()))
                .andExpect(jsonPath("$.content[0].userId").value(first.getUser().getId()));

        mvc.perform(get("/api/orders").param("size", "1").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(second.getId()))
                .andExpect(jsonPath("$.content[0].userId").value(second.getUser().getId()));

        mvc.perform(get("/api/orders/{id}", deleted.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReadOrderDetailsWithItemsSortedByPositionId() throws Exception {
        OrderEntity order = order(owner(), "NEW", false);
        OrderItemEntity first = line(order, item("Book", "10.00"), 2);
        OrderItemEntity second = line(order, item("Pen", "5.00"), 1);
        flushAndClear();

        mvc.perform(get("/api/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(order.getUser().getId()))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalPrice").value(25))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].id").value(first.getId()))
                .andExpect(jsonPath("$.items[0].itemId").value(first.getItem().getId()))
                .andExpect(jsonPath("$.items[0].itemName").value("Book"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[1].id").value(second.getId()));
    }

    @Test
    void shouldRejectDeletionOfItemReferencedByAnOrder() throws Exception {
        OrderEntity order = order(owner(), "NEW", false);
        ItemEntity item = item("Book", "12.50");
        line(order, item, 2);
        flushAndClear();

        mvc.perform(delete("/api/items/{id}", item.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        assertThat(entityManager.find(ItemEntity.class, item.getId())).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/orders", "/api/orders/1", "/api/items", "/api/items/1"})
    void shouldRejectAnonymousAndNonAdminRequests(String path) throws Exception {
        mvc.perform(get(path).with(anonymous())).andExpect(status().isUnauthorized());
        mvc.perform(get(path).with(user("customer").roles("USER"))).andExpect(status().isForbidden());
    }

    private UserEntity owner() {
        UserEntity user = new UserEntity();
        user.setName("Order");
        user.setSurname("Customer");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail(UUID.randomUUID() + "@example.com");
        user.setActive(true);
        entityManager.persist(user);
        return user;
    }

    private ItemEntity item(String name, String price) {
        ItemEntity item = new ItemEntity();
        item.setName(name);
        item.setPrice(new BigDecimal(price));
        entityManager.persist(item);
        return item;
    }

    private OrderEntity order(UserEntity owner, String status, boolean deleted) {
        OrderEntity order = new OrderEntity();
        order.setUser(owner);
        order.setStatus(status);
        order.setDeleted(deleted);
        order.setTotalPrice(new BigDecimal("25.00"));
        entityManager.persist(order);
        return order;
    }

    private OrderItemEntity line(OrderEntity order, ItemEntity item, int quantity) {
        OrderItemEntity line = new OrderItemEntity();
        line.setQuantity(quantity);
        order.addOrderItem(line);
        item.addOrderItem(line);
        entityManager.persist(line);
        return line;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
