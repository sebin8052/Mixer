package com.Mixer.library.model;

import com.Mixer.library.enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "wallets_history")
public class WalletHistory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_history_id")
    private Long id;
    private double amount;
    private WalletTransactionType type;
    private String trasactionType;
    private String transactionStatus;
    private Date transationDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id",referencedColumnName = "wallet_id")
    private Wallet wallet;
}

