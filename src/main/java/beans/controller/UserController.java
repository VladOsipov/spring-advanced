package beans.controller;

import beans.models.User;
import beans.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public ModelAndView getRegistrationPage(){
		ModelAndView modelAndView = new ModelAndView("registration");
		modelAndView.addObject("title", "Registration Page");
		modelAndView.addObject("message", "Register new User");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView registerUser(@RequestParam String username, @RequestParam String email){
		User user = new User();
		user.setName(username);
		user.setEmail(email);
		userService.register(user);
		ModelAndView modelAndView = new ModelAndView("userRegistered");
		modelAndView.addObject("username", user.getName());
		return modelAndView;
	}

	@RequestMapping(value = "/uploadUser/", method = RequestMethod.GET)
	public ModelAndView uploadUser(){
		ModelAndView modelAndView = new ModelAndView("upload");
		return modelAndView;
	}

	@RequestMapping(value = "/uploadUser/", method = RequestMethod.POST)
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		User user = parseFile(file);
		userService.register(user);

		return "redirect:/uploadUser/";
	}

	private User parseFile(MultipartFile file){
		//TODO: implement parsing
		User user = new User();
		user.setEmail("email@gmail.com");
		user.setName("Ivan");
		user.setId(2l);
		user.setBirthday(LocalDate.now());
		return user;
	}

}
