package Designovel.Capstone.domain.style.style;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StyleRepository extends JpaRepository<Style, StyleId> {
}
