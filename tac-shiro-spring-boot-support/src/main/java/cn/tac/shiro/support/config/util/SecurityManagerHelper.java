package cn.tac.shiro.support.config.util;

import cn.tac.shiro.support.config.factory.StatelessDefaultSubjectFactory;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.AbstractValidatingSessionManager;

/**
 * @author tac
 * @since 1.1
 */
public abstract class SecurityManagerHelper {
    public static void disableSession(SecurityManager securityManager) {
        disableSession(securityManager, new StatelessDefaultSubjectFactory());
    }

    public static void disableSession(SecurityManager securityManager, SubjectFactory subjectFactory) {
        if (securityManager instanceof DefaultSecurityManager) {
            DefaultSecurityManager tmp = (DefaultSecurityManager) securityManager;
            tmp.setSubjectFactory(subjectFactory);
            if (tmp.getSessionManager() instanceof AbstractValidatingSessionManager) {
                AbstractValidatingSessionManager sessionManager = (AbstractValidatingSessionManager) tmp.getSessionManager();
                sessionManager.setSessionValidationSchedulerEnabled(false);
            }
            if (tmp.getSubjectDAO() instanceof DefaultSubjectDAO) {
                DefaultSubjectDAO subjectDAO = (DefaultSubjectDAO) tmp.getSubjectDAO();
                if (subjectDAO.getSessionStorageEvaluator() instanceof DefaultSessionStorageEvaluator) {
                    DefaultSessionStorageEvaluator evaluator = (DefaultSessionStorageEvaluator) subjectDAO.getSessionStorageEvaluator();
                    evaluator.setSessionStorageEnabled(false);
                }
            }
        }
    }
}
