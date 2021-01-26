package hu.flowacademy.qasitespring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    /**
     * Using the full_name as json property name
     * when we creating json from User object
     * without this, default property name would be fullName
     *
     * When reading a json, the parser (Jackson data binder) will looking for
     * full_name, instead of default fullName
     */
    @JsonProperty("full_name")
    @Column(name = "full_name")
    private String fullName;
    @Enumerated(EnumType.STRING)
    /**
     * Using @Enumerated, to store the enum value in database as string
     * the default config stores the index of the enum value like an array
     * */
    private Role role;
}

