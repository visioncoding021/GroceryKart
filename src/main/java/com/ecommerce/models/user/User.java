package com.ecommerce.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql = "UPDATE user SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class User {
    @Id
    @TableGenerator(name = "user_gen", table = "id_gen", pkColumnName = "gen_name", valueColumnName = "gen_val", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_gen")
    private Long id;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    private boolean isDeleted;

    private boolean isActive;

    private boolean isExpired;

    private boolean isLocked;

    private int invalidAttemptCount;

    private LocalDateTime passwordUpdateDate;

    public User(){
        isActive = true;
    }

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private List<Role> roles;

    public List<Role> getRoles() {
        if (roles==null) roles = new ArrayList<>();
        return roles;
    }

}
