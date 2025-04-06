package whz.pti.eva.base.service;

import java.util.List;

public interface BaseService <Z>{

	Z save(Z Z);
    List<Z> getAll();
    Z getById(Long id);
    Z update(Long id, Z z);
    boolean deleteById(Long id);
 
}