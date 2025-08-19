package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.TempOrderDTO;
import com.bookstore.booksmanagementsystem.dto.TempOrderItemDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.service.TempOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/temp-orders")
public class TempOrderController {

    private final TempOrderService tempOrderService;

    public TempOrderController(TempOrderService tempOrderService) {
        this.tempOrderService = tempOrderService;
    }

    // Customer endpoints
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TempOrderDTO> getMyDraft() {
        return ResponseEntity.ok(tempOrderService.getOrCreateMyDraft());
    }

    @GetMapping({"/me/list", "/me/list/", "/me/drafts", "/list/me"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TempOrderDTO>> listMyDrafts() {
        return ResponseEntity.ok(tempOrderService.listMyDrafts());
    }

    @GetMapping("/me/{tempOrderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TempOrderDTO> getMyDraftById(@PathVariable Long tempOrderId) {
        return ResponseEntity.ok(tempOrderService.getMyDraftById(tempOrderId));
    }

    

    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TempOrderDTO> addItems(@RequestBody List<TempOrderItemDTO> items) {
        return ResponseEntity.ok(tempOrderService.addItemsToMyDraft(items));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TempOrderDTO> updateItem(@PathVariable Long itemId, @RequestBody TempOrderItemDTO item) {
        return ResponseEntity.ok(tempOrderService.updateItemInMyDraft(itemId, item));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        tempOrderService.removeItemFromMyDraft(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tempOrderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancel(@PathVariable Long tempOrderId) {
        tempOrderService.cancelMyDraft(tempOrderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tempOrderId}/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TempOrderDTO> submit(@PathVariable Long tempOrderId) {
        return ResponseEntity.ok(tempOrderService.submitMyDraft(tempOrderId));
    }

    @PostMapping("/{tempOrderId}/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TempOrderDTO> addItemsToSpecificDraft(@PathVariable Long tempOrderId, @RequestBody List<TempOrderItemDTO> items) {
        return ResponseEntity.ok(tempOrderService.addItemsToMyDraft(tempOrderId, items));
    }

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<List<TempOrderDTO>> listSubmitted() {
        return ResponseEntity.ok(tempOrderService.listSubmittedDrafts());
    }

    @GetMapping("/{tempOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<TempOrderDTO> getById(@PathVariable Long tempOrderId) {
        return ResponseEntity.ok(tempOrderService.getDraftById(tempOrderId));
    }

    @PostMapping("/{tempOrderId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<CustomerBookOrderDTO> approve(@PathVariable Long tempOrderId) {
        return ResponseEntity.ok(tempOrderService.approveDraft(tempOrderId));
    }
}
