package kg.benext.db.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kg.benext.db.entity.model.Product;

@Converter
public class ProductDataConverter implements AttributeConverter<Product, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Product product) {
        try {
            return objectMapper.writeValueAsString(product);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Product to JSON", e);
        }
    }

    @Override
    public Product convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, Product.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Product", e);
        }
    }
}