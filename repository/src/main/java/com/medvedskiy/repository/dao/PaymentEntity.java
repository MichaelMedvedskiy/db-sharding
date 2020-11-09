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
@Table(name="PAYMENT")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @JsonProperty("id")
    private Long id;

    @Column(name = "SENDER")
    @JsonProperty("sender")
    private Long sender;

    @Column(name = "RECEIVER")
    @JsonProperty("receiver")
    private Long receiver;

    @Column(name = "PRICE")
    @JsonProperty("price")
    private Long price;

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getPrice() {
        return price;
    }
}
