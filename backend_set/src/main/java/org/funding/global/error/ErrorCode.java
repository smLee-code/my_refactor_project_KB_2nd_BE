package org.funding.global.error;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "COMMON001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, "COMMON002", "지원하지 않는 HTTP 메소드입니다."),
    INTERNAL_SERVER_ERROR(500, "COMMON003", "서버 내부 오류가 발생했습니다."),

    // 멤버 에러
    MEMBER_NOT_FOUND(404, "MEMBER001", "해당 사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATION(400, "MEMBER002", "이미 사용 중인 이메일입니다."),
    LOGIN_INPUT_INVALID(400, "MEMBER003", "이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_ADMIN(401, "MEMBER004", "해당 멤버는 관리자가 아닙니다."),

    // 펀딩 에러
    FUNDING_NOT_FOUND(404, "FUNDING001", "해당 펀딩을 찾을 수 없습니다."),
    FAIL_FUNDING(400, "FUNDING002", "펀딩 생성에 실패하였습니다."),
    FAIL_FUND_UPDATE(402, "FUNDING003", "펀딩 수정에 실패하였습니다."),
    FAIL_DELETE_FUND(405, "FUNDING004", "펀딩 삭제중 에러가 발생하였습니다."),
    NOT_FUND_TYPE(406, "FUNDING005", "지원하지 않는 타입 정보입니다."),
    FAIL_ENTER_FUND(404, "FUNDING006", "펀딩 가입 처리중 오류 발생"),
    END_FUND(404, "FUNDING007", "해당 펀딩은 종료되었습니다."),

    // 프로젝트 에러
    PROJECT_NOT_FOUND(404, "PROJECT001", "해당 프로젝트를 찾을 수 없습니다."),
    FAIL_PROJECT(400, "PROJECT002", "프로젝트 생성에 실패하였습니다."),
    NOT_FOUND_PROJECT_TYPE(402, "PROJECT003", "찾을 수 없는 프로젝트 타입니다."),
    NOT_PROJECT_TYPE(406, "PROJECT004", "지원하지 않는 타입 정보입니다."),
    NOT_PROJECT_OWNER(404, "PROJECT005", "프로젝트의 작성자가 아닙니다"),

    // 인증 에러
    AUTHENTICATION_FAILED(401, "A001", "인증에 실패하였습니다."),
    ACCESS_DENIED(403, "A002", "해당 권한이 없습니다."),

    // 카테고리 에러
    SAME_CATEGORY_NAME(404, "CATEGORY001", "이미 존재하는 이름의 카테고리입니다."),
    CATEGORY_NOT_FOUND(404, "CATEGORY002", "존재하지 않는 카테고리 이름입니다."),

    // 댓글 에러
    NOT_FOUND_COMMENT(404, "COMMENT001", "삭제할 댓글이 없습니다."),

    // 이메일 전송 에러
    ERROR_EMAIL(400, "EMAIL001", "이메일 전송 과정에서 에러가 발생했습니다."),

    // openai 에러
    FAIL_VISION_AI(400, "AI001", "이미지 검증에 실패하였습니다."),

    // 결제 애러
    FAIL_PAYMENT(400, "PAYMENT001", "결제 정보 생성에 실패하였습니다."),
    ENTER_DONATE_AMOUNT(401, "PAYMENT002", "기부 금액을 입력해주세요"),
    NOT_FOUND_PAYMENT(404, "PAYMENT003", "결제 정보를 찾을 수 없습니다."),
    FAIL_VERIFY_PAYMENT(405, "PAYMENT004", "결제 검증에 실패하였습니다."),
    FAIL_SUCCESS_PAY(406, "PAYMENT005", "결제 완료 처리 실패"),
    NO_HAVE_PAYMENT(400, "PAYMENT006", "결제가 필요한 펀딩이 아닙니다"),
    NOT_FOUND_PORT_ONE_TOKEN(404, "PAYMENT007", "포트원 토큰 획득 실패"),

    // 투표 에러
    ALREADY_RETRY_VOTE(400, "RETRY VOTE001", "이미 투표하신 펀딩입니다."),
    NOT_END_FUND(401, "RETRY VOTE002", "아직 펀딩이 종료되지 않았습니다."),
    NOT_CANCEL_RETRY_VOTE(400, "RETRY VOTE003",  "투표 미 참여시 투표를 취소할 수 없습니다."),

    // 첼린지 에러
    NOT_FOUND_CHALLENGE(404, "CHALLENGE001", "해당 챌린지를 찾을 수 없습니다."),
    ALREADY_JOIN_CHALLENGE(400, "CHALLENGE002", "이미 챌린지에 가입한 회원입니다."),
    MISS_DATE_CHALLENGE(401, "CHALLENGE003", "챌린지에 참여 가능한 날짜가 아닙니다."),
    ALREADY_VERIFIED(401, "CHALLENGE004", "이미 인증된 날짜입니다."),
    NOT_CHALLENGE_MEMBER(402, "CHALLENGE005", "챌린지에 가입되지 않은 회원입니다."),
    NO_CHALLENGE(403, "CHALLENGE006", "해당 상품은 챌린지 상품이 아닙니다."),

    // 상품 에러
    NOT_FOUND_PRODUCT(404, "PRODUCT001", "해당 상품을 찾을 수 없습니다."),

    // 기부 에러
    OVER_DONATE_AMOUNT(400, "DONATE001", "기부 금액에서 벗어난 금액입니다."),
    NOT_FOUND_DONATE(401,"DONATE002", "기부 내역을 찾을 수 없습니다."),
    CAN_NOT_DELETE(404, "DONATE003", "기부 내역 삭제 권한이 없습니다."),
    DONE_DONATE(400, "DONATE004", "해당 기부는 종료되었습니다."),

    // 대출 에러
    AMOUNT_OVER(400, "LOAN001", "대출 한도가 넘었습니다."),
    NOT_FOUND_LOAN(401, "LOAN002", "신청 내역이 존재하지 않습니다."),
    ALREADY_LOAN(401, "LOAN003", "이미 지급 완료된 대출이라 취소가 불가합니다."),
    ALREADY_ACCEPT(400, "LOAN004", "이미 처리된 신청입니다."),
    NOT_PAYMENT(401, "LOAN004", "지급은 승인 후에 가능합니다."),

    // 저축 에러
    NOT_FOUND_SAVING(400, "SAVING001", "해당 저축에 신청되어있지 않습니다."),
    NO_CANCEL_SAVING(401, "SAVING002", "저축 해지 권한이 없습니다."),
    ALREADY_JOINED_SAVING(400, "SAVING003", "이미 가입한 저축 상품입니다.")
    ;


    private final int status;
    private final String code;
    private final String message;
}