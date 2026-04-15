package kg.benext.db.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public class Product {

    @JsonProperty("Id")
    private UUID id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Price")
    private Double price;

    @JsonProperty("BrandId")
    private UUID brandId;

    @JsonProperty("BrandName")
    private String brandName;

    @JsonProperty("ImageFile")
    private String imageFile;

    @JsonProperty("CategoryIds")
    private List<UUID> categoryIds;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("ReviewCount")
    private Integer reviewCount;

    @JsonProperty("Translations")
    private List<Object> translations;

    @JsonProperty("AverageRating")
    private Double averageRating;

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public UUID getBrandId() { return brandId; }
    public void setBrandId(UUID brandId) { this.brandId = brandId; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getImageFile() { return imageFile; }
    public void setImageFile(String imageFile) { this.imageFile = imageFile; }

    public List<UUID> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<UUID> categoryIds) { this.categoryIds = categoryIds; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public List<Object> getTranslations() { return translations; }
    public void setTranslations(List<Object> translations) { this.translations = translations; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    @Override
    public String toString() {
        return "Product{name='" + name + "', price=" + price + ", brandName='" + brandName + "'}";
    }
}