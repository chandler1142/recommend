package com.chandler.demo.recommend.dao;

import com.chandler.demo.recommend.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {

    @Query("select u from UserEntity u where u.name=:name")
    UserEntity findByName(@Param("name") String name);

}
