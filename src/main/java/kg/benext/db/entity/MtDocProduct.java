package kg.benext.db.entity;

import jakarta.persistence.*;
import kg.benext.db.entity.converter.ProductDataConverter;
import kg.benext.db.entity.model.Product;

import java.time.OffsetDateTime;

@Entity
@Table(name = "mt_doc_product", schema = "public")
public class MtDocProduct extends BaseDocument {

    @Convert(converter = ProductDataConverter.class)
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private Product data;  // теперь сразу Product, не String

    @Column(name = "mt_deleted")
    private Boolean mtDeleted;

    @Column(name = "mt_deleted_at")
    private OffsetDateTime mtDeletedAt;

    public Product getData() { return data; }
    public void setData(Product data) { this.data = data; }

    public Boolean getMtDeleted() { return mtDeleted; }
    public void setMtDeleted(Boolean mtDeleted) { this.mtDeleted = mtDeleted; }

    public OffsetDateTime getMtDeletedAt() { return mtDeletedAt; }
    public void setMtDeletedAt(OffsetDateTime mtDeletedAt) { this.mtDeletedAt = mtDeletedAt; }
}