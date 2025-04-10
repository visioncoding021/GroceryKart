package com.ecommerce.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long activation=null;
    private Long access=null;
    private Long refresh=null;
    private Long resetPassword=null;

    @OneToOne(mappedBy = "token")
    @JsonIgnore
    private User user;

}
