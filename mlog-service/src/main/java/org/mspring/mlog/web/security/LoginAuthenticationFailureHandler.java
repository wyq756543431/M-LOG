/**
 * 
 */
package org.mspring.mlog.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

/**
 * @author Gao Youbo
 * @since 2013-1-14
 * @Description
 * @TODO
 */
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = Logger.getLogger(LoginAuthenticationFailureHandler.class);

    private String defaultFailureUrl;
    private boolean forwardToDestination = false;
    private boolean allowSessionCreation = true;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public LoginAuthenticationFailureHandler() {
    }

    public LoginAuthenticationFailureHandler(String defaultFailureUrl) {
        setDefaultFailureUrl(defaultFailureUrl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.web.authentication.AuthenticationFailureHandler
     * #onAuthenticationFailure(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse,
     * org.springframework.security.core.AuthenticationException)
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // TODO Auto-generated method stub
        String failureUrl = org.mspring.platform.utils.StringUtils.isNotBlank(request.getParameter("failureUrl")) ? request.getParameter("failureUrl") : defaultFailureUrl;
        if (failureUrl == null) {
            log.debug("No failure URL set, sending 401 Unauthorized error");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
        }
        else {
            saveException(request, exception);

            if (forwardToDestination) {
                log.debug("Forwarding to " + failureUrl);
                request.getRequestDispatcher(failureUrl).forward(request, response);
            }
            else {
                log.debug("Redirecting to " + failureUrl);
                redirectStrategy.sendRedirect(request, response, failureUrl);
            }
        }
    }

    /**
     * Caches the {@code AuthenticationException} for use in view rendering.
     * <p>
     * If {@code forwardToDestination} is set to true, request scope will be
     * used, otherwise it will attempt to store the exception in the session. If
     * there is no session and {@code allowSessionCreation} is {@code true} a
     * session will be created. Otherwise the exception will not be stored.
     */
    protected final void saveException(HttpServletRequest request, AuthenticationException exception) {
        if (forwardToDestination) {
            request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
        }
        else {
            HttpSession session = request.getSession(false);

            if (session != null || allowSessionCreation) {
                request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
            }
        }
    }

    /**
     * The URL which will be used as the failure destination.
     * 
     * @param defaultFailureUrl
     *            the failure URL, for example "/loginFailed.jsp".
     */
    public void setDefaultFailureUrl(String defaultFailureUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl), "'" + defaultFailureUrl + "' is not a valid redirect URL");
        this.defaultFailureUrl = defaultFailureUrl;
    }

    protected boolean isUseForward() {
        return forwardToDestination;
    }

    /**
     * If set to <tt>true</tt>, performs a forward to the failure destination
     * URL instead of a redirect. Defaults to <tt>false</tt>.
     */
    public void setUseForward(boolean forwardToDestination) {
        this.forwardToDestination = forwardToDestination;
    }

    /**
     * Allows overriding of the behaviour when redirecting to a target URL.
     */
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    protected boolean isAllowSessionCreation() {
        return allowSessionCreation;
    }

    public void setAllowSessionCreation(boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }

}
