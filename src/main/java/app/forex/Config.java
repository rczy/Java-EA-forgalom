package app.forex;

import com.oanda.v20.account.AccountID;

public class Config {
    private Config() {}
    public static final String URL = "https://api-fxpractice.oanda.com";
    public static final String TOKEN = "983fa4d300d30907fd7f311691527aa6-ab49f0e2216a109385cfcc2757e8ef95";
    public static final AccountID ACCOUNTID = new AccountID("101-004-27558407-001");
}
