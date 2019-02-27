package cn.itcast.bos.service.take_delivery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.take_delivery.WayBill;

public interface WayBillService {

	void save(WayBill wayBill);

	Page<WayBill> getPageData(WayBill wayBill, Pageable pageable);

	WayBill findByWayBillNum(String wayBillNum);

	

}
