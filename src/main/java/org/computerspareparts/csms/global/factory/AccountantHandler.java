package org.computerspareparts.csms.global.factory;


public class AccountantHandler implements UserRoleHandler {
    @Override
    public String getDashboardUrl() {
        return "/employee/accountant/dashboard";
    }
}
