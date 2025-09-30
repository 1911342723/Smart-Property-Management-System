package com.property.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.property.dto.AIWorkOrderResponse;
import com.property.entity.Room;
import com.property.entity.SysUser;
import com.property.mapper.RoomMapper;
import com.property.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * AI服务类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "ai.openai", name = "enabled", havingValue = "true")
public class AIService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.base-url}")
    private String baseUrl;

    @Value("${ai.openai.model}")
    private String modelName;

    @Value("${ai.openai.temperature:0.7}")
    private Double temperature;

    @Value("${ai.openai.max-tokens:1000}")
    private Integer maxTokens;

    /**
     * 分析图片并生成工单信息
     * 
     * @param image 图片文件
     * @param roomId 房屋ID
     * @param category 工单类别
     * @return AI工单响应
     */
    public AIWorkOrderResponse analyzeImageAndGenerateWorkOrder(MultipartFile image, Long roomId, String category) throws IOException {
        System.out.println("=== AIService.analyzeImageAndGenerateWorkOrder ===");
        
        // 1. 获取当前用户信息
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("未登录或用户不存在");
        }
        System.out.println("Current user: " + currentUser.getRealName());

        // 2. 获取房屋信息
        Room room = null;
        if (roomId != null) {
            room = roomMapper.selectRoomWithDetails(roomId);
            System.out.println("Room: " + (room != null ? formatRoomAddress(room) : "null"));
        }

        // 3. 上传图片并获取URL（用于保存到工单）
        String imageUrl = uploadImage(image);
        System.out.println("Image uploaded: " + imageUrl);

        // 4. 将图片转换为Base64（用于发送给豆包AI）
        String base64Image = convertImageToBase64(image);
        System.out.println("Image converted to base64, length: " + base64Image.length());

        // 5. 调用AI模型分析图片
        String aiAnalysis = analyzeImageWithDoubao(base64Image, category);
        System.out.println("AI analysis result: " + aiAnalysis);

        // 5. 解析AI响应
        JSONObject analysisResult = parseAIResponse(aiAnalysis);
        System.out.println("Parsed result: " + analysisResult.toJSONString());

        // 6. 构建响应
        AIWorkOrderResponse response = new AIWorkOrderResponse();
        response.setTitle(analysisResult.getString("title"));
        response.setDescription(analysisResult.getString("description"));
        response.setSuggestedCategory(analysisResult.getString("category"));
        response.setSuggestedPriority(analysisResult.getString("priority"));
        response.setImageUrl(imageUrl);
        response.setSubmitterName(currentUser.getRealName());
        response.setSubmitterPhone(currentUser.getPhone());
        response.setRoomAddress(room != null ? formatRoomAddress(room) : "");

        return response;
    }

    /**
     * 使用豆包API分析图片
     * 
     * @param base64Image Base64编码的图片
     * @param category 工单类别
     * @return AI分析结果
     */
    private String analyzeImageWithDoubao(String base64Image, String category) {
        try {
            System.out.println("=== Calling Doubao API ===");
            System.out.println("API URL: " + baseUrl);
            System.out.println("Model: " + modelName);
            System.out.println("Image base64 length: " + base64Image.length());
            
            // 构建提示词
            String prompt = buildPrompt(category);
            System.out.println("Prompt: " + prompt);
            
            // 构建请求体（按照豆包官方格式）
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);
            
            // 构建messages数组
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            
            // 构建content数组（包含图片和文本）
            JSONArray content = new JSONArray();
            
            // 添加图片（使用Base64格式）
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrlObj = new JSONObject();
            // 使用data URI格式：data:image/jpeg;base64,xxxxx
            imageUrlObj.put("url", "data:image/jpeg;base64," + base64Image);
            imageContent.put("image_url", imageUrlObj);
            content.add(imageContent);
            
            // 添加文本
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", prompt);
            content.add(textContent);
            
            userMessage.put("content", content);
            messages.add(userMessage);
            requestBody.put("messages", messages);
            
            System.out.println("Request body: " + requestBody.toJSONString());
            
            // 创建HTTP请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            System.out.println("=== Doubao API Response ===");
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
            
            // 解析响应
            JSONObject responseJson = JSON.parseObject(response.getBody());
            JSONArray choices = responseJson.getJSONArray("choices");
            if (choices != null && choices.size() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                String result = message.getString("content");
                System.out.println("AI Response Content: " + result);
                return result;
            }
            
            throw new RuntimeException("AI响应格式错误");
            
        } catch (Exception e) {
            System.err.println("=== Doubao API Error ===");
            e.printStackTrace();
            throw new RuntimeException("AI分析失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构建AI提示词
     * 
     * @param category 工单类别
     * @return 提示词
     */
    private String buildPrompt(String category) {
        String categoryName = getCategoryName(category);
        
        return String.format(
                "你是一个专业的物业管理助手。请分析这张图片，并为业主生成一个%s工单。\n" +
                "请以JSON格式返回以下信息：\n" +
                "{\n" +
                "  \"title\": \"工单标题（简洁明了，10-20字）\",\n" +
                "  \"description\": \"详细描述问题（包括问题现象、位置、影响等，50-200字）\",\n" +
                "  \"category\": \"工单类别（REPAIR-维修/COMPLAINT-投诉/SUGGESTION-建议）\",\n" +
                "  \"priority\": \"优先级（LOW-低/MEDIUM-中/HIGH-高/URGENT-紧急）\"\n" +
                "}\n\n" +
                "分析要点：\n" +
                "1. 识别图片中的问题（如设备损坏、环境问题、安全隐患等）\n" +
                "2. 评估问题的严重程度和紧急程度\n" +
                "3. 使用专业但易懂的语言描述\n" +
                "4. 如果图片不清晰或无法识别问题，请说明需要更清晰的图片\n\n" +
                "请直接返回JSON，不要包含其他说明文字。",
                categoryName
        );
    }

    /**
     * 解析AI响应
     * 
     * @param aiResponse AI响应文本
     * @return 解析后的JSON对象
     */
    private JSONObject parseAIResponse(String aiResponse) {
        try {
            // 尝试直接解析JSON
            return JSON.parseObject(aiResponse);
        } catch (Exception e) {
            // 如果失败，尝试提取JSON部分
            String jsonStr = extractJSON(aiResponse);
            if (jsonStr != null) {
                return JSON.parseObject(jsonStr);
            }
            
            // 如果都失败，返回默认值
            JSONObject defaultResult = new JSONObject();
            defaultResult.put("title", "待确认的维修问题");
            defaultResult.put("description", "AI分析结果：" + aiResponse);
            defaultResult.put("category", "REPAIR");
            defaultResult.put("priority", "MEDIUM");
            return defaultResult;
        }
    }

    /**
     * 从文本中提取JSON
     * 
     * @param text 文本
     * @return JSON字符串
     */
    private String extractJSON(String text) {
        if (text == null) return null;
        
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        
        return null;
    }

    /**
     * 将图片转换为Base64编码
     * 
     * @param file 图片文件
     * @return Base64字符串
     */
    private String convertImageToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 上传图片
     * 
     * @param file 图片文件
     * @return 图片URL
     */
    private String uploadImage(MultipartFile file) throws IOException {
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + extension;

        // 创建目录结构
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = "repair/" + dateDir + "/" + fileName;

        // 获取绝对路径
        String absoluteUploadPath = new File(uploadPath).getAbsolutePath();
        if (!absoluteUploadPath.endsWith(File.separator)) {
            absoluteUploadPath += File.separator;
        }

        // 创建目录
        String fullDirPath = absoluteUploadPath + "repair" + File.separator + dateDir.replace("/", File.separator);
        File uploadDir = new File(fullDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        File destFile = new File(uploadDir, fileName);
        Files.copy(file.getInputStream(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // 返回URL
        return urlPrefix + relativePath;
    }

    /**
     * 获取当前登录用户
     * 
     * @return 用户信息
     */
    private SysUser getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();

                // 先尝试按用户名查找
                SysUser user = sysUserMapper.selectOne(
                        new QueryWrapper<SysUser>().eq("username", username).eq("deleted", 0)
                );

                // 如果按用户名找不到，尝试按手机号查找
                if (user == null) {
                    user = sysUserMapper.selectOne(
                            new QueryWrapper<SysUser>().eq("phone", username).eq("deleted", 0)
                    );
                }

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化房屋地址
     * 
     * @param room 房屋信息
     * @return 格式化后的地址
     */
    private String formatRoomAddress(Room room) {
        StringBuilder address = new StringBuilder();
        if (room.getBuildingName() != null) {
            address.append(room.getBuildingName());
        }
        if (room.getUnitName() != null) {
            address.append(room.getUnitName());
        }
        if (room.getRoomNo() != null) {
            address.append(room.getRoomNo());
        }
        return address.toString();
    }

    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 获取工单类别名称
     * 
     * @param category 类别代码
     * @return 类别名称
     */
    private String getCategoryName(String category) {
        if (category == null) return "工单";
        switch (category) {
            case "REPAIR":
                return "维修";
            case "COMPLAINT":
                return "投诉";
            case "SUGGESTION":
                return "建议";
            default:
                return "工单";
        }
    }
}