package com.wse.crm;

import com.alibaba.fastjson.JSON;
import com.wse.crm.base.ResultInfo;
import com.wse.crm.exceptions.NoLoginException;
import com.wse.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

/**
 * 全局异常统一处理
 *
 * @author wse
 * @version 1.0
 * @date 2020/8/19 0019 17:29
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {


    /**
     * ⽅法返回值类型
     * 视图
     * JSON
     * 如何判断⽅法的返回类型：
     * 如果⽅法级别配置了@ResponseBody 注解，表示⽅法返回的是JSON；
     * 反之，返回的是视图⻚⾯
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handle
     * @param e
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handle, Exception e) {
        /**
         * 判断异常类型
         * 如果是未登录异常，则先执⾏相关的拦截操作
         */
        if (e instanceof NoLoginException) {
            // 如果捕获的是未登录异常，则重定向到登录⻚⾯
            ModelAndView mv = new ModelAndView("redirect:/index");
            return mv;
        }


        //默认异常处理
        ModelAndView modelAndView = new ModelAndView();
        //设置视图名
        modelAndView.setViewName("error");//error.ftl
        modelAndView.addObject("msg", "系统异常，请重试！");
        modelAndView.addObject("code", 400);

        // 判断HandlerMethod
        if (handle instanceof HandlerMethod) {
            // 类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handle;
            // 获取⽅法上的ResponseBody 注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            // 判断ResponseBody 注解是否存在(如果不存在，表示返回的是视图;如果存在，表示返回的是JSON)
            if (null == responseBody) {
                if (e instanceof ParamsException) {
                    ParamsException p = (ParamsException) e;
                    modelAndView.addObject("code", p.getCode());
                    modelAndView.addObject("msg", p.getMsg());

                }
                return modelAndView;
            }else {
                //返回json
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("网络异常，请重试！");

                //如果捕获的是⾃定义异常
                if (e instanceof ParamsException) {
                    ParamsException p = (ParamsException) e;
                    resultInfo.setMsg(p.getMsg());
                    resultInfo.setCode(p.getCode());
                }


                // 设置响应类型和编码格式（响应JSON格式）
                httpServletResponse.setContentType("application/json;charset=utf-8");

                PrintWriter out = null;
                try {
                    // 得到输出流
                    out = httpServletResponse.getWriter();
                    //将对象转换成JSON格式，通过输出流输出
                    String json = JSON.toJSONString(resultInfo);
                    //输出
                    out.write(json);
                    //刷新
                    out.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }

                }
            }

        }
        return modelAndView;
    }
}
