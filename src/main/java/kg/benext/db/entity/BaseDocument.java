package kg.benext.db.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseDocument {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private String data;

    @Column(name = "mt_last_modified")
    private OffsetDateTime mtLastModified;

    @Column(name = "mt_version", nullable = false)
    private UUID mtVersion;

    @Column(name = "mt_dotnet_type")
    private String mtDotnetType;

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public OffsetDateTime getMtLastModified() { return mtLastModified; }
    public void setMtLastModified(OffsetDateTime mtLastModified) { this.mtLastModified = mtLastModified; }

    public UUID getMtVersion() { return mtVersion; }
    public void setMtVersion(UUID mtVersion) { this.mtVersion = mtVersion; }

    public String getMtDotnetType() { return mtDotnetType; }
    public void setMtDotnetType(String mtDotnetType) { this.mtDotnetType = mtDotnetType; }
}