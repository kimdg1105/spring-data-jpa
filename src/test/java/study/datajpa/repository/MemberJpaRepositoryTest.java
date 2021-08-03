package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

<<<<<<< HEAD
import javax.persistence.EntityManager;
=======
>>>>>>> main
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

<<<<<<< HEAD
    @Autowired
    EntityManager em;

=======
>>>>>>> main
    Member member1;
    Member member2;
    Member member3;


    @BeforeEach
    void beforeEach(TestInfo info) {
<<<<<<< HEAD
        if (info.getDisplayName().equals("noBeforeEach")) {
=======
        if (info.getDisplayName().equals("paging")) {
>>>>>>> main
            return; // skip @BeforeEach in mySpecialTestName test
        }

        member1 = Member.builder().username("AAA").age(10).build();
        member2 = Member.builder().username("AAA").age(20).build();
        member3 = Member.builder().username("BBB").age(20).build();
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);

    }

    @Test
    public void basicCRUD() {

        //단건 조회 검증
        Member findMember1 =
                memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 =
                memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(3);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(3);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(1);
    }

    @Test
    public void namedQueryTest() {
        List<Member> members = memberJpaRepository.findByUsername("BBB");
        Member member = members.get(0);
        assertThat(member).isEqualTo(member3);
    }

    @Test
<<<<<<< HEAD
    @DisplayName("noBeforeEach")
=======
    @DisplayName("paging")
>>>>>>> main
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
        List<Member> members = memberJpaRepository.findAll();
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
<<<<<<< HEAD
    @DisplayName("noBeforeEach")
=======
>>>>>>> main
    public void bulkUpdateJpaTest() {
        //given
        for (int i = 0; i < 10; i++) {
            member1 = Member.builder().username("AAA").age(i + 10).build();
            member2 = Member.builder().username("AAA").age(i + 20).build();
            memberJpaRepository.save(member1);
            memberJpaRepository.save(member2);
        }
        List<Member> members = memberJpaRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member);
        }


        //when
        int updateCount = memberJpaRepository.bulkAgePlus(15);
<<<<<<< HEAD
        em.flush();
        em.clear();

=======
>>>>>>> main

        //then
        System.out.println("------Before/After--------");
        List<Member> updateMembers = memberJpaRepository.findAll();
<<<<<<< HEAD
        for (Member member : members) {
            System.out.println("member = " + updateMembers);
=======
        for (Member member : updateMembers) {
            System.out.println("member = " + member);
>>>>>>> main
        }
        System.out.println("updateCount = " + updateCount);
    }

}
