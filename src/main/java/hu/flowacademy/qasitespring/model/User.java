package hu.flowacademy.qasitespring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

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
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}

