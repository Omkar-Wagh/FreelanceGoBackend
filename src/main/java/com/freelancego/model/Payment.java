package com.freelancego.model;

import com.freelancego.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Double amount;              // actual transaction amount

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false, nullable = false)
    private OffsetDateTime paidAt;     // when payment was processed
    private String transactionId;      // reference from gateway
    private String method;             // CARD, UPI, BANK_TRANSFER, etc.

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.NOT_PAID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "payer_id")
    private User payer;

    @ManyToOne
    @JoinColumn(name = "payee_id")
    private User payee;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public OffsetDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(OffsetDateTime paidAt) { this.paidAt = paidAt; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }

    public User getPayer() { return payer; }
    public void setPayer(User payer) { this.payer = payer; }

    public User getPayee() { return payee; }
    public void setPayee(User payee) { this.payee = payee; }
}
