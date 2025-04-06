package whz.pti.eva.pizza.domain;

import java.math.BigDecimal;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import whz.pti.eva.base.domain.BaseEntity;

@Entity
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class Pizza extends BaseEntity<Long> {
 
    String name;
    BigDecimal priceLarge;
    BigDecimal priceMedium;
    BigDecimal priceSmall;

    String imagePath;

    public Pizza() {}
    
    public Pizza(String name, BigDecimal priceLarge, BigDecimal priceMedium, BigDecimal priceSmall, String imagePath) {
        this.name = name;
        this.priceLarge = priceLarge;
        this.priceMedium = priceMedium;
        this.priceSmall = priceSmall;
        this.imagePath = imagePath;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceLarge() {
        return priceLarge;
    }

    public void setPriceLarge(BigDecimal priceLarge) {
        this.priceLarge = priceLarge;
    }

    public BigDecimal getPriceMedium() {
        return priceMedium;
    }

    public void setPriceMedium(BigDecimal priceMedium) {
        this.priceMedium = priceMedium;
    }

    public BigDecimal getPriceSmall() {
        return priceSmall;
    }

    public void setPriceSmall(BigDecimal priceSmall) {
        this.priceSmall = priceSmall;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}