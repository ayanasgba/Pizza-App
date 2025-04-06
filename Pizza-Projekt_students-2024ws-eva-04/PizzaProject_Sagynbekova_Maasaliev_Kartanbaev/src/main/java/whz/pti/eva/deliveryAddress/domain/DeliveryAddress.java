package whz.pti.eva.deliveryAddress.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import whz.pti.eva.base.domain.BaseEntity;
import whz.pti.eva.customer.domain.Customer;

@Entity
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class DeliveryAddress extends BaseEntity<Long> {

	String street;
	String houseNumber;
	String town;
	String postalCode;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	Customer customer;

    public DeliveryAddress() {}

    public DeliveryAddress(String street, String houseNumber, String town, String postalCode) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.town = town;
        this.postalCode = postalCode;
    }

    public Long getId() {
	    return super.getId();
	}

	public String getStreet() {
	    return street;
	}

	public void setStreet(String street) {
	    this.street = street;
	}

	public String getHouseNumber() {
	    return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
	    this.houseNumber = houseNumber;
	}

	public String getTown() {
	    return town;
	}

	public void setTown(String town) {
	    this.town = town;
	}

	public String getPostalCode() {
	    return postalCode;
	}

	public void setPostalCode(String postalCode) {
	    this.postalCode = postalCode;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
