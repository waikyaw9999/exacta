package com.exacta.timer.repository;

import com.exacta.timer.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByOrderByNameAsc();
}
