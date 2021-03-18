package com.zah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("manage")
public class PublishOperatePlanController {

	@RequestMapping("publishOperatePlanShow")
	public String publishOperatePlanShow(Model model) {
		return null;
	}


}
