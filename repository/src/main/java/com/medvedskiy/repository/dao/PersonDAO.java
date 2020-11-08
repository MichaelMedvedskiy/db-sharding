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
public class PersonDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="payment_id_seq")
    @SequenceGenerator(name="payment_id_seq", sequenceName="payment_id_seq", allocationSize=1)
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

}
