package com.example.crudtry.service;

import com.example.crudtry.model.Member;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.example.crudtry.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> findAllMembers() { //顯示所有聯絡人

        var it = memberRepository.findAll();
        var members = new ArrayList<Member>();
        it.forEach(members::add);
        return members;
    }

    public List<Member> findMembersByName(String name) {  //根據姓名查找聯絡人資訊

        return (List<Member>) memberRepository.findByName(name);
    }

    public Member addMember(Member member) {  //新增聯絡人
        Member save = memberRepository.save(member);
        return  save;
    }


    public void updateMember(Long id, Member updatedMember) {
        Optional<Member> existingMemberOptional = memberRepository.findById(id);  //用ID去找資料，並把資料存在容器中

        if (existingMemberOptional.isEmpty()) {
            //判斷容器內是否為空，若為空則拋出錯誤訊息
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member with ID " + id + " not found");
        }

        // 使用 existingMemberOptional.get() 來獲取 Member 對象
        Member existingMember = existingMemberOptional.get();

        // 使用 updatedMember 中的資料來更新現有成員 (existingMember) 的名稱和電話號碼
        existingMember.setName(updatedMember.getName());
        existingMember.setPhone(updatedMember.getPhone());
        existingMember.setGender(updatedMember.getGender());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setAddress(updatedMember.getAddress());

        // 把更新後的對象存回去資料庫
        memberRepository.save(existingMember);
    }


    public void deleteMemberByName(@PathVariable String name) {
        List<Member> membersToDelete = (List<Member>) memberRepository.findByName(name);
        //透過.findByName去找出name相同的資料，並用list暫存
        if (membersToDelete.isEmpty()) {
            // 如果list為空代表沒有找到，就拋出錯誤訊息
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No members with name " + name + " found");
        }

        // 有找到資料就把該資料刪除
        memberRepository.deleteAll(membersToDelete);
    }

    public void deleteMemberById(Long id) {
        Optional<Member> memberToDelete = memberRepository.findById(id);

        if (memberToDelete.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member with ID " + id + " not found");
        }

        memberRepository.deleteById(id);
    }

    public Member findMemberById(Long id) {
        Optional<Member> member = memberRepository.findById(id);

        if (member.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member with ID " + id + " not found");
        }

        return member.get();
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

}


