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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${web.site:http://buckteethss.com}")
    private String webSite;

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
        emailVo.setSubject("【BEIJING SIMPLE HOME DECO CO.,LTD】Account Successfully Registered");
        emailVo.setTos(Lists.newArrayList(email));
        Optional<EmailConfig> emailConfigOptional = emailRepository.findById(1L);
        EmailConfig emailConfig = emailConfigOptional.orElseGet(EmailConfig::new);
        emailVo.setContent(buidlCreateUserEmailContent(userName, accountName, password, emailConfig.getFromUser()));
        send(emailVo, emailConfig);
    }

    @Override
    public void sendResetPwdEmail(String userName, String accountName, String password, String email, EmailConfig emailConfig) {
        EmailVo emailVo = new EmailVo();
        emailVo.setSubject("【BEIJING SIMPLE HOME DECO CO.,LTD】Your Password Has Been Reset Successfully");
        emailVo.setTos(Lists.newArrayList(email));
        emailVo.setContent(buidlResetPwdEmailContent(userName, accountName, password, emailConfig.getFromUser()));
        send(emailVo, emailConfig);
    }

    private String buidlResetPwdEmailContent(String userName, String accountName, String password, String email) {
        return "<p style=\"text-align:left;\">\n" +
                "\t<br />\n" +
                "\t<b>\nDear " + userName + "," +
                "\t<br />\n" +
                "\t<br />Your account and password on [BEIJING SIMPLE HOME DECO CO.,LTD] has been successfully reset.\n" +
                "\t<br />\n" +
                "\t<br />\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>Account Username：" + accountName + "</b>\n" +
                "\t</font>\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "</p>\n" +
                "<p>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>New Password：" + password + "</b>\n" +
                "\t</font>\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "</p>\n" +
                "<p>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>WebSite：" + webSite + "</b>\n" +
                "\t</font>\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "</p>\n" +
                "<p style=\"text-align:left;\">\n" +
                "(This is an automated email—please do not reply directly.)<br /><br />\n" +
                "For security reasons, please:<br />\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tKeep this information confidential and do not share it with others.\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tChange your password after the first login for added protection.\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "If you have any questions or did not request this reset, please contact:<br />\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tEmail: "+ email +"\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tCustomer Service Hotline: 86-10-59528198\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "Best regards,<br />\n" +
                "[BEIJING SIMPLE HOME DECO CO.,LTD]<br />";
    }

    private String buidlCreateUserEmailContent(String userName, String accountName, String password, String email) {
        return "<p style=\"text-align:left;\">\n" +
                "\t<br />\n" +
                "\t<b>\nDear " + userName + "," +
                "\t<br />\n" +
                "\t<br />Your account and password on [BEIJING SIMPLE HOME DECO CO.,LTD] has been successfully registered.\n" +
                "\t<br />\n" +
                "\t<br />\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>Account Username：" + accountName + "</b>\n" +
                "\t</font>\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "</p>\n" +
                "<p>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>Password：" + password + "</b>\n" +
                "\t</font>\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "</p>\n" +
                "<p>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t<font color=\"#c24f4a\">\n" +
                "\t\t<b>WebSite：" + webSite + "</b>\n" +
                "\t</font>\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "</p>\n" +
                "<p style=\"text-align:left;\">\n" +
                "(This is an automated email—please do not reply directly.)<br /><br />\n" +
                "For security reasons, please:<br />\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tKeep this information confidential and do not share it with others.\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tChange your password after the first login for added protection.\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "If you have any questions or did not request this reset, please contact:<br />\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tEmail: "+ email +"\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "<ul>\n" +
                "\t<li>\n" +
                "\t\tCustomer Service Hotline: 86-10-59528198\n" +
                "\t</li>\n" +
                "</ul>\n" +
                "Best regards,<br />\n" +
                "[BEIJING SIMPLE HOME DECO CO.,LTD]<br />";
    }
}
