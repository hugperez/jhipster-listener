package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.Action;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A EntityHistory.
 */
@Entity
@Table(name = "entity_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EntityHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_login", nullable = false)
    private String userLogin;

    @NotNull
    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private Action actionType;

    @Lob
    @Column(name = "content")
    private byte[] content;

    @Column(name = "content_content_type")
    private String contentContentType;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private ZonedDateTime creationDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EntityHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserLogin() {
        return this.userLogin;
    }

    public EntityHistory userLogin(String userLogin) {
        this.setUserLogin(userLogin);
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public EntityHistory entityName(String entityName) {
        this.setEntityName(entityName);
        return this;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public EntityHistory entityId(Long entityId) {
        this.setEntityId(entityId);
        return this;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Action getActionType() {
        return this.actionType;
    }

    public EntityHistory actionType(Action actionType) {
        this.setActionType(actionType);
        return this;
    }

    public void setActionType(Action actionType) {
        this.actionType = actionType;
    }

    public byte[] getContent() {
        return this.content;
    }

    public EntityHistory content(byte[] content) {
        this.setContent(content);
        return this;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return this.contentContentType;
    }

    public EntityHistory contentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
        return this;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public EntityHistory creationDate(ZonedDateTime creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((EntityHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EntityHistory{" +
            "id=" + getId() +
            ", userLogin='" + getUserLogin() + "'" +
            ", entityName='" + getEntityName() + "'" +
            ", entityId=" + getEntityId() +
            ", actionType='" + getActionType() + "'" +
            ", content='" + getContent() + "'" +
            ", contentContentType='" + getContentContentType() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            "}";
    }
}
