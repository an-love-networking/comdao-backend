package com.comdao.api.user.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
public class UserSettings {
    @Id
    Long id;

    @OneToOne()
    @JoinColumn(name = "user_id")
    @MapsId
    User user;

    Boolean shouldNotify = true;
    Boolean useSms = false;
    Boolean includePromotion = true;
    Boolean useDarkMode = false;
    Boolean useTwoStepVerification = false;

    @Version
    private Long version;
}
