package com.comdao.api.user.entities;

import com.comdao.api.user.entities.enums.Role;
import com.comdao.api.user.entities.enums.Tier;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(
        name = "users",
        check = @CheckConstraint(
                name = "mandatory_email_or_phone_with_username",
                constraint = "(email IS NOT NULL OR phone IS NOT NULL) AND username IS NOT NULL"
        )
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", allocationSize = 1, sequenceName = "user_seq")
    private Long id;

    private String username;
    private String email;
    private String phone;
    private String password;

    private String fullName;
    private String address = "";
    private Date dateOfBirth;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate created = LocalDate.now();
    @Enumerated(value = EnumType.STRING)
    private Tier tier = Tier.PLASTIC;

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;
    private Boolean isActive = true;
    private LocalDateTime lastChangePassword;

    @JsonIgnore
    private String activeJwtToken;

    private Double rewardPoint = 0.0;
    @JsonIgnore
    @Version
    private Long version;

    public void addRewardPoint(Double rewardPoint) {
        this.rewardPoint += rewardPoint;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
