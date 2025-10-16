package study.data_jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

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
}
