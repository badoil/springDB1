package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 파라미터 연동, 커넥션 풀을 고려한 종료
 */
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // @Transactional 어노테이션으로 트랜잭션 aop 사용해서 프록시로 넘겨버림
        bizLogic(fromId, toId, money);

    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromMember.getMemberId(), fromMember.getMoney()- money);
        validation(toMember );
        memberRepository.update( toMember.getMemberId(), toMember.getMoney()+ money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생"); }
    }
}
