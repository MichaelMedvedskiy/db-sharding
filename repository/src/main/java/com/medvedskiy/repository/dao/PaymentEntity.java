package com.medvedskiy.repository.dao;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Payment representation
 */
@Entity
@Table(name="PAYMENT")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SENDER")
    private Long sender;

    @Column(name = "RECEIVER")
    private Long receiver;

    @Column(name = "PRICE")
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
