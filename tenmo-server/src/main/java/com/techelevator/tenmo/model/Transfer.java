package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class Transfer {

    final int TRANSFER_TYPE_REQUEST = 1;
    final int TRANSFER_TYPE_SEND = 2;
    final int TRANSFER_STATUS_PENDING = 1;
    final int TRANSFER_STATUS_APPROVED = 2;
    final int TRANSFER_STATUS_REJECTED = 3;

    int transferId;
    @Min(1)
    int accountTo;
    @Min(1)
    int accountFrom;
    int transferTypeId;
    int transferStatusId;
    @Min(1)
    BigDecimal amount;

    public Transfer(int transferId, int accountTo, int accountFrom, int transferTypeId, int transferStatusId, BigDecimal amount) {
        this.transferId = transferId;
        this.accountTo = accountTo;
        this.accountFrom = accountFrom;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.amount = amount;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
