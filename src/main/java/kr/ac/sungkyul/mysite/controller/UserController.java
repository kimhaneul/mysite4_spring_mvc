package kr.ac.sungkyul.mysite.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kr.ac.sungkyul.mysite.service.UserService;
import kr.ac.sungkyul.mysite.vo.UserVo;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;
	
	UserVo authUser;

	@RequestMapping("/joinform")
	public String joinForm() {
		return "user/joinform";
	}

	@RequestMapping("/joinsuccess")
	public String joinSuccess() {
		return "user/joinsuccess";
	}

	@RequestMapping("/join")
	public String join(@ModelAttribute UserVo vo) {
		System.out.println("UserController:join()");
		userService.join(vo);
		return "redirect:/user/joinsuccess";
	}
	
	@RequestMapping("{userid}/loginform")
	public String loginForm(@PathVariable String userid) {

		System.out.println("loginform  " + userid);
		return "user/loginform";
	}

	@RequestMapping("/modifyform")
	public String modifyform() {
		return "user/modifyform";
	}

	@RequestMapping("/update")
	public String update(@ModelAttribute UserVo vo) {
		vo.setNo(authUser.getNo());
		System.out.println("userController " + vo);
		userService.update(vo);
		return "/main";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpSession session,
			@RequestParam(value = "email", required = false, defaultValue = "") String email,
			@RequestParam(value = "password", required = false, defaultValue = "") String password) {

		authUser = userService.login(email, password);
		if (authUser == null) {
			return "redirect:/user/loginform";
		}

		// 인증 성공
		session.setAttribute("authUser", authUser);

		return "redirect:/main";
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("authUser");
		session.invalidate();

		return "redirect:/main";
	}
}
