package cn.itcast.bos.service.take_delivery.impl;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.update.UpdateHelper.Operation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.take_delivery.WayBillRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.take_delivery.WayBillService;

@Service
@Transactional
public class WayBillServiceImpl implements WayBillService {

	@Autowired
	private WayBillRepository wayBillRepository;

	@Autowired
	private WayBillIndexRepository wayBillIndexRepository;

	@Override
	public void save(WayBill wayBill) {
		// 判断运单号是否存在
		WayBill persistantWayBill = wayBillRepository.findByWayBillNum(wayBill.getWayBillNum());
		if (persistantWayBill == null || persistantWayBill.getId() == null) {
			// 运单号不存在,保存索引,存入数据库
			wayBillIndexRepository.save(wayBill);
			wayBillRepository.save(wayBill);
		} else {
			// 运单号存在,应更新原运单号对应的内容,保存索引
			try {
				Integer id = persistantWayBill.getId();
				BeanUtils.copyProperties(persistantWayBill, wayBill);
				persistantWayBill.setId(id);
				wayBillRepository.save(persistantWayBill);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Page<WayBill> getPageData(WayBill wayBill, Pageable pageable) {
		// 判断有没有搜索条件,如果没有搜索条件,直接查询全部
		if (StringUtils.isBlank(wayBill.getWayBillNum()) && StringUtils.isBlank(wayBill.getSendAddress())
				&& StringUtils.isBlank(wayBill.getRecAddress()) && StringUtils.isBlank(wayBill.getSendProNum())
				&& (wayBill.getSignStatus() == null || wayBill.getSignStatus() == 0)) {
			// 无搜索条件
			return wayBillRepository.findAll(pageable);
		} else {
			// 有搜索条件,创建查询条件
			BoolQueryBuilder query = new BoolQueryBuilder();// 多条件组合的布尔查询对象
			// 增加查询条件
			if (StringUtils.isNotBlank(wayBill.getWayBillNum())) {
				TermQueryBuilder termQueryBuilder = new TermQueryBuilder("wayBillNum", wayBill.getWayBillNum());
				query.must(termQueryBuilder);
			}
			if (StringUtils.isNotBlank(wayBill.getSendAddress())) {
				// 情况一: 输入"上海",是词条的一部分,使用模糊匹配词条查询
				WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder("sendAddress",
						"*" + wayBill.getSendAddress() + "*");
				// 情况二:输入"上海花园小区",是多个词条的组合,需进行分词后进行每个词条的匹配查询
				QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendAddress())
						.field("sendAddress").defaultOperator(Operator.AND);
				// 两种情况取or关系
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
				boolQueryBuilder.should(wildcardQueryBuilder);
				boolQueryBuilder.should(queryStringQueryBuilder);
				query.must(boolQueryBuilder);
			}
			if (StringUtils.isNotBlank(wayBill.getRecAddress())) {
				WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder("recAddress",
						"*" + wayBill.getRecAddress() + "*");
				query.must(wildcardQueryBuilder);
			}
			if (StringUtils.isNotBlank(wayBill.getSendProNum())) {
				TermQueryBuilder termQueryBuilder = new TermQueryBuilder("sendProNum", wayBill.getSendProNum());
				query.must(termQueryBuilder);
			}
			if (wayBill.getSignStatus() != null && wayBill.getSignStatus() != 0) {
				TermQueryBuilder termQueryBuilder = new TermQueryBuilder("signStatus", wayBill.getSignStatus());
				query.must(termQueryBuilder);
			}
			SearchQuery searchQuery = new NativeSearchQuery(query);
			searchQuery.setPageable(pageable);
			return wayBillIndexRepository.search(searchQuery);
		}
	}

	@Override
	public WayBill findByWayBillNum(String wayBillNum) {
		return wayBillRepository.findByWayBillNum(wayBillNum);
	}

}
