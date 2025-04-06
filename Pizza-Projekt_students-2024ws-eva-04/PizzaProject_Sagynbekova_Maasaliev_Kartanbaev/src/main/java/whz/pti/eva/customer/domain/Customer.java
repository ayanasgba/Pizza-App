package whz.pti.eva.customer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import whz.pti.eva.base.domain.BaseEntity;
import whz.pti.eva.deliveryAddress.domain.DeliveryAddress;
import whz.pti.eva.customer.domain.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class Customer extends BaseEntity<Long> {

    String firstName;
    String lastName;
    String loginName;
    String passwordHash;
    @Enumerated(EnumType.STRING)
    Role role;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    List<DeliveryAddress> deliveryAddresses = new ArrayList<>();

    public Customer() {}

    public Customer(Long id, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
         return super.getId();
        }

    public String getFirstName() {
         return firstName;
     }

    public void setFirstName(String firstName) {
         this.firstName = firstName;
     }

    public String getLastName() {
         return lastName;
     }

    public void setLastName(String lastName) {
         this.lastName = lastName;
     }

    public String getLoginName() {
         return loginName;
     }

    public Role getRole() {
            return role;
        }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLoginName(String loginName) {
         this.loginName = loginName;
     }

    public String getPasswordHash() {
         return passwordHash;
     }

    public void setPasswordHash(String passwordHash) {
         this.passwordHash = passwordHash;
     }

    public List<DeliveryAddress> getAddresses() {
            return deliveryAddresses;
        }

    public void setAddresses(List<DeliveryAddress> deliveryAddresses) {
            this.deliveryAddresses = deliveryAddresses;
        }

    public void addDeliveryAddress(DeliveryAddress address) {
        address.setCustomer(this);
        this.deliveryAddresses.add(address);
    }

    public void removeDeliveryAddress(DeliveryAddress address) {
        this.deliveryAddresses.remove(address);
        address.setCustomer(null);
    }
}