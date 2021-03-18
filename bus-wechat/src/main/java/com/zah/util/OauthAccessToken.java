package com.zah.util;

public class OauthAccessToken {

    private String expires_in; //成功有效时间
    private String access_token;  // 普通Token
    private String errcode; //失败ID
    private String errmsg; //失败消息
    private long expiresIn; //过期时间 ， 默认2小时

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    /**
     *
     * @param expires_in 从微信服务器获取到的过期时间
     * @param access_token 从微信服务器获取到的过期时间access-token
     */
    public OauthAccessToken(String expires_in, String access_token) {
        this.access_token = access_token;
        //当前系统时间+上过期时间
        expiresIn = System.currentTimeMillis() + Integer.parseInt(expires_in) * 1000;
    }

    //判断token是否过期
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresIn;
    }
}