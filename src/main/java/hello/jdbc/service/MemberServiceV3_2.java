package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 파라미터 연동, 커넥션 풀을 고려한 종료
 */
@Slf4j
public class MemberServiceV3_2 {

//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult(status -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

        // 위처럼 템플릿을 사용하면 아래와 같은 커밋, 롤백 직접 해줄 필요 없음
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//
//        try{
//            // 비즈니스 로직
//            bizLogic(fromId, toId, money);
//            transactionManager.commit(status); // 성공시 커밋
//
//        } catch (Exception e) {
//            transactionManager.rollback(status); // 실패시 롤백
//            throw new IllegalStateException(e);
//        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromMember.getMemberId(), fromMember.getMoney()- money);
        validation(toMember );
        memberRepository.update( toMember.getMemberId(), toMember.getMoney()+ money);
    }

    private void release(Connection con) {
        if(con != null) {
            try{
                con.setAutoCommit(true);
                con.close();
            }catch (Exception e) {
                log.info("Error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생"); }
    }
}
