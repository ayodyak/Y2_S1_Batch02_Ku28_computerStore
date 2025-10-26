package org.computerspareparts.csms.global.factory;


import org.computerspareparts.csms.global.entity.Role;

public class UserRoleFactory {

    public static UserRoleHandler getHandler(Role role) {
        switch (role) {
            case MANAGER:
                return new ManagerHandler();
            case SALES_STAFF:
                return new SalesStaffHandler();
            case FINANCE_ACCOUNTANT:
                return new AccountantHandler();
            case IT_TECHNICIAN:
                return new ItTechnicianHandler();
            case CUSTOMER:
                return new CustomerHandler();
            case SUPPLIER:
                return new SupplierHandler();
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
