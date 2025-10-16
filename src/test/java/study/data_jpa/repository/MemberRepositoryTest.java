package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;


    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();


        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);


    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        //삭제가 됐는지 다시 한번 불러오기.
        count = memberRepository.count();

        Assertions.assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }


    @Test
    public void testQuery(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        Assertions.assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameList(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();

        for(String user : result){
            System.out.println(user);
        }
    }

    @Test
    public void findMemberDto(){
        Team team1 = new Team("teamA");
        teamRepository.save(team1);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team1);
        memberRepository.save(member1);

        List<MemberDto> result = memberRepository.findMemberDto();

        for(MemberDto user : result){
            System.out.println("MemberDto = " +user);
        }
    }

    @Test
    public void findByNames(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByName(Arrays.asList("AAA", "BBB"));

        for(Member user : result){
            System.out.println(user);
        }
    }

    @Test
    public void returnType(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        memberRepository.findListByUsername("AAA");
//        memberRepository.findMemberByUsername("AAA");
//        memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age,pageRequest);
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); //엔티티를 쉽게 map을 활용해서 Dto로 변환하는 법


        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

//        for (Member member : content){
//            System.out.println("가져온 멤버 수 = " + member);
//        }
//        System.out.println("토탈 값 = " + totalElements);

        Assertions.assertThat(content.size()).isEqualTo(3);     //현재 페이지의 컬럼갯수
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);    //총 컬럼 갯수
        Assertions.assertThat(page.getNumber()).isEqualTo(0);           // 현재 페이지 번호
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);       //전체 페이지 갯수
        Assertions.assertThat(page.isFirst()).isTrue();                         //첫번째 페이지인가
        Assertions.assertThat(page.hasNext()).isTrue();                         //다음 페이지가 있나

    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));


        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        // 벌크 연산은 영속성 컨텍스트를 거치지 않고 바로 DB에 반영되므로,
        // 같은 트랜잭션 내에서 이미 조회된 엔티티와 DB 상태가 불일치할 수 있음.
        // em.flush();
        // em.clear();  ← 영속성 컨텍스트를 초기화하여 불일치 방지

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " +  member5);


        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

}