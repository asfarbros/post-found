package com.example.backend.controller;

import com.example.backend.model.FoundItem;
import com.example.backend.repository.FoundItemRepository;
import com.example.backend.service.CloudinaryService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class FoundItemController {

    @Autowired
    private FoundItemRepository foundItemRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // --- CREATE ITEM (with postedBy support) ---
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createItem(
            @RequestParam("itemName") String itemName,
            @RequestParam("description") String description,
            @RequestParam("locationFound") String locationFound,
            @RequestParam("postedBy") String postedBy, // ✅ new field
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = cloudinaryService.uploadFile(imageFile);
            }

            FoundItem newItem = new FoundItem();//Entity/Model class ku autowired use panna koodathu 
            newItem.setItemName(itemName);
            newItem.setDescription(description);
            newItem.setLocationFound(locationFound);
            newItem.setDateFound(LocalDate.now());
            newItem.setStatus("FOUND");
            newItem.setImageUrl(imageUrl);
            newItem.setPostedBy(postedBy); // ✅ store who posted the item

            FoundItem savedItem = foundItemRepository.save(newItem);
            return ResponseEntity.ok(savedItem);

        } catch (Exception e) {
            System.err.println("Error creating item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Could not create item: " + e.getMessage());
        }
    }

    // --- READ ALL ITEMS ---
    @GetMapping
    public ResponseEntity<List<FoundItem>> getAllItems() {
        List<FoundItem> items = foundItemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    // --- READ ITEM BY ID ---
    @GetMapping("/{id}")
    public ResponseEntity<FoundItem> getItemById(@PathVariable Long id) {
        Optional<FoundItem> item = foundItemRepository.findById(id);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- UPDATE ITEM STATUS ---
    @PutMapping("/{id}")
    public ResponseEntity<FoundItem> updateItemStatus(@PathVariable Long id, @RequestBody FoundItem updatedItemInfo) {
        return foundItemRepository.findById(id)
                .map(item -> {
                    item.setStatus(updatedItemInfo.getStatus());
                    FoundItem savedItem = foundItemRepository.save(item);
                    return ResponseEntity.ok(savedItem);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- DELETE ITEM ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Optional<FoundItem> itemOptional = foundItemRepository.findById(id);
        if (itemOptional.isPresent()) {
            FoundItem item = itemOptional.get();
            if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) {
                boolean deleted = cloudinaryService.deleteFile(item.getImageUrl());
                if (!deleted) {
                    System.err.println("Failed to delete Cloudinary image for item ID: " + id + " URL: " + item.getImageUrl());
                }
            }
            foundItemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

        // --- CLAIM ITEM (Auto-link with logged-in user) ---
    @PutMapping("/{id}/claim")
    public ResponseEntity<?> claimItem(@PathVariable Long id, @RequestParam(required = false) String claimedBy) {
    Optional<FoundItem> itemOptional = foundItemRepository.findById(id);

    if (itemOptional.isPresent()) {
        FoundItem item = itemOptional.get();

        if ("CLAIMED".equalsIgnoreCase(item.getStatus())) {
            return ResponseEntity.badRequest().body("Item already claimed.");
        }

        // If claimedBy not provided in request, reject
        if (claimedBy == null || claimedBy.isBlank()) {
            return ResponseEntity.badRequest().body("claimedBy is required.");
        }

        item.setStatus("CLAIMED");
        item.setClaimedBy(claimedBy);

        foundItemRepository.save(item);
        return ResponseEntity.ok(item);
    } else {
        return ResponseEntity.notFound().build();
    }
}

    // --- FETCH CLAIMED ITEMS ---
    @GetMapping("/claimed")
    public ResponseEntity<List<FoundItem>> getClaimedItems() {
        List<FoundItem> claimedItems = foundItemRepository.findByStatus("CLAIMED");
        return ResponseEntity.ok(claimedItems);
    }
}
