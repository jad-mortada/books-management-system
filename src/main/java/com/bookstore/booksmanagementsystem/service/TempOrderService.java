package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.TempOrderDTO;
import com.bookstore.booksmanagementsystem.dto.TempOrderItemDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;

import java.util.List;

public interface TempOrderService {
    TempOrderDTO getOrCreateMyDraft();
    TempOrderDTO getMyDraft();
    TempOrderDTO addItemsToMyDraft(List<TempOrderItemDTO> items);
    TempOrderDTO addItemsToMyDraft(Long tempOrderId, List<TempOrderItemDTO> items);
    TempOrderDTO updateItemInMyDraft(Long itemId, TempOrderItemDTO item);
    void removeItemFromMyDraft(Long itemId);
    void cancelMyDraft(Long tempOrderId);
    TempOrderDTO submitMyDraft(Long tempOrderId);

    // Customer view
    java.util.List<TempOrderDTO> listMyDrafts();
    TempOrderDTO getMyDraftById(Long tempOrderId);

    // Admin
    List<TempOrderDTO> listSubmittedDrafts();
    CustomerBookOrderDTO approveDraft(Long tempOrderId);
    TempOrderDTO getDraftById(Long tempOrderId);
}
