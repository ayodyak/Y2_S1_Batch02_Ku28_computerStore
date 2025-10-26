package org.computerspareparts.csms.global.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "part")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id", columnDefinition = "INT")
    private Integer partId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_level", nullable = false)
    private Integer stockLevel;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Constructors
    public Part() {}

    // Getters and setters
    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockLevel() { return stockLevel; }
    public void setStockLevel(Integer stockLevel) { this.stockLevel = stockLevel; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Derived/transient properties for view templates
    @Transient
    public String getStatus() {
        if (stockLevel == null || reorderLevel == null) return "Unknown";
        if (stockLevel == 0) return "Out of Stock";
        if (stockLevel > 0 && stockLevel <= reorderLevel) return "Low Stock";
        return "In Stock";
    }
}
