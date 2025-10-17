package com.xiaowang.shopping.shop.common.convertor;

import com.xiaowang.shopping.shop.common.domain.entity.Banner;
import com.xiaowang.shopping.shop.common.vo.BannerVO;

/**
 * 通用模块转换器
 * 
 * @author xiaowang
 */
public class CommonConvertor {

    /**
     * 转换为轮播图VO
     */
    public static BannerVO toBannerVO(Banner banner) {
        if (banner == null) {
            return null;
        }

        BannerVO vo = new BannerVO();
        vo.setId(banner.getId());
        vo.setImage(banner.getImage());
        vo.setTitle(banner.getTitle());
        vo.setLinkType(getLinkTypeCode(banner.getLinkType()));
        vo.setLinkValue(banner.getLinkValue());
        vo.setSort(banner.getSort());
        vo.setStatus(banner.getStatus());

        return vo;
    }

    /**
     * 获取链接类型代码
     */
    private static String getLinkTypeCode(Integer linkType) {
        if (linkType == null) {
            return "url";
        }

        switch (linkType) {
            case 1:
                return "product";
            case 2:
                return "category";
            case 3:
                return "url";
            default:
                return "url";
        }
    }
}

