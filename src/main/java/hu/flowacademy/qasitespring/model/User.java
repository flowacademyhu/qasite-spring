package hu.flowacademy.qasitespring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Data
 * generates getters, setters, toString, equals
 * */
@Data
/**
 * @Builder
 * creates a builder pattern, so we can use
 * User.builder().username("username").password("password").build() instead of
 * new User(null, "username", "password")
 * */
@Builder
/**
 * Generated a default constructor
 * */
@NoArgsConstructor
/**
 * Generated a constructor with all the arguments
 * */
@AllArgsConstructor
/**
 * Shows that the class represents a relational database table
 * */
@Entity
/**
 * Adds chance to set table details, such as name
 * */
@Table(name = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    @Column(name = "full_name")
    private String fullName;
    @Enumerated(EnumType.STRING)
    /**
     * Using @Enumerated, to store the enum value in database as string
     * the default config stores the index of the enum value like an array
     * */
    private Role role;
}

