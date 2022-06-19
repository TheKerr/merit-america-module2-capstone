package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class Transfer {

    public final static int TRANSFER_TYPE_REQUEST = 1;
    public final static int TRANSFER_TYPE_SEND = 2;
    public final static int TRANSFER_STATUS_PENDING = 1;
    public final static int TRANSFER_STATUS_APPROVED = 2;
    public final static int TRANSFER_STATUS_REJECTED = 3;

    int transferId;
    @Min(0)
    int accountTo;
    @Min(0)
    int accountFrom;
    int typeId;
    int statusId;
    @Min(0)
    BigDecimal amount;
    String fromName;
    String toName;

    public Transfer() {
    }

    public Transfer(int transferId, int accountTo, int accountFrom, int typeId, int statusId, BigDecimal amount) {
        this.transferId = transferId;
        this.accountTo = accountTo;
        this.accountFrom = accountFrom;
        this.typeId = typeId;
        this.statusId = statusId;
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

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
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

    public int getTypeId() {
        return typeId;
    }

    public int getStatusId() {
        return statusId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }
}
