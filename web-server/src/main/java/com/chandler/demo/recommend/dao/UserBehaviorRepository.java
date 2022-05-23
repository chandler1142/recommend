package com.chandler.demo.recommend.dao;

import com.chandler.demo.recommend.entities.UserBehaviorEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserBehaviorRepository extends PagingAndSortingRepository<UserBehaviorEntity, Integer> {
}
