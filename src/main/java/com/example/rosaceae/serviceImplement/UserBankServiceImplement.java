package com.example.rosaceae.serviceImplement;

import com.example.rosaceae.dto.Request.UserRequest.UserBankRequest;
import com.example.rosaceae.dto.Response.UserResponse.UserBankResponse;
import com.example.rosaceae.enums.Role;
import com.example.rosaceae.model.UserBank;
import com.example.rosaceae.repository.UserBankRepo;
import com.example.rosaceae.repository.UserRepo;
import com.example.rosaceae.service.UserBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserBankServiceImplement implements UserBankService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserBankRepo userBankRepo;

    @Override
    public UserBankResponse addUserBank(int userId, UserBankRequest userBankRequest) {
        var user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return UserBankResponse.builder()
                    .userBank(null)
                    .status(404)
                    .message("Shop not found")
                    .build();
        }
        if(!user.getRole().equals(Role.SHOP)){
            return UserBankResponse.builder()
                    .userBank(null)
                    .status(404)
                    .message("User is not a shop")
                    .build();
        }
        var userBank = userBankRepo.findByUserUsersID(userId).orElse(null);
        if (userBank != null) {
            return UserBankResponse.builder()
                    .userBank(null)
                    .status(400)
                    .message("Shop already has a bank account")
                    .build();
        }

        UserBank newUserBank = UserBank.builder()
                .bankName(userBankRequest.getBankName())
                .bankAccountNumber(userBankRequest.getBankAccountNumber())
                .user(user)
                .build();
        userBankRepo.save(newUserBank);
        return UserBankResponse.builder()
                .userBank(newUserBank)
                .status(200)
                .message("Shop bankAccount has been added")
                .build();
    }

    @Override
    public UserBankResponse updateUserBank(int bankId, UserBankRequest userBankRequest) {
        var bank = userBankRepo.findById(bankId).orElse(null);
        if (bank == null) {
            return UserBankResponse.builder()
                    .userBank(null)
                    .status(404)
                    .message("Bank account not found")
                    .build();
        }

        bank.setBankName(userBankRequest.getBankName());
        bank.setBankAccountNumber(userBankRequest.getBankAccountNumber());
        userBankRepo.save(bank);
        return UserBankResponse.builder()
                .userBank(bank)
                .status(200)
                .message("Shop bankAccount has been update")
                .build();
    }

    @Override
    public List<UserBank> getUserBank(int userId) {
        List<UserBank> userBanks = userBankRepo.findUserBankByUserUsersID(userId);
        if (userBanks == null || userBanks.isEmpty()) {
            return Collections.emptyList();
        }
        return userBanks;
    }


}
