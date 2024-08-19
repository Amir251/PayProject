package com.snap.wallet.demo.wallet_demo.service;

import com.snap.wallet.demo.wallet_demo.dto.User;
import com.snap.wallet.demo.wallet_demo.dtorequest.UserRequest;
import com.snap.wallet.demo.wallet_demo.model.CredentialEntity;
import com.snap.wallet.demo.wallet_demo.model.RoleEntity;
import com.snap.wallet.demo.wallet_demo.model.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void createUser(UserRequest userRequest);
    RoleEntity getRoleByName(String name);

    void verifiedUserByKey(String key);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);

    CredentialEntity getUserCredentialById(Long id);

    void updateUser(UserRequest userRequest,Long userId);

    List<String> findAllUsersEmail();
}
