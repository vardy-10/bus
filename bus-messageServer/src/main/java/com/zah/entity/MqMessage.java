package com.zah.entity;

import java.io.Serializable;

public class MqMessage  implements Serializable {
    private static final long serialVersionUID = 4359709211352400087L;

    private  String orderId;

    private  String shiftsNumber;

    private  String shiftsDate;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShiftsNumber() {
        return shiftsNumber;
    }

    public void setShiftsNumber(String shiftsNumber) {
        this.shiftsNumber = shiftsNumber;
    }

    public String getShiftsDate() {
        return shiftsDate;
    }

    public void setShiftsDate(String shiftsDate) {
        this.shiftsDate = shiftsDate;
    }

    @Override
    public String toString() {
        return "MqMessage{" +
                "orderId='" + orderId + '\'' +
                ", shiftsNumber='" + shiftsNumber + '\'' +
                ", shiftsDate='" + shiftsDate + '\'' +
                '}';
    }
}
