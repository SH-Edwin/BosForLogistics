package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.bos.domain.base.TakeTime;

public interface TakeTimeRepostitory extends JpaRepository<TakeTime, Integer> {

}
