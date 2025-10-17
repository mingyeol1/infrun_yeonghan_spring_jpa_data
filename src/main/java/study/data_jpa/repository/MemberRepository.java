package study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    List<Member> findByUsername(String username);

    @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByName(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); //컬렉션반환
    Member findMemberByUsername(String username); //단건 반환
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

//    @Query(value = "select m from Member m left join m.team t",
//                countQuery = "select count(m) from Member m")   //쿼리가 복잡해지면(성능 저하) 카운트 쿼리로 분리해주는것도 좋음.
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)   // 이걸 넣으면 알아서 영속성 컨텍스트를 clear를 해줌
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member>  findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) //jpql 없이 엔티티그래프를 사용하면 쉽게 가능함.
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")    //jpql에다가 엔티티그래프를 섞어서 사용도 가능함.
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username ") String username);

    @QueryHints(value =  @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
