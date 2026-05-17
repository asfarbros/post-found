package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "found_items") // This will be the table name in your database
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName; // e.g., "Red Water Bottle"

    @Column(nullable = false)
    private String description; // e.g., "Found near the library"

    @Column(nullable = false)
    private LocalDate dateFound; // The date the item was found

    @Column
    private String imageUrl; // A URL to a picture of the item (can be null)

    @Column(nullable = false)
    private String status; // e.g., "FOUND", "CLAIMED"

    @Column
    private String claimedBy; // Name or email of the person who claimed the item

    @Column
    private String postedBy; // The user who created (posted) this found item

    @Column
    private String locationFound; // e.g., "Library", "Canteen"
}