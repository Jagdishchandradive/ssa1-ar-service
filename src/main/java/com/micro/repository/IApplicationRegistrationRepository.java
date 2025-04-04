package com.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.micro.entity.CitizenAppRegistrationEntity;

public interface IApplicationRegistrationRepository extends JpaRepository<CitizenAppRegistrationEntity, Integer>{

}
