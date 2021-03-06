package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.specification.MemberSpec;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager em;

    Member member1;
    Member member2;
    Member member3;

    @BeforeEach
    void beforeEach(TestInfo info) {
        if (info.getDisplayName().equals("noBeforeEach")) {
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
    public void pagingBySpringDataJpa() {
        //given
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);


        for (int i = 0; i < 10; i++) {
            member1 = Member.builder().username("AAA" + i).age(10).team(teamA).build();
            member2 = Member.builder().username("AAA" + i).age(20).team(teamB).build();
            memberRepository.save(member1);
            memberRepository.save(member2);
        }

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "username"));

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

    @Test
    @DisplayName("noBeforeEach")
    public void bulkUpdateJpaTest() {
        //given
        for (int i = 0; i < 10; i++) {
            member1 = Member.builder().username("AAA").age(i + 10).build();
            member2 = Member.builder().username("AAA").age(i + 20).build();
            memberRepository.save(member1);
            memberRepository.save(member2);
        }
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member);
        }


        //when
        int updateCount = memberRepository.bulkAgePlus(15);
//        em.flush();
//        em.clear();


        //then
        System.out.println("------Before/After--------");
        List<Member> updateMembers = memberRepository.findAll();
        for (Member member : updateMembers) {
            System.out.println("member = " + member);
        }
        System.out.println("updateCount = " + updateCount);
    }

    @Test
    public void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        member1.setTeam(teamA);
        member2.setTeam(teamA);
        member3.setTeam(teamB);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // Lazy Loading (N+1 문제 발생)
            System.out.println("member.getTeam() = " + member.getTeam().getName());

        }


        //then


    }

    @Test
    public void findMemberFetchJoinTest() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        member1.setTeam(teamA);
        member2.setTeam(teamA);
        member3.setTeam(teamB);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // Lazy Loading (N+1 문제 발생)
            System.out.println("member.getTeam() = " + member.getTeam().getName());

        }
    }

    @Test
    public void queryHint(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("modifyName");

        em.flush();
    }

//    @Test
//    public void queryLock(){
//        Member member1 = new Member("member1", 10);
//        memberRepository.save(member1);
//        em.flush();
//        em.clear();
//
//        //when
//        List<Member> findMember = memberRepository.findMemberByUsernameWithLock("member1");
//        em.clear();
//    }

    @Test
    public void forCustom(){
        List<Member> memberCustom = memberRepository.findMemberCustom();
        for (Member member : memberCustom) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void specBasic(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m1", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);


        //then
    }

    @Test
    public void queryByExample(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m1", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> ex = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(ex);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");


        //then

    }

    @Test
    public void projections(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m1", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //when
        List<Member> m1 = memberRepository.findByUsername("m1");
        System.out.println("--------------");
        List<UsernameOnly> m11 = memberRepository.findProjectionsByUsername("m1");
        System.out.println("--------------");
        // 오픈 프로젝션 (엔티티 전체를 우선 다 가져온다.)
        memberRepository.findByUsernameAndAge("m1",0);

    }

    @Test
    public void nativeQuery(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);


        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m1", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //when

        Page<MemberProjection> result = memberRepository.findByNativProjection(PageRequest.of(0, 5));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection);
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
            System.out.println("-------------------");
        }


    }
}