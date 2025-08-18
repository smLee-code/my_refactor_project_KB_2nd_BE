package org.funding.payment.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.payment.vo.PaymentVO;

@Mapper
public interface PaymentDAO {
    // 결제 정보 저장
    void insertPayment(PaymentVO payment);
    
    // merchant_uid로 결제 정보 조회
    PaymentVO selectPaymentByMerchantUid(String merchantUid);
    
    // imp_uid로 결제 정보 조회
    PaymentVO selectPaymentByImpUid(String impUid);
    
    // 결제 상태 업데이트
    void updatePaymentStatus(PaymentVO payment);
    
    // 결제 완료 처리 (imp_uid, status, 결제 정보 업데이트)
    void updatePaymentComplete(PaymentVO payment);
    
    // 펀딩별 총 결제 금액 조회
    Long getTotalAmountByFundId(Long fundId);
}