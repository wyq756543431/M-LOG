/**
 * 
 */
package org.mspring.mlog.web.module.blog;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mspring.mlog.entity.Comment;
import org.mspring.mlog.entity.Post;
import org.mspring.mlog.entity.security.User;
import org.mspring.mlog.service.CommentService;
import org.mspring.mlog.service.OptionService;
import org.mspring.mlog.service.PostService;
import org.mspring.mlog.utils.PostUrlUtils;
import org.mspring.mlog.web.freemarker.widget.stereotype.Widget;
import org.mspring.mlog.web.security.SecurityUtils;
import org.mspring.platform.utils.RequestUtils;
import org.mspring.platform.utils.StringUtils;
import org.mspring.platform.utils.ValidatorUtils;
import org.mspring.platform.web.render.JSONRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Gao Youbo
 * @since 2013-3-1
 * @description 文章评论
 * @TODO
 */
@Widget
@RequestMapping("/comment")
public class Blog_CommentWidget extends AbstractBlogWidget {
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private OptionService optionService;

    @RequestMapping("/get_json")
    public void get_json(@RequestParam(required = false) Long post, HttpServletRequest request, HttpServletResponse response) {
        if (post == null) {
            return;
        }
        List<Comment> comments = commentService.findCommentsByPost(post);
        JSONRender renderer = new JSONRender(comments, true);
        renderer.render(response);
    }

    /**
     * 发表评论
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/post")
    public String postComment(HttpServletRequest request, HttpServletResponse response, Model model) {
        String postId = request.getParameter("postId");
        if (!Post.CommentStatus.OPEN.equals(postService.getPostById(new Long(postId)).getCommentStatus())) {
            return prompt(model, "文章评论已关闭，无法发表评论");
        }
        User currentUser = SecurityUtils.getCurrentUser(request);
        if (currentUser == null) {
            return prompt(model, "请先登录");
        }

        String content = request.getParameter("content");
        String ip = RequestUtils.getRemoteIP(request);
        String agent = RequestUtils.getUserAgent(request);

        String reply_comment_str = request.getParameter("reply_comment");
        Long reply_comment = null;
        if (StringUtils.isNotBlank(reply_comment_str) && ValidatorUtils.isNumber(reply_comment_str.trim())) {
            reply_comment = new Long(request.getParameter("reply_comment").trim());
        }

        /**
         * 验证评论内容不能为空
         */
        if (StringUtils.isBlank(content)) {
            return prompt(model, "评论发表失败，评论内容不能为空");
        }

        /**
         * 验证评论内容长度
         */
        if (content.length() > 4000) {
            return prompt(model, "评论发表失败，评论内容不能超过4000字符");
        }

        Comment comment = new Comment();
        comment.setAuthor(currentUser);
        comment.setContent(content);
        comment.setPostIp(ip);
        comment.setAgent(agent);
        comment.setCreateTime(new Date());
        comment.setPost(new Post(new Long(postId.trim())));
        if (reply_comment != null) {
            comment.setParent(new Comment(reply_comment));
        }

        // 判断评论审核功能是否开启
        String is_comment_audit = optionService.getOption("comment_audit");
        if ("true".equals(is_comment_audit)) { // 如果开启评论审核
            comment.setStatus(Comment.Status.WAIT_FOR_APPROVE);
        } else {
            comment.setStatus(Comment.Status.APPROVED);
        }
        comment = commentService.createComment(comment);
        String postUrl = PostUrlUtils.getPostUrl(comment.getPost());
        return String.format("redirect:%s", postUrl + "#comment-" + comment.getId());
    }
}
