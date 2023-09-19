package com.example.crudtry.controller;

import com.example.crudtry.model.Member;
import com.example.crudtry.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/member")
    public String getAllMembers(Model model) {
        List<Member> allMembers = memberService.findAllMembers();
        model.addAttribute("newMember", allMembers); // 添加到模型中
        return "/member"; // 返回视图名称
    }

    @PostMapping("/action")
    public String handleAction(@ModelAttribute("newMember") Member newMember, @RequestParam("action") String action, Model model) {
        if ("add".equals(action)) {
            // 執行新增操作
            memberService.addMember(newMember);
            return "redirect:/member";
        } else if ("search".equals(action)) {
            // 執行搜尋操作
            Iterable<Member> searchResults = memberService.findMembersByName(newMember.getName());
            model.addAttribute("newMember", searchResults);
        }   return "/member";

    }

    @PostMapping("/edit")
    public String editMember(@ModelAttribute Member updatedMember) {

        Long id = updatedMember.getId();

        memberService.updateMember(id, updatedMember);

        return "redirect:/member";
    }
    @GetMapping("/edit/{id}")
    public String editMember(@PathVariable Long id, Model model) {
        Optional<Member> optionalMember = memberService.findById(id);
        if (optionalMember.isPresent()) {
            model.addAttribute("member", optionalMember.get());
            return "edit";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/update")
    public String updateMember(@ModelAttribute Member member) {
        memberService.updateMember(member.getId(), member);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMemberById(id);
        return "redirect:/member";
    }
    //輸出csv檔案
    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportToCsv() throws IOException {
        // 獲取通訊錄數據
        List<Member> members = memberService.findAllMembers();
        // 生成CSV數據
        byte[] csvData = generateCsvData(members);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "contacts.csv");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(csvData);
    }
    private byte[] generateCsvData(List<Member> members) {
        // 在這裡生成CSV數據，可以使用Java庫（如OpenCSV）來協助
        StringBuilder csv = new StringBuilder();
        csv.append("學生姓名,聯絡人類別,聯絡人電話,聯絡人信箱,學生住址\n");
        for (Member member : members) {
            csv.append(member.getName()).append(",")
                    .append(member.getGender()).append(",")
                    .append(member.getPhone()).append(",")
                    .append(member.getEmail()).append(",")
                    .append(member.getAddress()).append("\n");
        }
        return csv.toString().getBytes();
    }
    //輸出excel檔案
    @GetMapping("/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=contacts.xlsx");

        List<Member> memberList = memberService.findAllMembers();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Contacts");

        Row headerRow = sheet.createRow(0);
        String[] columns = {"學生姓名", "聯絡人類別", "聯絡人電話", "聯絡人信箱", "學生住址"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Member member : memberList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(member.getName());
            row.createCell(1).setCellValue(member.getGender());
            row.createCell(2).setCellValue(member.getPhone());
            row.createCell(3).setCellValue(member.getEmail());
            row.createCell(4).setCellValue(member.getAddress());
        }

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
    }

}
