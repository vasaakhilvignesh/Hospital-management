package com.hospital.authservice.factory;

import com.hospital.authservice.entity.Role;
import com.hospital.authservice.exception.InvalidRoleException;
import org.springframework.stereotype.Component;

/**
 * Factory Provider that returns the appropriate concrete UserFactory based on user role.
 */
@Component
public class UserFactoryProvider {

    private final AdminFactory adminFactory;
    private final PatientFactory patientFactory;
    private final StaffFactory staffFactory;

    public UserFactoryProvider(AdminFactory adminFactory, PatientFactory patientFactory, StaffFactory staffFactory) {
        this.adminFactory = adminFactory;
        this.patientFactory = patientFactory;
        this.staffFactory = staffFactory;
    }

    public UserFactory getFactory(Role role) {
        if (role == null) {
            throw new InvalidRoleException("User role cannot be null");
        }
        return switch (role) {
            case ADMIN -> adminFactory;
            case PATIENT -> patientFactory;
            case DOCTOR, STAFF, RECEPTIONIST -> staffFactory;
        };
    }
}
