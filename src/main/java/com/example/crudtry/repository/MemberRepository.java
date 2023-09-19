package com.example.crudtry.repository;

import com.example.crudtry.model.Member;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Function;

@Repository
public interface MemberRepository extends JpaRepository<Member, Object>{

    List<Member> findAll();

    Iterable<Member> findByName(String name);


    void deleteByName(String name);
}

