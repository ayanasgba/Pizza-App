package whz.pti.eva.base.domain;

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@MappedSuperclass
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class BaseEntity <PK extends Serializable> {

	@Id
	@GeneratedValue
	PK id;
	
	public PK getId() {
		return id;
	}
	
	public void setId(PK id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		BaseEntity<?> other = (BaseEntity<?>) obj;

		// Если id не установлен (null), объекты не равны
		if (this.getId() == null || other.getId() == null) return false;

		// Сравнение идентификаторов
		return this.getId().equals(other.getId());
	}

	
}
