package com.medvedskiy.repository.dao.association;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Association of sender and DB index for lookups for sharding rule preservation
 */
@Entity
@Table(name = "ASSOCIATION")
public class AssociationEntity {

    @Id
    @Column(name = "SENDER")
    private Long sender;

    @Column(name = "DBID")
    private Integer dbId;

    public Integer getDbId() {
        return this.dbId;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }
}
