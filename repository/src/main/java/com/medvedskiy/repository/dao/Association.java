package com.medvedskiy.repository.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="ASSOCIATION")
public class Association {

    @Id
    @Column(name = "SENDER")
    private Long sender;

    @Column(name = "DBID")
    private Integer dbId;

    public Integer getDbId() {
        return this.dbId;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }
}
