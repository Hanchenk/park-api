package com.southwind.controller;

import com.southwind.util.DataGenerator;
import com.southwind.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据生成控制器
 */
@RestController
@RequestMapping("/data")
public class DataGeneratorController {

    @Autowired
    private DataGenerator dataGenerator;

    /**
     * 生成模拟数据
     * @param count 要生成的记录数量
     * @return 结果
     */
    @GetMapping("/generate")
    public Result generateData(@RequestParam(defaultValue = "1000") int count) {
        try {
            dataGenerator.generateAndSaveData(count);
            return Result.ok().put("msg", "成功生成 " + count + " 条模拟数据");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().put("msg", "生成数据失败: " + e.getMessage());
        }
    }
} 