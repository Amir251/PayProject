package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.dto.User;
import com.snap.wallet.demo.wallet_demo.dtorequest.UserRequest;
import com.snap.wallet.demo.wallet_demo.enumeration.EventType;
import com.snap.wallet.demo.wallet_demo.enumeration.WalletType;
import com.snap.wallet.demo.wallet_demo.event.UserEvent;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.*;
import com.snap.wallet.demo.wallet_demo.repository.*;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.snap.wallet.demo.wallet_demo.util.UserUtil.createUserEntity;
import static com.snap.wallet.demo.wallet_demo.util.UserUtil.fromUserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final WalletRepository walletRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public void createUser(UserRequest userRequest) {
        validateEmailAndPhone(userRequest.getEmail(), userRequest.getPhone());
        UserEntity saved = userRepository.save(createNewUser(userRequest));
        CredentialEntity credentialEntity = new CredentialEntity(encoder.encode(userRequest.getPassword()), saved);
        credentialRepository.save(credentialEntity);
        ConfirmationEntity confirmationEntity = new ConfirmationEntity(saved);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(saved, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }

    @Override
    @Transactional
    public void verifiedUserByKey(String key) {
        ConfirmationEntity userConfirmation = getUserConfirmation(key);
        UserEntity userEntityByEmail = getUserEntityByEmail(userConfirmation.getUserEntity().getEmail());
        userEntityByEmail.setEnabled(true);
        userRepository.save(userEntityByEmail);
        confirmationRepository.delete(userConfirmation);
        defineWallet(userEntityByEmail);
    }

    private void defineWallet(UserEntity userEntityByEmail) {
        Wallet wallet = new Wallet(userEntityByEmail);
        wallet.setUser(userEntityByEmail);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletType.ACTIVE.getValue());
        walletRepository.save(wallet);
    }


    @Override
    public Optional<UserEntity> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByNameIgnoreCase(name).orElseThrow(() -> new ApiException(ExceptionMessageCode.ROLE_NOT_FOUND));
    }

    @Override
    public User getUserByUserId(String userId) {
        UserEntity userLoaded = userRepository.findByUserId(userId).orElseThrow(() -> new ApiException("User Not Found"));
        return fromUserEntity(userLoaded, userLoaded.getRole(), getUserCredentialById(userLoaded.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntityByEmail = getUserEntityByEmail(email);
        return fromUserEntity(userEntityByEmail, userEntityByEmail.getRole(), getUserCredentialById(userEntityByEmail.getId()));
    }

    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        return credentialRepository.findByUserEntityId(userId).orElseThrow(() -> new ApiException(ExceptionMessageCode.UNABLE_FINE_USER_CREDENTIAL));
    }

    @Override
    public void updateUser(UserRequest userRequest, Long userId) {
        validateEmailAndPhoneForUpdate(userRequest.getEmail(), userRequest.getPhone(), userId);
        UserEntity userLoaded = userRepository.findById(userId).orElseThrow(() -> new ApiException(ExceptionMessageCode.USER_NOT_FOUND));
        userLoaded.setFirstName(userRequest.getFirstName());
        userLoaded.setLastName(userRequest.getLastName());
        userLoaded.setEmail(userRequest.getEmail());
        userLoaded.setPhone(userRequest.getPhone());
        userRepository.save(userLoaded);
    }

    @Override
    public List<String> findAllUsersEmail() {
        return userRepository.findAll().stream().filter(userEntity -> !userEntity.getRole().getName().equals("ADMIN"))
                .map(UserEntity::getEmail).toList();
    }

    private UserEntity createNewUser(UserRequest userRequest) {
        RoleEntity roleByName = getRoleByName("USER");
        return createUserEntity(userRequest, roleByName);
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key).orElseThrow(() -> new ApiException(ExceptionMessageCode.USER_NOT_FOUND_BY_THIS_KEY));
    }

    private UserEntity getUserEntityByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException(ExceptionMessageCode.USER_NOT_FOUND));
    }

    private void validateEmailAndPhone(String email, String phone) {
        userRepository.findByEmailIgnoreCase(email)
                .ifPresent(userEntity -> {
                    log.error(ExceptionMessageCode.EMAIL_ALREADY_IN_USE);
                    throw new ApiException(ExceptionMessageCode.EMAIL_ALREADY_IN_USE);
                });
        userRepository.findByPhone(phone)
                .ifPresent(userEntity -> {
                    log.error(ExceptionMessageCode.PHONE_ALREADY_IN_USE);
                    throw new ApiException(ExceptionMessageCode.PHONE_ALREADY_IN_USE);
                });
    }

    private void validateEmailAndPhoneForUpdate(String email, String phone, Long id) {
        userRepository.findByEmailIgnoreCase(email).filter(userEntity -> !userEntity.getId().equals(id))
                .ifPresent(userEntity -> {
                    log.error(ExceptionMessageCode.EMAIL_ALREADY_IN_USE);
                    throw new ApiException(ExceptionMessageCode.EMAIL_ALREADY_IN_USE);
                });
        userRepository.findByPhone(phone).filter(userEntity -> !userEntity.getId().equals(id))
                .ifPresent(userEntity -> {
                    log.error(ExceptionMessageCode.PHONE_ALREADY_IN_USE);
                    throw new ApiException(ExceptionMessageCode.PHONE_ALREADY_IN_USE);
                });
    }
}
