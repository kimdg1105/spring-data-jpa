package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    Member member1;
    Member member2;
    Member member3;

    @BeforeEach
    void beforeEach(){
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
    public void findByUsernameAndAgeGreaterThan(){


        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers.get(0).getUsername()).isEqualTo("AAA");
        assertThat(findMembers.get(0).getAge()).isEqualTo(20);

    }

    @Test
    public void namedQueryTest(){
        List<Member> members = memberJpaRepository.findByUsername("BBB");
        Member member = members.get(0);
        assertThat(member).isEqualTo(member3);
    }

    @Test
    public void namedQueryTest2(){
        List<Member> members = memberRepository.findByUsername("BBB");
        Member member = members.get(0);
        assertThat(member).isEqualTo(member3);
    }

    @Test
    public void namedQueryTest3(){
        List<Member> members = memberRepository.findByUsernameAndAge("AAA",20);
        Member member = members.get(0);
        assertThat(member).isEqualTo(member2);
    }
}