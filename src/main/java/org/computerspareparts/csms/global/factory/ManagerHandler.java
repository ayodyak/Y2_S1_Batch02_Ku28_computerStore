package org.computerspareparts.csms.global.factory;


public class ManagerHandler implements UserRoleHandler {
    @Override
    public String getDashboardUrl() {
        // Return the controller route for manager dashboard
        return "/employee/manager/dashboard";
    }
}
