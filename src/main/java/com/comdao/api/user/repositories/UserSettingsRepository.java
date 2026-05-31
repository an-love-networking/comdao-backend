package com.comdao.api.user.repositories;

import com.comdao.api.user.entities.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}
