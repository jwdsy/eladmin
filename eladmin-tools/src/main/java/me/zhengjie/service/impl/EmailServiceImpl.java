/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.service.impl;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.EmailConfig;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.repository.EmailRepository;
import me.zhengjie.service.EmailService;
import me.zhengjie.utils.EncryptUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Zheng Jie
 * @date 2018-12-26
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "email")
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;

    @Override
    @CachePut(key = "'config'")
    @Transactional(rollbackFor = Exception.class)
    public EmailConfig config(EmailConfig emailConfig, EmailConfig old) throws Exception {
        emailConfig.setId(1L);
        if(!emailConfig.getPass().equals(old.getPass())){
            // 对称加密
            emailConfig.setPass(EncryptUtils.desEncrypt(emailConfig.getPass()));
        }
        return emailRepository.save(emailConfig);
    }

    @Override
    @Cacheable(key = "'config'")
    public EmailConfig find() {
        Optional<EmailConfig> emailConfig = emailRepository.findById(1L);
        return emailConfig.orElseGet(EmailConfig::new);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(EmailVo emailVo, EmailConfig emailConfig){
        if(emailConfig.getId() == null){
            throw new BadRequestException("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        // 设置用户
        String user = emailConfig.getFromUser().split("@")[0];
        account.setUser(user);
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(EncryptUtils.desDecrypt(emailConfig.getPass()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfig.getUser()+"<"+emailConfig.getFromUser()+">");
        // ssl方式发送
        account.setSslEnable(true);
        // 使用STARTTLS安全连接
        account.setStarttlsEnable(true);
        // 解决jdk8之后默认禁用部分tls协议，导致邮件发送失败的问题
        account.setSslProtocols("TLSv1 TLSv1.1 TLSv1.2");
        String content = emailVo.getContent();
        // 发送
        try {
            int size = emailVo.getTos().size();
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[size]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    //关闭session
                    .setUseGlobalSession(false)
                    .send();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void sendCreateUserEmail(String userName, String accountName, String password, String email) {
        EmailVo emailVo = new EmailVo();
        emailVo.setSubject("【XX科技】账号注册成功");
        emailVo.setTos(Lists.newArrayList(email));
        emailVo.setContent(buidlCreateUserEmailContent(userName, accountName, password));
        Optional<EmailConfig> emailConfigOptional = emailRepository.findById(1L);
        EmailConfig emailConfig = emailConfigOptional.orElseGet(EmailConfig::new);
        send(emailVo, emailConfig);
    }

    @Override
    public void sendResetPwdEmail(String userName, String accountName, String password, String email, EmailConfig emailConfig) {
        EmailVo emailVo = new EmailVo();
        emailVo.setSubject("【XX科技】账号密码重置成功");
        emailVo.setTos(Lists.newArrayList(email));
        emailVo.setContent(buidlResetPwdEmailContent(userName, accountName, password));
        send(emailVo, emailConfig);
    }

    private String buidlResetPwdEmailContent(String userName, String accountName, String password) {
        return "<p style=\"text-align:left;\">\n" +
                "\t<br />\n" +
                "\t<b>\n" +
                "\t\t<font color=\"#c24f4a\">" + userName + "</font>\n" +
                "\t</b>，您好：\n" +
                "\t<br />\n" +
                "\t<br />您在xxxx网站( xxxx.com )的账号密码重置成功。\n" +
                "\t<br />\n" +
                "\t<br />\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>登录账号：" + accountName + "</b>\n" +
                "\t</font>\n" +
                "</p>\n" +
                "<p>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>重置后密码：" + password + "</b>\n" +
                "\t</font>\n" +
                "</p>\n" +
                "<p style=\"text-align:left;\">\n" +
                "\t<br />此邮件为系统自动发出，请勿直接回复。\n" +
                "\t<br />\n" +
                "\t<br />请您切勿将此信息泄露给他人，如有其他任何疑问，请发邮件至：service@xxxx.com 或致电XXX科技客服：400-619-xxxx\n" +
                "\t<br />\n" +
                "\t<br />XXX有限公司\n" +
                "\t<br />\n" +
                "</p>";
    }

    private static String buidlCreateUserEmailContent(String userName, String accountName, String password) {
        return "<p style=\"text-align:left;\">\n" +
                "\t<br />\n" +
                "\t<b>\n" +
                "\t\t<font color=\"#c24f4a\">" + userName + "</font>\n" +
                "\t</b>，您好：\n" +
                "\t<br />\n" +
                "\t<br />您在xxxx网站( xxxx.com )的账户已经注册成功。\n" +
                "\t<br />\n" +
                "\t<br />\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>登录账号：" + accountName + "</b>\n" +
                "\t</font>\n" +
                "</p>\n" +
                "<p>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>登录密码：" + password + "</b>\n" +
                "\t</font>\n" +
                "</p>\n" +
                "<p style=\"text-align:left;\">\n" +
                "\t<br />此邮件为系统自动发出，请勿直接回复。\n" +
                "\t<br />\n" +
                "\t<br />请您切勿将此信息泄露给他人，如有其他任何疑问，请发邮件至：service@xxxx.com 或致电XXX科技客服：400-619-xxxx\n" +
                "\t<br />\n" +
                "\t<br />XXX有限公司\n" +
                "\t<br />\n" +
                "</p>";
    }
}
