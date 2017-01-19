/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tk.mybatis.springboot.controller;

import IceInternal.Ex;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResultUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;
import tk.mybatis.springboot.UserId;
import tk.mybatis.springboot.ampq.data.BaseReceiveMessage;
import tk.mybatis.springboot.ampq.MessageEntity;
import tk.mybatis.springboot.domain.City;
import tk.mybatis.springboot.exception.BusinessException;
import tk.mybatis.springboot.interceptors.SessionInterceptors;
import tk.mybatis.springboot.service.CityService;
import tk.mybatis.springboot.util.ServerUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author liuzh
 * @since 2015-12-19 11:10
 */
@Controller
@RequestMapping("/cities")
public class CityController implements EnvironmentAware,ApplicationContextAware,MessageSourceAware{
    Environment environment;
    ApplicationContext applicationContext;

    private MessageSource messageSource;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CityService cityService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.getValidators();
    }

    @RequestMapping
    @ResponseBody
    public PageInfo<City> getAll(City city) {
        List<City> countryList = cityService.getAll(city);
        return new PageInfo<City>(countryList);
    }

    @RequestMapping("/msg")
    public Object testMessage(String m) throws Exception{
        BaseReceiveMessage baseReceiveMessage = new BaseReceiveMessage(new BaseReceiveMessage.Msg(m));

        rabbitTemplate.convertAndSend("host","create",baseReceiveMessage);
        return baseReceiveMessage;
    }

    @RequestMapping("/async")
    @ResponseBody
    public Object testAsyncRequest() throws Exception{
        System.out.println(Thread.currentThread().getId());
        Thread.sleep(1000);

        return Thread.currentThread().getId();
    }

    @RequestMapping("/dest")
    @ResponseBody
    public Object toRedirect(String flash){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(ServerUtil.getHttpServletRequest());
        return inputFlashMap.get("flash");
    }

    @RequestMapping("/redirect")
    public String testFlashAttr(RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("abc","Hello World!");
        redirectAttributes.addFlashAttribute("flash","Flash Value!");
        return "redirect:/cities/dest";
    }

    @RequestMapping("/some")
    @ResponseBody
    public Object someTest(@Validated City p, HttpServletRequest request) throws Exception{
        throw new BusinessException("123");
        //return new City("城市名","state");
        //return WebUtils.getRealPath(request.getServletContext(),"/my");
    }

    @RequestMapping(value = "/asyncTask",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Callable<String> webAsyncTest(){

        return new Callable<String>() {
            public String call() throws Exception {
                ResponseEntity entity = restTemplate.getForEntity("http://www.baidu.com",String.class);
                String body = (String) entity.getBody();
                return "异步请求处理完成" + new String(body.getBytes(),"UTF-8");
            }
        };
    }

    @RequestMapping("/reqAdvice")
    @ResponseBody
    public Object testRequestBodyAdvice(@RequestBody @UserId City city){
        return city;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseBody
    @RequestMapping("/server")
    @ResponseStatus(HttpStatus.CONFLICT)
    public Object testServer(){
        return new City("城市名","state");
    }
}
