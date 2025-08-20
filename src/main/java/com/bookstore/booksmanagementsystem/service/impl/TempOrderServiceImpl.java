package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.TempOrderDTO;
import com.bookstore.booksmanagementsystem.dto.TempOrderItemDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderItemDTO;
import com.bookstore.booksmanagementsystem.entity.*;
import com.bookstore.booksmanagementsystem.repository.*;
import com.bookstore.booksmanagementsystem.service.TempOrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TempOrderServiceImpl implements TempOrderService {

    private final TempCustomerOrderRepository tempOrderRepo;
    private final TempCustomerOrderItemRepository tempItemRepo;
    private final CustomerRepository customerRepo;
    private final BookRepository bookRepo;
    private final ListEntityRepository listRepo;
    private final SchoolRepository schoolRepo;
    private final ClassEntityRepository classRepo;
    private final CustomerBookOrderRepository orderRepo;
    private final CustomerBookOrderItemRepository orderItemRepo;

    public TempOrderServiceImpl(TempCustomerOrderRepository tempOrderRepo,
                                TempCustomerOrderItemRepository tempItemRepo,
                                CustomerRepository customerRepo,
                                BookRepository bookRepo,
                                ListEntityRepository listRepo,
                                SchoolRepository schoolRepo,
                                ClassEntityRepository classRepo,
                                CustomerBookOrderRepository orderRepo,
                                CustomerBookOrderItemRepository orderItemRepo) {
        this.tempOrderRepo = tempOrderRepo;
        this.tempItemRepo = tempItemRepo;
        this.customerRepo = customerRepo;
        this.bookRepo = bookRepo;
        this.listRepo = listRepo;
        this.schoolRepo = schoolRepo;
        this.classRepo = classRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            return ud.getUsername();
        }
        if (principal instanceof String s) {
            // In many JWT setups, principal is the username/email as String
            return s;
        }
        // Fallback to Authentication#getName(), which typically returns the username
        return auth.getName();
    }

    private Customer currentCustomer() {
        String email = currentEmail();
        if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        return customerRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found for " + email));
    }

    private double computeUnitPrice(Book book, String conditionType) {
        double base = Optional.ofNullable(book.getPrice()).orElse(0.0);
        if ("USED".equalsIgnoreCase(conditionType)) return base * 0.5;
        return base;
    }

    private TempOrderDTO toDTO(TempCustomerOrder order) {
        TempOrderDTO dto = new TempOrderDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        if (order.getCustomer() != null) {
            dto.setCustomerFirstName(order.getCustomer().getFirstName());
            dto.setCustomerLastName(order.getCustomer().getLastName());
        }
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        List<TempOrderItemDTO> items = order.getItems().stream().map(this::toDTO).collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }

    private TempOrderItemDTO toDTO(TempCustomerOrderItem item) {
        TempOrderItemDTO dto = new TempOrderItemDTO();
        dto.setId(item.getId());
        dto.setBookId(item.getBook() != null ? item.getBook().getId() : null);
        if (item.getBook() != null) {
            dto.setBookTitle(item.getBook().getTitle());
            dto.setBookAuthor(item.getBook().getAuthor());
            dto.setImageUrl(item.getBook().getImageUrl());
        }
        dto.setOfficialListId(item.getOfficialList() != null ? item.getOfficialList().getId() : null);
        dto.setSchoolId(item.getSchool() != null ? item.getSchool().getId() : null);
        dto.setClassId(item.getClassEntity() != null ? item.getClassEntity().getId() : null);
        dto.setYear(item.getYear());
        dto.setQuantity(item.getQuantity());
        dto.setConditionType(item.getConditionType() != null ? item.getConditionType().name() : null);
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    @Override
    public TempOrderDTO getOrCreateMyDraft() {
        Customer me = currentCustomer();
        TempCustomerOrder draft = tempOrderRepo.findFirstByCustomerAndStatus(me, TempCustomerOrder.Status.DRAFT)
                .orElseGet(() -> {
                    TempCustomerOrder o = new TempCustomerOrder();
                    o.setCustomer(me);
                    o.setStatus(TempCustomerOrder.Status.DRAFT);
                    return tempOrderRepo.save(o);
                });
        return toDTO(draft);
    }

    @Override
    public TempOrderDTO addItemsToMyDraft(Long tempOrderId, List<TempOrderItemDTO> items) {
        if (items == null) items = new ArrayList<>();
        Customer me = currentCustomer();
        TempCustomerOrder draft = tempOrderRepo.findById(tempOrderId)
                .orElseThrow(() -> new RuntimeException("Draft not found"));
        if (!draft.getCustomer().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        // Allow adding while DRAFT or SUBMITTED; block after approval
        if (draft.getStatus() == TempCustomerOrder.Status.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approved orders cannot be modified");
        }
        for (TempOrderItemDTO it : items) {
            Book book = bookRepo.findById(it.getBookId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
            TempCustomerOrderItem.ConditionType cond = "USED".equalsIgnoreCase(it.getConditionType())
                    ? TempCustomerOrderItem.ConditionType.USED : TempCustomerOrderItem.ConditionType.NEW;
            TempCustomerOrderItem entity = new TempCustomerOrderItem();
            entity.setTempOrder(draft);
            entity.setBook(book);
            if (it.getOfficialListId() != null) {
                listRepo.findById(it.getOfficialListId()).ifPresent(entity::setOfficialList);
            }
            if (it.getSchoolId() != null) schoolRepo.findById(it.getSchoolId()).ifPresent(entity::setSchool);
            if (it.getClassId() != null) classRepo.findById(it.getClassId()).ifPresent(entity::setClassEntity);
            entity.setYear(it.getYear());
            entity.setQuantity(it.getQuantity() != null && it.getQuantity() > 0 ? it.getQuantity() : 1);
            entity.setConditionType(cond);
            double unit = computeUnitPrice(book, cond.name());
            entity.setUnitPrice(unit);
            entity.setSubtotal(unit * entity.getQuantity());
            tempItemRepo.save(entity);
            draft.getItems().add(entity);
        }
        return toDTO(draft);
    }

    @Override
    public TempOrderDTO getMyDraft() {
        Customer me = currentCustomer();
        TempCustomerOrder draft = tempOrderRepo.findFirstByCustomerAndStatus(me, TempCustomerOrder.Status.DRAFT)
                .orElseThrow(() -> new RuntimeException("No draft available"));
        return toDTO(draft);
    }

    @Override
    public TempOrderDTO addItemsToMyDraft(List<TempOrderItemDTO> items) {
        if (items == null) items = new ArrayList<>();
        Customer me = currentCustomer();
        // Prefer existing DRAFT; if absent, use existing SUBMITTED; otherwise create new DRAFT
        TempCustomerOrder draft = tempOrderRepo.findFirstByCustomerAndStatus(me, TempCustomerOrder.Status.DRAFT)
                .orElseGet(() -> tempOrderRepo.findFirstByCustomerAndStatus(me, TempCustomerOrder.Status.SUBMITTED)
                        .orElseGet(() -> {
                            TempCustomerOrder o = new TempCustomerOrder();
                            o.setCustomer(me);
                            o.setStatus(TempCustomerOrder.Status.DRAFT);
                            return tempOrderRepo.save(o);
                        }));
        for (TempOrderItemDTO it : items) {
            Book book = bookRepo.findById(it.getBookId()).orElseThrow(() -> new RuntimeException("Book not found"));
            TempCustomerOrderItem entity = new TempCustomerOrderItem();
            entity.setTempOrder(draft);
            entity.setBook(book);
            if (it.getOfficialListId() != null) {
                listRepo.findById(it.getOfficialListId()).ifPresent(entity::setOfficialList);
            }
            if (it.getSchoolId() != null) schoolRepo.findById(it.getSchoolId()).ifPresent(entity::setSchool);
            if (it.getClassId() != null) classRepo.findById(it.getClassId()).ifPresent(entity::setClassEntity);
            entity.setYear(it.getYear());
            entity.setQuantity(it.getQuantity() != null && it.getQuantity() > 0 ? it.getQuantity() : 1);
            TempCustomerOrderItem.ConditionType cond = "USED".equalsIgnoreCase(it.getConditionType())
                    ? TempCustomerOrderItem.ConditionType.USED : TempCustomerOrderItem.ConditionType.NEW;
            entity.setConditionType(cond);
            double unit = computeUnitPrice(book, cond.name());
            entity.setUnitPrice(unit);
            entity.setSubtotal(unit * entity.getQuantity());
            tempItemRepo.save(entity);
            draft.getItems().add(entity);
        }
        return toDTO(draft);
    }

    @Override
    public TempOrderDTO updateItemInMyDraft(Long itemId, TempOrderItemDTO item) {
        Customer me = currentCustomer();
        TempCustomerOrderItem entity = tempItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        TempCustomerOrder draft = entity.getTempOrder();
        if (draft == null) throw new RuntimeException("No draft available");
        if (!draft.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        // Allow edits while DRAFT or SUBMITTED; disallow otherwise (e.g., APPROVED)ء
        if (draft.getStatus() != TempCustomerOrder.Status.DRAFT && draft.getStatus() != TempCustomerOrder.Status.SUBMITTED) {
            throw new RuntimeException("Cannot modify items for this order status");
        }
        if (item.getQuantity() != null && item.getQuantity() > 0) entity.setQuantity(item.getQuantity());
        if (item.getConditionType() != null) {
            TempCustomerOrderItem.ConditionType cond = "USED".equalsIgnoreCase(item.getConditionType())
                    ? TempCustomerOrderItem.ConditionType.USED : TempCustomerOrderItem.ConditionType.NEW;
            entity.setConditionType(cond);
        }
        double unit = computeUnitPrice(entity.getBook(), entity.getConditionType().name());
        entity.setUnitPrice(unit);
        entity.setSubtotal(unit * entity.getQuantity());
        tempItemRepo.save(entity);
        return toDTO(draft);
    }

    @Override
    public void removeItemFromMyDraft(Long itemId) {
        Customer me = currentCustomer();
        TempCustomerOrderItem entity = tempItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        TempCustomerOrder draft = entity.getTempOrder();
        if (draft == null) throw new RuntimeException("No draft available");
        if (!draft.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        // Allow removals while DRAFT or SUBMITTED; disallow otherwise
        if (draft.getStatus() != TempCustomerOrder.Status.DRAFT && draft.getStatus() != TempCustomerOrder.Status.SUBMITTED) {
            throw new RuntimeException("Cannot modify items for this order status");
        }
        draft.getItems().remove(entity);
        tempItemRepo.delete(entity);
    }

    @Override
    public void cancelMyDraft(Long tempOrderId) {
        Customer me = currentCustomer();
        TempCustomerOrder draft = tempOrderRepo.findById(tempOrderId).orElseThrow(() -> new RuntimeException("Draft not found"));
        if (!draft.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        // Allow cancelling while DRAFT or SUBMITTED; block only after approval
        if (draft.getStatus() == TempCustomerOrder.Status.APPROVED) {
            throw new RuntimeException("Approved orders cannot be cancelled by customer");
        }
        tempOrderRepo.delete(draft);
    }

    @Override
    public TempOrderDTO submitMyDraft(Long tempOrderId) {
        Customer me = currentCustomer();
        TempCustomerOrder draft = tempOrderRepo.findById(tempOrderId).orElseThrow(() -> new RuntimeException("Draft not found"));
        if (!draft.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        if (draft.getStatus() != TempCustomerOrder.Status.DRAFT) throw new RuntimeException("Only DRAFT can be submitted");
        draft.setStatus(TempCustomerOrder.Status.SUBMITTED);
        return toDTO(draft);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TempOrderDTO> listSubmittedDrafts() {
        return tempOrderRepo.findAllByStatus(TempCustomerOrder.Status.SUBMITTED).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TempOrderDTO> listMyDrafts() {
        Customer me = currentCustomer();
        List<TempCustomerOrder.Status> statuses = java.util.Arrays.asList(
                TempCustomerOrder.Status.DRAFT,
                TempCustomerOrder.Status.SUBMITTED
        );
        return tempOrderRepo.findAllByCustomerAndStatusIn(me, statuses).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CustomerBookOrderDTO approveDraft(Long tempOrderId) {
        TempCustomerOrder draft = tempOrderRepo.findById(tempOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Draft not found"));
        if (draft.getStatus() != TempCustomerOrder.Status.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SUBMITTED drafts can be approved");
        }

        // Move to permanent tables
        // Always create a NEW order per approval; never modify an existing approved order
        CustomerBookOrder order;
        ListEntity firstList = null;
        School firstSchool = null;
        ClassEntity firstClass = null;
        if (draft.getItems() != null && !draft.getItems().isEmpty()) {
            TempCustomerOrderItem first = draft.getItems().iterator().next();
            firstList = first.getOfficialList();
            firstSchool = first.getSchool();
            firstClass = first.getClassEntity();
        }

        // Create and persist the new order now
        order = new CustomerBookOrder();
        order.setCustomer(draft.getCustomer());
        if (firstList != null) order.setOfficialList(firstList);
        if (firstSchool != null) order.setSchool(firstSchool);
        if (firstClass != null) order.setClassEntity(firstClass);
        order.setOrderDate(java.time.LocalDateTime.now());
        orderRepo.save(order);

        // Prepare DTO container and populate while saving items to ensure reliability
        Set<CustomerBookOrderItemDTO> itemDTOs = new HashSet<>();
        for (TempCustomerOrderItem ti : draft.getItems()) {
            CustomerBookOrderItem.BookCondition cond =
                    (ti.getConditionType() == TempCustomerOrderItem.ConditionType.USED)
                            ? CustomerBookOrderItem.BookCondition.USED
                            : CustomerBookOrderItem.BookCondition.NEW;

            Long orderId = order.getId();
            Long bookId = ti.getBook() != null ? ti.getBook().getId() : null;
            Long officialListId2 = ti.getOfficialList() != null ? ti.getOfficialList().getId() : null;
            Long schoolId2 = ti.getSchool() != null ? ti.getSchool().getId() : null;
            Long classId2 = ti.getClassEntity() != null ? ti.getClassEntity().getId() : null;

            CustomerBookOrderItem oi = null;
            if (orderId != null && bookId != null) {
                // Try full-key match first (order, book, condition, list, school, class)
                oi = orderItemRepo
                        .findByCustomerBookOrderIdAndBookIdAndConditionTypeAndOfficialListIdAndSchoolIdAndClassEntityId(
                                orderId, bookId, cond, officialListId2, schoolId2, classId2)
                        .orElse(null);
                // Then try 5-column key (order, book, condition, list, school)
                if (oi == null) {
                    oi = orderItemRepo
                            .findByCustomerBookOrderIdAndBookIdAndConditionTypeAndOfficialListIdAndSchoolId(
                                    orderId, bookId, cond, officialListId2, schoolId2)
                            .orElse(null);
                }
                // Finally fallback to 3-column key (order, book, condition)
                if (oi == null) {
                    oi = orderItemRepo
                            .findByCustomerBookOrderIdAndBookIdAndConditionType(orderId, bookId, cond)
                            .orElse(null);
                }
            }

            if (oi == null) {
                // create a new row
                oi = new CustomerBookOrderItem();
                oi.setCustomerBookOrder(order);
                oi.setBook(ti.getBook());
                oi.setOfficialList(ti.getOfficialList());
                oi.setSchool(ti.getSchool());
                oi.setClassEntity(ti.getClassEntity());
                oi.setQuantity(ti.getQuantity());
                oi.setConditionType(cond);
                // copy committed pricing
                oi.setUnitPrice(ti.getUnitPrice());
                oi.setSubtotal(ti.getSubtotal());
                try {
                    orderItemRepo.saveAndFlush(oi);
                } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                    // Unique constraint hit – merge into existing row by fallback key and retry
                    CustomerBookOrderItem existing = orderItemRepo
                            .findByCustomerBookOrderIdAndBookIdAndConditionTypeAndOfficialListIdAndSchoolId(
                                    orderId, bookId, cond, officialListId2, schoolId2)
                            .orElse(null);
                    if (existing == null) {
                        existing = orderItemRepo
                                .findByCustomerBookOrderIdAndBookIdAndConditionType(orderId, bookId, cond)
                                .orElse(null);
                    }
                    if (existing == null) throw ex;
                    int mergedQty = (existing.getQuantity() == null ? 0 : existing.getQuantity()) +
                            (ti.getQuantity() == null ? 0 : ti.getQuantity());
                    existing.setQuantity(mergedQty);
                    Double unit2 = existing.getUnitPrice() != null ? existing.getUnitPrice() : ti.getUnitPrice();
                    existing.setUnitPrice(unit2);
                    existing.setSubtotal((unit2 != null ? unit2 : 0.0) * mergedQty);
                    oi = orderItemRepo.saveAndFlush(existing);
                }
            } else {
                // merge quantities and recompute subtotal
                int newQty = (oi.getQuantity() == null ? 0 : oi.getQuantity()) +
                        (ti.getQuantity() == null ? 0 : ti.getQuantity());
                oi.setQuantity(newQty);
                // prefer existing unitPrice if present, else take from temp item
                Double unit = oi.getUnitPrice() != null ? oi.getUnitPrice() : ti.getUnitPrice();
                oi.setUnitPrice(unit);
                oi.setSubtotal((unit != null ? unit : 0.0) * newQty);
                orderItemRepo.saveAndFlush(oi);
            }

            // Build item DTO directly from persisted item and source data
            CustomerBookOrderItemDTO dto = new CustomerBookOrderItemDTO();
            dto.setId(oi.getId());
            dto.setOrderId(order.getId());
            if (oi.getBook() != null) {
                dto.setBookId(oi.getBook().getId());
                dto.setBookTitle(oi.getBook().getTitle());
                dto.setBookAuthor(oi.getBook().getAuthor());
                // bookPrice should represent the base per-unit price (undiscounted)
                if (oi.getBook().getPrice() != null) {
                    dto.setBookPrice(oi.getBook().getPrice());
                }
                dto.setImageUrl(oi.getBook().getImageUrl());
            }
            dto.setQuantity(oi.getQuantity());
            dto.setUnitPrice(oi.getUnitPrice());
            dto.setSubtotal(oi.getSubtotal());
            dto.setConditionType(oi.getConditionType() == CustomerBookOrderItem.BookCondition.USED
                    ? CustomerBookOrderItemDTO.BookCondition.USED : CustomerBookOrderItemDTO.BookCondition.NEW);
            dto.setOfficialListId(oi.getOfficialList() != null ? oi.getOfficialList().getId() : null);
            dto.setSchoolId(oi.getSchool() != null ? oi.getSchool().getId() : null);
            dto.setSchoolName(oi.getSchool() != null ? oi.getSchool().getName() : null);
            dto.setClassId(oi.getClassEntity() != null ? oi.getClassEntity().getId() : null);
            dto.setClassName(oi.getClassEntity() != null ? oi.getClassEntity().getName() : null);
            dto.setYear(oi.getClassEntity() != null ? oi.getClassEntity().getYear() : null);
            itemDTOs.add(dto);
        }

        // Build return DTO from created permanent order
        CustomerBookOrderDTO result = new CustomerBookOrderDTO();
        result.setId(order.getId());
        if (order.getCustomer() != null) {
            result.setCustomerId(order.getCustomer().getId());
            result.setCustomerFirstName(order.getCustomer().getFirstName());
            result.setCustomerLastName(order.getCustomer().getLastName());
        }
        result.setOrderItems(itemDTOs);

        // Delete temp draft items explicitly (safety) then parent draft
        tempItemRepo.deleteAllByTempOrder(draft);
        tempOrderRepo.delete(draft);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public TempOrderDTO getDraftById(Long tempOrderId) {
        TempCustomerOrder draft = tempOrderRepo.findById(tempOrderId)
                .orElseThrow(() -> new RuntimeException("Draft not found"));
        return toDTO(draft);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TempOrderDTO getMyDraftById(Long tempOrderId) {
        Customer me = currentCustomer();
        TempCustomerOrder draft = tempOrderRepo.findById(tempOrderId)
                .orElseThrow(() -> new RuntimeException("Draft not found"));
        if (!draft.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        return toDTO(draft);
    }
}
