package com.example.backend.repository;

import com.example.backend.model.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {
    // Custom query to find all items with a specific status
    List<FoundItem> findByStatus(String status);
}
