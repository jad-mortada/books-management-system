package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderItemDTO;
import com.bookstore.booksmanagementsystem.entity.*;
import com.bookstore.booksmanagementsystem.entity.CustomerBookOrder.OrderStatus;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.*;
import com.bookstore.booksmanagementsystem.service.CustomerBookOrderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
                                        CustomerBookOrderItemRepository customerBookOrderItemRepository,
                                        CustomerRepository customerRepository, ListEntityRepository listEntityRepository,
                                        SchoolRepository schoolRepository, ClassEntityRepository classEntityRepository,
                                        ListBookRepository listBookRepository, BookRepository bookRepository, ModelMapper modelMapper) {
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
        ListEntity officialList = listEntityRepository.findById(orderDTO.getOfficialListId())
                .orElseThrow(() -> new ResourceNotFoundException("Official List", "id", orderDTO.getOfficialListId()));
        School school = schoolRepository.findById(orderDTO.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", orderDTO.getSchoolId()));
        ClassEntity classEntity = classEntityRepository.findById(orderDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", orderDTO.getClassId()));

        customerBookOrderRepository.findByCustomerIdAndOfficialListIdAndSchoolIdAndClassEntityId(
                orderDTO.getCustomerId(), officialList.getId(), school.getId(), classEntity.getId())
                .ifPresent(o -> {
                    throw new DuplicateResourceException("Customer Book Order", "combination of customer, official list, school, class",
                            "CustomerId: " + orderDTO.getCustomerId() + ", ListId: " + officialList.getId() +
                            ", SchoolId: " + school.getId() + ", ClassId: " + classEntity.getId());
                });

        CustomerBookOrder order = modelMapper.map(orderDTO, CustomerBookOrder.class);
        order.setCustomer(customer);
        order.setOfficialList(officialList);
        order.setSchool(school);
        order.setClassEntity(classEntity);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        CustomerBookOrder savedOrder = customerBookOrderRepository.save(order);

        Set<CustomerBookOrderItem> orderItems = orderDTO.getOrderItems().stream()
                .map(itemDTO -> {
                    Book book = bookRepository.findById(itemDTO.getBookId())
                            .orElseThrow(() -> new ResourceNotFoundException("Book", "id", itemDTO.getBookId()));
                    CustomerBookOrderItem item = modelMapper.map(itemDTO, CustomerBookOrderItem.class);
                    item.setCustomerBookOrder(savedOrder);
                    item.setBook(book);
                    if (item.getIsPrepared() == null) item.setIsPrepared(false);
                    if (item.getConditionType() == null) item.setConditionType(CustomerBookOrderItem.BookCondition.NEW);
                    return item;
                })
                .collect(Collectors.toSet());
        customerBookOrderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        return mapToCustomerBookOrderDTO(savedOrder);
    }

    @Override
    public CustomerBookOrderDTO getCustomerBookOrderById(Long id) {
        CustomerBookOrder order = customerBookOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", id));
        return mapToCustomerBookOrderDTO(order);
    }

    @Override
    public List<CustomerBookOrderDTO> getAllCustomerBookOrders() {
        List<CustomerBookOrder> orders = customerBookOrderRepository.findAll();
        return orders.stream()
                .map(this::mapToCustomerBookOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerBookOrderDTO> getCustomerBookOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        List<CustomerBookOrder> orders = customerBookOrderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::mapToCustomerBookOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerBookOrderDTO updateCustomerBookOrder(Long id, CustomerBookOrderDTO orderDTO) {
        CustomerBookOrder existingOrder = customerBookOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", id));

        if (orderDTO.getStatus() != null) {
            existingOrder.setStatus(orderDTO.getStatus());
        }

        CustomerBookOrder updatedOrder = customerBookOrderRepository.save(existingOrder);
        return mapToCustomerBookOrderDTO(updatedOrder);
    }

    @Override
    public void deleteCustomerBookOrder(Long id) {
        CustomerBookOrder order = customerBookOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", id));
        customerBookOrderRepository.delete(order);
    }

    @Override
    @Transactional
    public CustomerBookOrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        CustomerBookOrder order = customerBookOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order", "id", orderId));
        order.setStatus(newStatus);
        CustomerBookOrder updatedOrder = customerBookOrderRepository.save(order);
        return mapToCustomerBookOrderDTO(updatedOrder);
    }

    @Override
    @Transactional
    public CustomerBookOrderItemDTO updateOrderItemStatus(Long orderItemId, Boolean isPrepared, CustomerBookOrderItemDTO.BookCondition condition) {
        CustomerBookOrderItem item = customerBookOrderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Book Order Item", "id", orderItemId));

        if (isPrepared != null) {
            item.setIsPrepared(isPrepared);
        }
        if (condition != null) {
            item.setConditionType(modelMapper.map(condition, CustomerBookOrderItem.BookCondition.class));
        }

        CustomerBookOrderItem updatedItem = customerBookOrderItemRepository.save(item);
        return mapToCustomerBookOrderItemDTO(updatedItem);
    }

    @Override
    @Transactional
    public CustomerBookOrderDTO generatePersonalizedOrder(Long customerId, Long schoolId, Long classId, Integer year) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        ListEntity officialList = listEntityRepository.findByClassEntityIdAndYear(classId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Official List", "classId and year", classId + " and " + year));

        customerBookOrderRepository.findByCustomerIdAndOfficialListIdAndSchoolIdAndClassEntityId(
                customerId, officialList.getId(), school.getId(), classEntity.getId())
                .ifPresent(existingOrder -> {
                    throw new DuplicateResourceException("Personalized Order", "for this customer, school, class, and year",
                            "Customer ID: " + customerId + ", School: " + school.getName() + ", Class: " + classEntity.getName() + ", Year: " + year);
                });

        CustomerBookOrder newOrder = new CustomerBookOrder();
        newOrder.setCustomer(customer);
        newOrder.setOfficialList(officialList);
        newOrder.setSchool(school);
        newOrder.setClassEntity(classEntity);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(OrderStatus.PENDING);
        CustomerBookOrder savedOrder = customerBookOrderRepository.save(newOrder);

        List<ListBook> officialListBooks = listBookRepository.findByListEntityId(officialList.getId());
        Set<CustomerBookOrderItem> orderItems = officialListBooks.stream()
                .map(listBook -> {
                    CustomerBookOrderItem item = new CustomerBookOrderItem();
                    item.setCustomerBookOrder(savedOrder);
                    item.setBook(listBook.getBook());
                    item.setQuantity(listBook.getQuantity());
                    item.setIsPrepared(false);
                    item.setConditionType(CustomerBookOrderItem.BookCondition.NEW);
                    return item;
                })
                .collect(Collectors.toSet());

        customerBookOrderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        return mapToCustomerBookOrderDTO(savedOrder);
    }

    private CustomerBookOrderDTO mapToCustomerBookOrderDTO(CustomerBookOrder order) {
        CustomerBookOrderDTO dto = modelMapper.map(order, CustomerBookOrderDTO.class);
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerFirstName(order.getCustomer().getFirstName());
        dto.setCustomerLastName(order.getCustomer().getLastName());
        dto.setOfficialListId(order.getOfficialList().getId());
        dto.setOfficialListYear(String.valueOf(order.getOfficialList().getYear()));
        dto.setOfficialListClassName(order.getOfficialList().getClassEntity().getName());
        dto.setOfficialListSchoolName(order.getOfficialList().getClassEntity().getSchool().getName());
        dto.setSchoolId(order.getSchool().getId());
        dto.setClassId(order.getClassEntity().getId());

        if (order.getOrderItems() != null) {
            Set<CustomerBookOrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .map(this::mapToCustomerBookOrderItemDTO)
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
        dto.setBookPrice(item.getBook().getPrice());
        return dto;
    }
}
