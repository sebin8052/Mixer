package com.Bassbazaar.library.service;

import com.Bassbazaar.library.dto.WalletHistoryDto;
import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.model.Order;
import com.Bassbazaar.library.model.Wallet;
import com.Bassbazaar.library.model.WalletHistory;

import java.util.List;

public interface WalletService
{
    List<WalletHistoryDto> findAllById(long id);
    Wallet findByCustomer(Customer customer);
    WalletHistory save(double amount, Customer customer);
    WalletHistory findById(long id);
    void updateWallet(WalletHistory walletHistory, Customer customer, boolean status);
    void debit(Wallet wallet,double totalPrice);
    void returnCredit(Order order, Customer customer);
    void addWalletToReferalEarn(long id);
}
