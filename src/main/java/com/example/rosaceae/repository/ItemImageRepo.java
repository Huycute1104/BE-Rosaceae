package com.example.rosaceae.repository;

import com.example.rosaceae.model.Item;
import com.example.rosaceae.model.ItemImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemImageRepo extends JpaRepository<ItemImages,Integer> {
    Optional<ItemImages> findByItemImageId(int id);
    List<ItemImages> findByItem(Item item);

}
