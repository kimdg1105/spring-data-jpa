package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.QueryHint;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository, JpaSpecificationExecutor {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * Member.findByUsername에서 네임드쿼리를 찾아 있으면 실행한다.
     * 없다면, 메소드이름으로 쿼리를 생성한다.
     *
     * @param username
     * @return
     */
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findByUsernameAndAge(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m inner join m.team t")
    List<MemberDto> findMemberDtos();

    @Query(value = "select m from Member m",
            countQuery = "select count (m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // 쿼리 이후 영속성 컨텍스트를 자동으로 클리어한다.
    @Query("update Member m set m.age = :age where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    List<Member> findMemberByUsername(@Param("username") String username);

    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
        //하이버네이트 내부적으로 스냅샷을 생성하지 않게 한다.
    Member findReadOnlyByUsername(String username);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    List<Member> findMemberByUsernameWithLock(String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    List<Member> findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName" +
            " from member m left join team t",
            countQuery = "select count (*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativProjection(Pageable pageable);


}
