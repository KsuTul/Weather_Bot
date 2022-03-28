package com.example.weatherbot.repository;

import com.example.weatherbot.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long>{

}
