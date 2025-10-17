package com.xiaowang.shopping.shop.common.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaowang.shopping.shop.common.convertor.CommonConvertor;
import com.xiaowang.shopping.shop.common.domain.entity.Banner;
import com.xiaowang.shopping.shop.common.domain.service.CommonService;
import com.xiaowang.shopping.shop.common.param.FeedbackRequest;
import com.xiaowang.shopping.shop.common.vo.BannerVO;
import com.xiaowang.shopping.shop.common.vo.UploadResultVO;
import com.xiaowang.shopping.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 通用接口控制器
 * 
 * @author xiaowang
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonApiController {

    private final CommonService commonService;

    /**
     * 获取轮播图列表
     */
    @GetMapping("/banners")
    public Result<List<BannerVO>> getBanners() {
        // 查询轮播图列表
        List<Banner> banners = commonService.listBanners();

        // 转换为VO
        List<BannerVO> voList = banners.stream()
                .map(CommonConvertor::toBannerVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result<UploadResultVO> upload(@RequestParam("file") MultipartFile file) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            return Result.error("1001", "文件不能为空");
        }

        // 验证文件大小（10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return Result.error("1002", "文件大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return Result.error("1003", "文件名不能为空");
        }

        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        try {
            // 生成文件名：UUID + 扩展名
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

            // 生成日期路径：uploads/202401/
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            String uploadPath = "uploads/" + datePath + "/";

            // 创建目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            File destFile = new File(uploadPath + fileName);
            file.transferTo(destFile);

            // 构建文件URL（这里简化处理，实际应该使用配置的域名）
            String fileUrl = "http://example.com/" + uploadPath + fileName;

            // 构建结果
            UploadResultVO resultVO = new UploadResultVO();
            resultVO.setUrl(fileUrl);
            resultVO.setFileName(fileName);
            resultVO.setFileSize(file.getSize());

            return Result.success(resultVO);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("1004", "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 提交意见反馈
     */
    @PostMapping("/feedback")
    public Result<Void> submitFeedback(@RequestBody FeedbackRequest request) {
        // 参数校验
        if (StringUtils.isBlank(request.getType())) {
            return Result.error("1001", "反馈类型不能为空");
        }
        if (StringUtils.isBlank(request.getContent())) {
            return Result.error("1002", "反馈内容不能为空");
        }

        Long userId = getUserId();

        // 转换类型
        Integer typeCode = convertFeedbackType(request.getType());

        // 提交反馈
        commonService.submitFeedback(userId, typeCode, request.getContent(), 
                request.getImages(), request.getContact());

        return Result.success(null);
    }

    /**
     * 转换反馈类型
     */
    private Integer convertFeedbackType(String type) {
        switch (type) {
            case "complaint":
                return 1;
            case "suggestion":
                return 2;
            case "other":
                return 3;
            default:
                return 3;
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId() {
        String userIdStr = (String) StpUtil.getLoginId();
        return Long.valueOf(userIdStr);
    }
}

