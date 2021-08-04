package study.datajpa.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @PostConstruct
    void init(){
        Team teamA = new Team("TeamA");
        teamRepository.save(teamA);

        Member testerA = Member.builder().team(teamA).age(25).username("TesterA").build();
        memberRepository.save(testerA);
    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member findMember = memberRepository.findById(id).get();
        return findMember.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }



}
