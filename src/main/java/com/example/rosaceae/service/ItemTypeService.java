package com.example.rosaceae.service;

import com.example.rosaceae.dto.Request.CategoryRequest.CreateCategoryRequest;
import com.example.rosaceae.dto.Request.ItemTypeRequest.ItemTypeResponse;
import com.example.rosaceae.dto.Response.CategoryResponse.CategoryResponse;
import com.example.rosaceae.dto.Response.ItemTypeResponse.ItemTypeRequest;
import com.example.rosaceae.model.Category;
import com.example.rosaceae.model.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemTypeService {
    public List<ItemType> findAll();
    Page<ItemType> findAll(Pageable pageable);
    public ItemTypeResponse createItemType(ItemTypeRequest request);
    public ItemTypeResponse updateItemType(ItemTypeRequest request , int ID);
    public Optional<ItemType> getItemTypeByID(int id);
    public ItemTypeResponse deleteItemType(int id);
}
