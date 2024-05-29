package com.Mixer.library.dto;

import com.Mixer.library.enums.WalletTransactionType;
import com.Mixer.library.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletHistoryDto
{
    private Long id;
    private double amount;
    private WalletTransactionType type;
    private String transactionStatus;
    private Wallet wallet;
}