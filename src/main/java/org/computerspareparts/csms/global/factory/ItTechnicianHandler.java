package org.computerspareparts.csms.global.factory;



public class ItTechnicianHandler implements UserRoleHandler {
    @Override
    public String getDashboardUrl() {
        return "/employee/it/dashboard";
    }
}
