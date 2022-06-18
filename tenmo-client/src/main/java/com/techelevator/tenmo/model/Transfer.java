package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    final int TRANSFER_TYPE_REQUEST = 1;
    final int TRANSFER_TYPE_SEND = 2;
    final int TRANSFER_STATUS_PENDING = 1;
    final int TRANSFER_STATUS_APPROVED = 2;
    final int TRANSFER_STATUS_REJECTED = 3;

    private Long transferId;
    private int statusId;
    private Long accountFrom;
    private Long accountTo;
    private int typeId;
    private BigDecimal amount;
    private String fromName;
    private String toName;

    public Transfer() {
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public String getStatusName() {
        if(statusId == TRANSFER_STATUS_PENDING) {
            return "Pending";
        }
        else if(statusId == TRANSFER_STATUS_APPROVED) {
            return "Approved";
        }
        else if(statusId == TRANSFER_STATUS_REJECTED) {
            return "Rejected";
        }
        return "";
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public Long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Long accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTypeName() {
        if(typeId == TRANSFER_TYPE_REQUEST) {
            return "Request";
        }
        else if (typeId == TRANSFER_TYPE_SEND) {
            return "Send";
        }
        return "";
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
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
