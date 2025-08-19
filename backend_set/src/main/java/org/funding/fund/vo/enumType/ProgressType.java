package org.funding.fund.vo.enumType;

public enum
ProgressType {
    Launch, End;
    public static ProgressType fromString(String value) {
        for (ProgressType type : ProgressType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지정되지 않은 진행 타입:" + value);
    }
}
