package com.manas.gaurdrails.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manas.gaurdrails.model.BotModel;

@Repository
public interface  BotRepository extends JpaRepository<BotModel, UUID> {
    
}
