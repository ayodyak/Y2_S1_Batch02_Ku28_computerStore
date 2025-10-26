package org.computerspareparts.csms.global.factory;


public class SupplierHandler implements UserRoleHandler {
    @Override
    public String getDashboardUrl() {
        // Return the controller path for supplier dashboard
        return "/supplier/dashboard";
    }
}
