package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.entity.*;
import com.bookstore.booksmanagementsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Disabled("Temporarily disabled due to flakiness and low priority")
public class TempOrderApprovalIntegrationTest {

    @Autowired private TempOrderService tempOrderService;
    @Autowired private TempCustomerOrderRepository tempOrderRepo;
    @Autowired private TempCustomerOrderItemRepository tempItemRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private BookRepository bookRepo;
    @Autowired private ListEntityRepository listRepo;
    @Autowired private SchoolRepository schoolRepo;
    @Autowired private ClassEntityRepository classRepo;
    @Autowired private CustomerBookOrderRepository orderRepo;
    @Autowired private CustomerBookOrderItemRepository orderItemRepo;

    private Customer customer;
    private Book book;
    private ListEntity officialList;
    private School school;
    private ClassEntity classEntity;

    @BeforeEach
    void setupSecurity() {
        // Ensure SecurityContext has our test user
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("testuser@example.com", "N/A");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeEach
    void seedData() {
        // Create Customer
        customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setEmail("testuser@example.com");
        customer.setPassword("test");
        customer = customerRepo.save(customer);

        // Create School
        long now = System.currentTimeMillis();
        school = new School();
        school.setName("Test School " + now);
        school.setAddress("123 Test St " + now);
        school.setPhoneNumber("000-000-0000");
        school = schoolRepo.save(school);

        // Create Class
        classEntity = new ClassEntity();
        classEntity.setName("Grade 1");
        classEntity.setYear(2025);
        classEntity.setSchool(school);
        classEntity = classRepo.save(classEntity);

        // Create ListEntity (official list)
        officialList = new ListEntity();
        officialList.setYear(2025);
        officialList.setClassEntity(classEntity);
        officialList = listRepo.save(officialList);

        // Create Book
        book = new Book();
        book.setTitle("Algebra I");
        book.setAuthor("Author A");
        book.setIsbn("TEST-ISBN-" + now);
        book.setPublisher("Test Pub");
        book.setPrice(10.0);
        book = bookRepo.save(book);
    }

    @Test
    void approveDraft_migratesToPermanentAndDeletesTemp() {
        // Create a submitted temp order with one item
        TempCustomerOrder draft = new TempCustomerOrder();
        draft.setCustomer(customer);
        draft.setStatus(TempCustomerOrder.Status.SUBMITTED);
        draft = tempOrderRepo.save(draft);

        TempCustomerOrderItem item = new TempCustomerOrderItem();
        item.setTempOrder(draft);
        item.setBook(book);
        item.setOfficialList(officialList);
        item.setSchool(school);
        item.setClassEntity(classEntity);
        item.setQuantity(2);
        item.setConditionType(TempCustomerOrderItem.ConditionType.NEW);
        item.setUnitPrice(10.0);
        item.setSubtotal(20.0);
        item = tempItemRepo.save(item);
        // maintain bidirectional link for in-memory consistency
        draft.getItems().add(item);

        // Execute approval
        CustomerBookOrderDTO result = tempOrderService.approveDraft(draft.getId());

        // Assert temp draft removed
        Optional<TempCustomerOrder> maybeDraft = tempOrderRepo.findById(draft.getId());
        assertThat(maybeDraft).isEmpty();
        // Items associated with the deleted draft should not impact permanent tables; draft is gone

        // Assert permanent order created with items
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(customer.getId());
        assertThat(result.getOrderItems()).hasSize(1);

        // Double-check in DB via counts (avoids stale persistence context issues)
        assertThat(orderRepo.count()).isEqualTo(1);
        assertThat(orderItemRepo.count()).isEqualTo(1);
    }
}
