package com.medichub.repository;

import com.medichub.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotoficationRepository extends JpaRepository <Notification, Long>{
}
