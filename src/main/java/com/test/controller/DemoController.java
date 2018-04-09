package com.test.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.zhuozhengsoft.pageoffice.*;

/**
 * @author Administrator
 *
 */
@RestController
public class DemoController {
	
	@Value("${posyspath}") 
	private String poSysPath;
	

	@RequestMapping("/hello")
	public String test() {
		System.out.println("hello run");
		return "Hello";
	}
	@RequestMapping("/fileList")
	public List<String> getFileList(){
		List<String> list=new ArrayList<>();
		for(File file:new File("C:\\Users\\david\\Desktop\\test").listFiles()){
			list.add(file.getAbsolutePath());
		}
		return  list;
	}
	
	@RequestMapping(value={"","/","/index"}, method=RequestMethod.GET)
	public ModelAndView showIndex(){
		ModelAndView mv = new ModelAndView("Index");
		return mv;
	}

	/**
	 * 打开pageOffice，需要一个文件路径作为参数，从而打开这个路径对应的文件
	 * @param request
	 * @param filePath
	 * @return
	 */
	@RequestMapping(value="/openPageOffice", method=RequestMethod.GET)
	public ModelAndView showWord(HttpServletRequest request,String filePath){
		ModelAndView mv = new ModelAndView("pageOffice");//此处的模板文件是pageOffice.html
		String userName="张三";
		PageOfficeCtrl poCtrl=new PageOfficeCtrl(request);
		poCtrl.setServerPage("/poserver.zz");//设置服务页面
		poCtrl.addCustomToolButton("保存并关闭","Save",1);//添加自定义保存关闭按钮
		try {
			poCtrl.setSaveFilePage("/savePageOffice?filePath="+ URLEncoder.encode(filePath,"utf-8"));//设置处理文件保存的请求方法，并将文件路径进行编码后作为参数传递
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		File file=new File(filePath);
		if(!file.exists()||!file.isFile()){
			mv.addObject("pageoffice",file.getName()+" 不存在");
			return  mv;
		}
		String fileName=file.getName();
		String suffix=fileName.substring(fileName.lastIndexOf(".")+1);
		mv.addObject("fileName",fileName);
		//打开word
		if(filePath.endsWith(".doc")||filePath.endsWith(".docx")){
			poCtrl.webOpen(filePath,OpenModeType.docNormalEdit,userName);
		}else if(filePath.endsWith(".xls")||filePath.endsWith(".xlsx")){
			poCtrl.webOpen(filePath,OpenModeType.xlsNormalEdit,userName);
		}else if(filePath.endsWith(".ppt")||filePath.endsWith(".pptx")){
			poCtrl.webOpen(filePath,OpenModeType.pptNormalEdit,userName);
		}else{
			mv.addObject("pageoffice","暂不支持"+suffix+"文件格式的编辑");
			return  mv;
		}
		mv.addObject("pageoffice",poCtrl.getHtmlCode("PageOfficeCtrl1"));
		return mv;
	}

	/**
	 * 保存pageOffice的请求，需要传递一个文件路径
	 * @param request
	 * @param response
	 * @param filePath
	 */
	@RequestMapping("/savePageOffice")
	public void saveFile(HttpServletRequest request, HttpServletResponse response,String filePath){
		System.out.println(filePath);
		FileSaver fs = new FileSaver(request, response);
		fs.saveToFile(filePath);
		fs.close();
	}
	
	
	/**
	 * 添加PageOffice的服务器端授权程序Servlet（必须）
	 * @return
	 */
	@Bean
    public ServletRegistrationBean servletRegistrationBean() {
		com.zhuozhengsoft.pageoffice.poserver.Server poserver = new com.zhuozhengsoft.pageoffice.poserver.Server();

		poserver.setSysPath(ClassLoader.getSystemResource("").getPath());//设置PageOffice注册成功后,license.lic文件存放的目录
		ServletRegistrationBean srb = new ServletRegistrationBean(poserver);
		srb.addUrlMappings("/poserver.zz");
		srb.addUrlMappings("/posetup.exe");
		srb.addUrlMappings("/pageoffice.js");
		srb.addUrlMappings("/jquery.min.js");
		srb.addUrlMappings("/pobstyle.css");
		srb.addUrlMappings("/sealsetup.exe");
        return srb;// 
    }
	
	/**
	 * 添加印章管理程序Servlet（可选）
	 * @return
	 */
//	@Bean
//    public ServletRegistrationBean servletRegistrationBean2() {
//		com.zhuozhengsoft.pageoffice.poserver.AdminSeal adminSeal = new com.zhuozhengsoft.pageoffice.poserver.AdminSeal();
//		adminSeal.setAdminPassword(poPassWord);//设置印章管理员admin的登录密码
//		adminSeal.setSysPath(poSysPath);//设置印章数据库文件poseal.db存放的目录
//		ServletRegistrationBean srb = new ServletRegistrationBean(adminSeal);
//		srb.addUrlMappings("/adminseal.zz");
//		srb.addUrlMappings("/sealimage.zz");
//		srb.addUrlMappings("/loginseal.zz");
//        return srb;//
//    }
}
