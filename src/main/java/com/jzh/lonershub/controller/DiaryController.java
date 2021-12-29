package com.jzh.lonershub.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jzh.lonershub.bean.Diary;
import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 日记控制器，包括日记编写，上传及展示等功能
 * @author Jiang Zhihang
 * @date 2021/12/28 14:54
 */
@Controller
public class DiaryController {
    private DiaryService diaryService;

    @Autowired
    public void setDiaryService(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping(value = "/success/diary/write")
    public String write(@RequestParam String content, HttpSession session, HttpServletResponse response) throws IOException {
        if (!StringUtils.hasText(content)) {
            content = "无话可说...";
        }
        String creatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Diary diary = new Diary();
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        diary.setCreatorId(successLoner.getLonerId());
        diary.setCreateTime(creatTime);
        diary.setContent(content);
        if (!diaryService.save(diary)) {
            response.addCookie(new Cookie("errorMsg", "保存失败"));
            return "redirect:/success";
        }

        // 重新设置diaryPage对象
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creatorId", successLoner.getLonerId());
        // 按日期降序排序
        queryWrapper.orderByDesc("createTime");

        Page<Diary> diaryPage = diaryService.page(new Page<>(1, 6), queryWrapper);
        session.setAttribute("diaryPage", diaryPage);
        return "redirect:/success";
    }

    @PostMapping(value = "/success/diary/delete")
    public String delete(@RequestParam Integer id, HttpServletResponse response, HttpSession session) {
        if (!diaryService.removeById(id)) {
            response.addCookie(new Cookie("errorMsg", "删除失败"));
            return "redirect:/success";
        }

        Loner successLoner = (Loner) session.getAttribute("successLoner");
        // 重新设置diaryPage对象
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creatorId", successLoner.getLonerId());
        // 按日期降序排序
        queryWrapper.orderByDesc("createTime");

        Page<Diary> diaryPage = diaryService.page(new Page<>(1, 6), queryWrapper);
        session.setAttribute("diaryPage", diaryPage);
        return "redirect:/success";
    }
}


