package com.benorim.carhov.repository;

import com.benorim.carhov.entity.CarHovUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarHovUserRepository extends ListCrudRepository<CarHovUser, Long> {

}
