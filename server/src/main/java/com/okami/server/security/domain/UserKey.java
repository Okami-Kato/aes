package com.okami.server.security.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "key_store")
@Getter
@Setter
public class UserKey {

    @Id
    private Integer userId;

    @Column(name = "key")
    private byte[] key;

    @Column(name = "initialization_vector")
    private byte[] iv;

    @Column(name = "expiration_timestamp")
    private Instant expirationTimestamp;
}
