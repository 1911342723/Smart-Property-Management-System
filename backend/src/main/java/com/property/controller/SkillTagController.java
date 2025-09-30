package com.property.controller;

import com.property.dto.Result;
import com.property.entity.SkillTag;
import com.property.service.SkillTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技能标签控制器
 */
@Api(tags = "技能标签管理")
@RestController
@RequestMapping("/skill-tags")
@RequiredArgsConstructor
public class SkillTagController {
    
    private final SkillTagService skillTagService;
    
    @ApiOperation("获取所有启用的技能标签")
    @GetMapping("/active")
    public Result<List<SkillTag>> getAllActiveSkills() {
        List<SkillTag> skills = skillTagService.getAllActiveSkills();
        return Result.success(skills);
    }
    
    @ApiOperation("获取技能标签（按分类分组）")
    @GetMapping("/grouped")
    public Result<Map<String, List<SkillTag>>> getSkillsGroupedByCategory() {
        List<SkillTag> skills = skillTagService.getAllActiveSkills();
        Map<String, List<SkillTag>> grouped = skills.stream()
            .collect(Collectors.groupingBy(SkillTag::getCategory));
        return Result.success(grouped);
    }
    
    @ApiOperation("根据分类获取技能标签")
    @GetMapping("/category/{category}")
    public Result<List<SkillTag>> getSkillsByCategory(@PathVariable String category) {
        List<SkillTag> skills = skillTagService.getSkillsByCategory(category);
        return Result.success(skills);
    }
    
    @ApiOperation("创建技能标签")
    @PostMapping
    public Result<String> createSkill(@RequestBody SkillTag skillTag) {
        boolean success = skillTagService.createSkill(skillTag);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }
    
    @ApiOperation("更新技能标签")
    @PutMapping("/{id}")
    public Result<String> updateSkill(@PathVariable Long id, @RequestBody SkillTag skillTag) {
        skillTag.setId(id);
        boolean success = skillTagService.updateSkill(skillTag);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @ApiOperation("删除技能标签")
    @DeleteMapping("/{id}")
    public Result<String> deleteSkill(@PathVariable Long id) {
        boolean success = skillTagService.deleteSkill(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
