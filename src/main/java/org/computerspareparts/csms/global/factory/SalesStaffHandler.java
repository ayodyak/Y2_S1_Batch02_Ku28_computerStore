package org.computerspareparts.csms.global.factory;


public class SalesStaffHandler implements UserRoleHandler {
    @Override
    public String getDashboardUrl() {
        return "/employee/sales/dashboard";
    }
}
