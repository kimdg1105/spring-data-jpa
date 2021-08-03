package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    TeamRepository teamRepository;

    Member member1;
    Member member2;
    Member member3;

    @BeforeEach
    void beforeEach(TestInfo info) {
        if (info.getDisplayName().equals("paging")) {
            return; // skip @BeforeEach in mySpecialTestName test
        }

        member1 = Member.builder().username("AAA").age(10).build();
        member2 = Member.builder().username("AAA").age(20).build();
        member3 = Member.builder().username("BBB").age(20).build();
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

    }


    @Test
    public void basicCRUD() {

        //단건 조회 검증
        Member findMember1 =
                memberRepository.findById(member1.getId()).get();
        Member findMember2 =
                memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(3);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(3);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(1);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {


        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers.get(0).getUsername()).isEqualTo("AAA");
        assertThat(findMembers.get(0).getAge()).isEqualTo(20);

    }

    @Test
    public void namedQueryTest() {
        List<Member> members = memberJpaRepository.findByUsername("BBB");
        Member member = members.get(0);
        assertThat(member).isEqualTo(member3);
    }

    @Test
    public void namedQueryTest2() {
        List<Member> members = memberRepository.findByUsername("BBB");
        Member member = members.get(0);
        assertThat(member).isEqualTo(member3);
    }

    @Test
    public void namedQueryTest3() {
        List<Member> members = memberRepository.findByUsernameAndAge("AAA", 20);
        Member member = members.get(0);
        assertThat(member).isEqualTo(member2);
    }

    @Test
    public void findUsernameList() {
        List<String> members = memberRepository.findUsernameList();
        for (String member : members) {
            System.out.println("member = " + member);
        }
        assertThat(members.size()).isEqualTo(3);
    }

    @Test
    public void findMemberDtosTest() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        member1.changeTeam(teamA);
        member2.changeTeam(teamA);
        member3.changeTeam(teamB);

        List<MemberDto> members = memberRepository.findMemberDtos();
        for (MemberDto member : members) {
            System.out.println("member.toString() = " + member.toString());
        }
        assertThat(members.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("paging")
    public void paging() {
        //given
        for (int i = 0; i < 10; i++) {
            member1 = Member.builder().username("AAA").age(10).build();
            member2 = Member.builder().username("AAA").age(20).build();
            memberJpaRepository.save(member1);
            memberJpaRepository.save(member2);
        }
        int age = 10;
        int offset = 0;
        int limit = 5;


        //when
        List<Member> members = memberRepository.findAll();
        List<Member> memberPage = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);


        //then
        for (Member member : members) {
            System.out.println("member = " + member);
        }

        System.out.println("----------------");

        for (Member member : memberPage) {
            System.out.println("member = " + member);
        }
        System.out.println("totalCount = " + totalCount);
    }

    @Test
    @DisplayName("paging")
    public void pagingBySpringDataJpa() {
        //given
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);


        for (int i = 0; i < 10; i++) {
            member1 = Member.builder().username("AAA"+ i).age(10).team(teamA).build();
            member2 = Member.builder().username("AAA"+ i).age(20).team(teamB).build();
            memberJpaRepository.save(member1);
            memberJpaRepository.save(member2);
        }

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC,"username"));

        //when
        List<Member> members = memberRepository.findAll();
        Page<Member> memberPage = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> memberPageDto = memberPage.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));


        //then
        List<MemberDto> content = memberPageDto.getContent();
        for (MemberDto member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalCount = " + memberPage.getTotalElements());

        assertThat(content.size()).isEqualTo(5);
        assertThat(memberPage.getTotalElements()).isEqualTo(20);
        assertThat(memberPage.getNumber()).isEqualTo(0);
        assertThat(memberPage.isFirst()).isTrue();
    }
}