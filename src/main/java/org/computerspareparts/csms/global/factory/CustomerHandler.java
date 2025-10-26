package org.computerspareparts.csms.global.factory;

public class CustomerHandler implements UserRoleHandler {
    @Override
    public String getDashboardUrl() {

        return "/customer/dashboard";
    }
}
