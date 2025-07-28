package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderItemDTO;
import com.bookstore.booksmanagementsystem.entity.*;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.*;
import com.bookstore.booksmanagementsystem.service.CustomerBookOrderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerBookOrderServiceImpl implements CustomerBookOrderService {

    private final CustomerBookOrderRepository customerBookOrderRepository;
    private final CustomerBookOrderItemRepository customerBookOrderItemRepository;
    private final CustomerRepository customerRepository;
    private final ListEntityRepository listEntityRepository;
    private final SchoolRepository schoolRepository;
    private final ClassEntityRepository classEntityRepository;
    private final ListBookRepository listBookRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public CustomerBookOrderServiceImpl(CustomerBookOrderRepository customerBookOrderRepository,
                                        CustomerBookOrderItemRepository customerBookOrderItemRepository, CustomerRepository customerRepository,
                                        ListEntityRepository listEntityRepository, SchoolRepository schoolRepository,
                                        ClassEntityRepository classEntityRepository, ListBookRepository listBookRepository,
                                        BookRepository bookRepository, ModelMapper modelMapper) {
        this.customerBookOrderRepository = customerBookOrderRepository;
        this.customerBookOrderItemRepository = customerBookOrderItemRepository;
        this.customerRepository = customerRepository;
        this.listEntityRepository = listEntityRepository;
        this.schoolRepository = schoolRepository;
        this.classEntityRepository = classEntityRepository;
        this.listBookRepository = listBookRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CustomerBookOrderDTO createCustomerBookOrder(CustomerBookOrderDTO orderDTO) {
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDTO.getCustomerId()));

        if (orderDTO.getOrderItems() == null || orderDTO.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one order item.");
        }

        for (CustomerBookOrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            Long schoolId = itemDTO.getSchoolId();
            Long classId = itemDTO.getClassId();
            Long listId = itemDTO.getOfficialListId();

            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

            ClassEntity classEntity = classEntityRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

            ListEntity officialList = listEntityRepository.findById(listId)
                    .orElseThrow(() -> new ResourceNotFoundException("Official List", "id", listId));

            if (!officialList.getClassEntity().getId().equals(classId)) {
                throw new IllegalArgumentException(
                        "Official list does not match the class ID provided for list ID: " + listId);
            }

            if (!officialList.getClassEntity().getSchool().getId().equals(schoolId)) {
                throw new IllegalArgumentException(
                        "Class's school does not match the school ID provided for list ID: " + listId);
            }
        }

        CustomerBookOrder order = new CustomerBookOrder();

        if (orderDTO.getOrderItems() != null && !orderDTO.getOrderItems().isEmpty()) {
            CustomerBookOrderItemDTO firstItem = orderDTO.getOrderItems().iterator().next();
            Long listId = firstItem.getOfficialListId();
            Long schoolId = firstItem.getSchoolId();
            Long classId = firstItem.getClassId();

            boolean exists = customerBookOrderRepository.findByCustomerIdAndOfficialListIdAndSchoolIdAndClassEntityId(
                    orderDTO.getCustomerId(), listId, schoolId, classId).isPresent();
            if (exists) {
                throw new DuplicateResourceException(
                        "Order with the same customer, official list, school, and class already exists");
            }

            ListEntity officialList = listEntityRepository.findById(listId)
                    .orElseThrow(() -> new ResourceNotFoundException("Official List", "id", listId));
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));
            ClassEntity classEntity = classEntityRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

            order.setCustomer(customer);
            order.setOfficialList(officialList);
            order.setSchool(school);
            order.setClassEntity(classEntity);
        } else {
            order.setCustomer(customer);
        }

        order.setOrderDate(LocalDateTime.now());

        CustomerBookOrder savedOrder = customerBookOrderRepository.save(order);

        Set<CustomerBookOrderItem> orderItems = orderDTO.getOrderItems().stream().map(itemDTO -> {
            Long listIdLocal = itemDTO.getOfficialListId();
            boolean bookInList = listBookRepository.findByListEntityIdAndBookId(listIdLocal, itemDTO.getBookId())
                    .isPresent();
            if (!bookInList) {
                throw new ResourceNotFoundException("Book", "id in official list", itemDTO.getBookId());
            }
            Book book = bookRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", itemDTO.getBookId()));
            CustomerBookOrderItem item = new CustomerBookOrderItem();
            item.setCustomerBookOrder(savedOrder);
            item.setBook(book);
            item.setQuantity(itemDTO.getQuantity());
            item.setConditionType(itemDTO.getConditionType() != null
                    ? CustomerBookOrderItem.BookCondition.valueOf(itemDTO.getConditionType().name())
                    : CustomerBookOrderItem.BookCondition.NEW);
            Long listId = itemDTO.getOfficialListId();
            Long schoolId = itemDTO.getSchoolId();
            Long classId = itemDTO.getClassId();
            if (listId != null) {
                ListEntity officialList = listEntityRepository.findById(listId)
                        .orElseThrow(() -> new ResourceNotFoundException("Official List", "id", listId));
                item.setOfficialList(officialList);
            }
            if (schoolId != null) {
                School school = schoolRepository.findById(schoolId)
                        .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));
                item.setSchool(school);
            }
            if (classId != null) {
                ClassEntity classEntity = classEntityRepository.findById(classId)
                        .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));
                item.setClassEntity(classEntity);
            }
            double price = book.getPrice();
            if (item.getConditionType() == CustomerBookOrderItem.BookCondition.USED) {
                price = price * 0.7;
            }
            return item;
        }).collect(Collectors.toSet());
        customerBookOrderItemRepository.saveAll(orderItems);

        CustomerBookOrder reloadedOrder = customerBookOrderRepository.findById(savedOrder.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", savedOrder.getId()));

        Set<CustomerBookOrderItem> savedOrderItems = Set.copyOf(customerBookOrderItemRepository.findByCustomerBookOrderId(savedOrder.getId()));
        reloadedOrder.setOrderItems(savedOrderItems);

        return mapToCustomerBookOrderDTO(reloadedOrder);
    }

    @Override
    @Transactional
    public CustomerBookOrderDTO generatePersonalizedOrder(CustomerBookOrderDTO orderDTO) {
        Long customerId = orderDTO.getCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (orderDTO.getOrderItems() == null || orderDTO.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one order item.");
        }

        for (CustomerBookOrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            Long schoolId = itemDTO.getSchoolId();
            Long classId = itemDTO.getClassId();
            Long listId = itemDTO.getOfficialListId();

            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

            ClassEntity classEntity = classEntityRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

            ListEntity officialList = listEntityRepository.findById(listId)
                    .orElseThrow(() -> new ResourceNotFoundException("Official List", "id", listId));

            if (!officialList.getClassEntity().getId().equals(classId)) {
                throw new IllegalArgumentException(
                        "Official list does not match the class ID provided for list ID: " + listId);
            }

            if (!officialList.getClassEntity().getSchool().getId().equals(schoolId)) {
                throw new IllegalArgumentException(
                        "Class's school does not match the school ID provided for list ID: " + listId);
            }

            boolean bookInList = listBookRepository.findByListEntityIdAndBookId(listId, itemDTO.getBookId())
                    .isPresent();
            if (!bookInList) {
                throw new ResourceNotFoundException("Book", "id in official list", itemDTO.getBookId());
            }
        }

        CustomerBookOrder newOrder = new CustomerBookOrder();
        newOrder.setCustomer(customer);
        newOrder.setOrderDate(LocalDateTime.now());

        // Set officialList, school, and classEntity from the first order item
        CustomerBookOrderItemDTO firstItem = orderDTO.getOrderItems().iterator().next();
        ListEntity officialList = listEntityRepository.findById(firstItem.getOfficialListId())
                .orElseThrow(() -> new ResourceNotFoundException("Official List", "id", firstItem.getOfficialListId()));
        School school = schoolRepository.findById(firstItem.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", firstItem.getSchoolId()));
        ClassEntity classEntity = classEntityRepository.findById(firstItem.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", firstItem.getClassId()));
        newOrder.setOfficialList(officialList);
        newOrder.setSchool(school);
        newOrder.setClassEntity(classEntity);

        // Create and link order items
        Set<CustomerBookOrderItem> orderItems = orderDTO.getOrderItems().stream().map(itemDTO -> {
            Book book = bookRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", itemDTO.getBookId()));
            CustomerBookOrderItem item = new CustomerBookOrderItem();
            item.setCustomerBookOrder(newOrder); // Link child to parent
            item.setBook(book);
            item.setQuantity(itemDTO.getQuantity());
            item.setConditionType(itemDTO.getConditionType() != null
                    ? CustomerBookOrderItem.BookCondition.valueOf(itemDTO.getConditionType().name())
                    : CustomerBookOrderItem.BookCondition.NEW);

            Long listId = itemDTO.getOfficialListId();
            Long schoolId = itemDTO.getSchoolId();
            Long classId = itemDTO.getClassId();
            if (listId != null) {
                ListEntity itemOfficialList = listEntityRepository.findById(listId)
                        .orElseThrow(() -> new ResourceNotFoundException("Official List", "id", listId));
                item.setOfficialList(itemOfficialList);
            }
            if (schoolId != null) {
                School itemSchool = schoolRepository.findById(schoolId)
                        .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));
                item.setSchool(itemSchool);
            }
            if (classId != null) {
                ClassEntity itemClassEntity = classEntityRepository.findById(classId)
                        .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));
                item.setClassEntity(itemClassEntity);
            }
            return item;
        }).collect(Collectors.toSet());

        newOrder.setOrderItems(orderItems); // Link parent to children
        CustomerBookOrder savedOrder = customerBookOrderRepository.save(newOrder); // Cascade saves items

        CustomerBookOrder reloadedOrder = customerBookOrderRepository.findById(savedOrder.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", savedOrder.getId()));

        return mapToCustomerBookOrderDTO(reloadedOrder);
    }

    @Override
    public CustomerBookOrderDTO getCustomerBookOrderById(Long id) {
        CustomerBookOrder order = customerBookOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", id));
        return mapToCustomerBookOrderDTO(order);
    }

    @Override
    public java.util.List<CustomerBookOrderDTO> getAllCustomerBookOrders() {
        java.util.List<CustomerBookOrder> orders = customerBookOrderRepository.findAll();
        return orders.stream().map(this::mapToCustomerBookOrderDTO).collect(Collectors.toList());
    }

    @Override
    public java.util.List<CustomerBookOrderDTO> getCustomerBookOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        java.util.List<CustomerBookOrder> orders = customerBookOrderRepository.findByCustomerId(customerId);
        return orders.stream().map(this::mapToCustomerBookOrderDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerBookOrderDTO updateCustomerBookOrder(Long id, CustomerBookOrderDTO orderDTO) {
        CustomerBookOrder existingOrder = customerBookOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", id));

        CustomerBookOrder updatedOrder = customerBookOrderRepository.save(existingOrder);
        return mapToCustomerBookOrderDTO(updatedOrder);
    }

    @Override
    public void deleteCustomerBookOrder(Long id) {
        CustomerBookOrder order = customerBookOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", id));
        customerBookOrderRepository.delete(order);
    }

    private CustomerBookOrderDTO mapToCustomerBookOrderDTO(CustomerBookOrder order) {
        CustomerBookOrderDTO dto = modelMapper.map(order, CustomerBookOrderDTO.class);
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerFirstName(order.getCustomer().getFirstName());
        dto.setCustomerLastName(order.getCustomer().getLastName());

        if (order.getOrderItems() != null) {
            Set<CustomerBookOrderItem> orderItems = order.getOrderItems();
            orderItems.size();
            Set<CustomerBookOrderItemDTO> itemDTOs = orderItems.stream().map(this::mapToCustomerBookOrderItemDTO)
                    .collect(Collectors.toSet());
            dto.setOrderItems(itemDTOs);
        }
        return dto;
    }

    private CustomerBookOrderItemDTO mapToCustomerBookOrderItemDTO(CustomerBookOrderItem item) {
        CustomerBookOrderItemDTO dto = modelMapper.map(item, CustomerBookOrderItemDTO.class);
        dto.setOrderId(item.getCustomerBookOrder().getId());
        dto.setBookId(item.getBook().getId());
        dto.setBookTitle(item.getBook().getTitle());
        dto.setBookAuthor(item.getBook().getAuthor());
        dto.setBookPrice(item.getBook().getPrice() * item.getQuantity());

        dto.setOfficialListId(item.getOfficialList() != null ? item.getOfficialList().getId() : null);
        dto.setSchoolId(item.getSchool() != null ? item.getSchool().getId() : null);
        dto.setClassId(item.getClassEntity() != null ? item.getClassEntity().getId() : null);
        return dto;
    }
}