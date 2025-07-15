package org.funding.Image.vo.enumType;

public enum ImageType {
    Project, Funding;
    public static ImageType fromString(String value){
        for(ImageType imageType : ImageType.values()){
            if(imageType.name().equals(value)){
                return imageType;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
