package com.Bassbazaar.library.service.impl;

import com.Bassbazaar.library.dto.WalletHistoryDto;
import com.Bassbazaar.library.enums.WalletTransactionType;
import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.model.Order;
import com.Bassbazaar.library.model.Wallet;
import com.Bassbazaar.library.model.WalletHistory;
import com.Bassbazaar.library.repository.CustomerRepository;
import com.Bassbazaar.library.repository.WalletHistoryRepository;
import com.Bassbazaar.library.repository.WalletRepository;
import com.Bassbazaar.library.service.WalletService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService
{
    private WalletRepository walletRepository;
    private WalletHistoryRepository walletHistoryRepository;

    private CustomerRepository customerRepository;

    public WalletServiceImpl(WalletRepository walletRepository,
                             CustomerRepository customerRepository,
                             WalletHistoryRepository walletHistoryRepository) {
        this.walletHistoryRepository=walletHistoryRepository;
        this.customerRepository = customerRepository;
        this.walletRepository = walletRepository;
    }

    /*         WalletController    */
    @Override
    public List<WalletHistoryDto> findAllById(long id) {
        List<WalletHistory> walletHistory =walletHistoryRepository.findAllById(id);
        List<WalletHistoryDto> walletHistoryDtoList=transferData(walletHistory);
        return walletHistoryDtoList;
    }

    /*OrderController / WalletController / WalletServiceImp */
    @Override
    public Wallet findByCustomer(Customer customer) {
        Wallet wallet;
        if(customer.getWallet()==null){
            wallet=new Wallet();
            wallet.setBalance(0.0);
            wallet.setCustomer(customer);
            walletRepository.save(wallet);
        }else{
            wallet=customer.getWallet();
        }
        return wallet;
    }
    /*            WalletController      */
    @Override
    public WalletHistory save(double amount, Customer customer) {
        Wallet wallet=customer.getWallet();
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionType.CREDITED);
        walletHistory.setAmount(amount);
        walletHistoryRepository.save(walletHistory);
        return walletHistory;
    }

    /*            WalletController      */
    @Override
    public WalletHistory findById(long id) {
        WalletHistory walletHistory=walletHistoryRepository.findById(id);
        return walletHistory;
    }

    /*            WalletController      */
    @Override
    public void updateWallet(WalletHistory walletHistory, Customer customer,boolean status) {
        Wallet wallet=customer.getWallet();
        if(status) {
            walletHistory.setTransactionStatus("Success");
            walletHistoryRepository.save(walletHistory);
            wallet.setBalance(wallet.getBalance()+ walletHistory.getAmount());
            walletRepository.save(wallet);
        }else{
            walletHistory.setTransactionStatus("Failed");
            walletHistoryRepository.save(walletHistory);
        }
    }

    /*     OrderController    */
    @Override
    public void debit(Wallet wallet,double totalPrice) {
        wallet.setBalance(wallet.getBalance()-totalPrice);
        walletRepository.save(wallet);
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionType.DEBITED);
        walletHistory.setAmount(totalPrice);
        walletHistory.setTransactionStatus("Success");
        walletHistoryRepository.save(walletHistory);
    }

    /*      OrderServiceImp  */
    @Override
    public void returnCredit(Order order, Customer customer) {
        Wallet wallet=customer.getWallet();
        wallet.setBalance(wallet.getBalance()+order.getTotalPrice());
        walletRepository.save(wallet);
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionType.CREDITED);
        walletHistory.setTransactionStatus("Success");
        walletHistory.setAmount(order.getTotalPrice());
        walletHistoryRepository.save(walletHistory);
    }

    /*   WalletServiceImp   */
    public List<WalletHistoryDto> transferData(List<WalletHistory> walletHistoryList){
        List<WalletHistoryDto>walletHistoryDtoList=new ArrayList<>();
        for(WalletHistory walletHistory : walletHistoryList){
            WalletHistoryDto walletHistoryDto=new WalletHistoryDto();
            walletHistoryDto.setId(walletHistory.getId());
            walletHistoryDto.setType(walletHistory.getType());
            walletHistoryDto.setAmount(walletHistory.getAmount());
            walletHistoryDto.setWallet(walletHistory.getWallet());
            walletHistoryDto.setTransactionStatus(walletHistory.getTransactionStatus());
            walletHistoryDtoList.add(walletHistoryDto);
        }
        return walletHistoryDtoList;
    }

    /*         ReferalController    */
    @Override
    public void addWalletToReferalEarn(long id) {
        try {
            Customer customer = customerRepository.getReferenceById(id);
            List<Customer> optionalReferrer = customerRepository.findByReferalToken(customer.getReferalToken());
            if (!optionalReferrer.isEmpty()) {
                Customer referrer = optionalReferrer.get(0);
                Wallet wallet = findByCustomer(referrer);
                if (wallet != null) {
                    wallet.setBalance(wallet.getBalance() + 100);
                    System.out.println("Wallet balance increased successfully");
                } else {
                    wallet = new Wallet();
                    wallet.setCustomer(referrer);
                    wallet.setBalance(100);
                }
                walletRepository.save(wallet);
                WalletHistory walletHistory = new WalletHistory();
                walletHistory.setWallet(wallet);
                walletHistory.setAmount(100);
                walletHistory.setTransactionStatus("Credit");
                walletHistory.setTrasactionType("Referral Earning");
                walletHistory.setTransationDate(new Date());
                walletHistoryRepository.save(walletHistory);
                System.out.println("Wallet history saved successfully");
            } else {
                System.out.println("Referrer not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
