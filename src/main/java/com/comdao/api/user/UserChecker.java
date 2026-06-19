package com.comdao.api.user;

import com.comdao.api.user.entities.User;
import com.comdao.api.user.exceptions.UpdateInfoCollisionException;
import com.comdao.api.user.exceptions.UserDisabledException;
import com.comdao.api.user.exceptions.UserNotFoundException;
import com.comdao.api.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class UserChecker {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User checkExistAndActiveById(Long userId) throws
            UserDisabledException, UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User id #" + userId + " is not found")
        );

        if (!user.getIsActive())
            throw new UserDisabledException("User id #" + userId + "is disabled");

        return user;
    }


    @Transactional(readOnly = true)
    public User checkExistAndActiveByUsername(String username) throws
            UserDisabledException, UserNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User " + username + " is not found")
        );

        if (!user.getIsActive())
            throw new UserDisabledException("User id #" + username + "is disabled");

        return user;
    }

    @Transactional(readOnly = true)
    void checkEmailPhoneUsernameAvailable(String email, String phone, String username, Long id)
            throws UpdateInfoCollisionException {
        HashMap<String, Object> details = new HashMap<>();

        if (email != null &&
                userRepository.existsByEmailAndIdIsNot(email, id))
            details.put("email", "Email has already been taken");

        if (phone != null &&
                userRepository.existsByPhoneAndIdIsNot(phone, id))
            details.put("phone", "Phone number has already been taken");

        if (username != null &&
                userRepository.existsByUsernameAndIdIsNot(username, id))
            details.put("username", "Username has already been taken");

        if (!details.isEmpty())
            throw new UpdateInfoCollisionException("Info collision", details);
    }
}
