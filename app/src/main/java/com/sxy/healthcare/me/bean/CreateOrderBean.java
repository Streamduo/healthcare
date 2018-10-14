package com.sxy.healthcare.me.bean;

public class CreateOrderBean {

    /**
     * success : true
     * code : 错误码(原则上：错误信息的时候提示出来,特殊场景，正确的时候可能会有返回值)
     * msg : 提示信息(原则上：错误信息的时候提示出来,特殊场景，正确的时候可能会有返回值)
     * data : {"billNo":"订单编号","payBsType":"业务类型","paymentWay":"支付方式"}
     */

    private boolean success;
    private String code;
    private String msg;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * billNo : 订单编号
         * payBsType : 业务类型
         * paymentWay : 支付方式
         */

        private String billNo;
        private String payBsType;
        private String paymentWay;

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String getPayBsType() {
            return payBsType;
        }

        public void setPayBsType(String payBsType) {
            this.payBsType = payBsType;
        }

        public String getPaymentWay() {
            return paymentWay;
        }

        public void setPaymentWay(String paymentWay) {
            this.paymentWay = paymentWay;
        }
    }
}
