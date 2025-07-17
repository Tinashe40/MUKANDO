package com.mukando.commons.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BaseDao<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
