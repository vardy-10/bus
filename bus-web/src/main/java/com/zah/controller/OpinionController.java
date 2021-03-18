package com.zah.controller;

import com.zah.service.PublicService;
import com.zah.service.impl.OpinionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("manage")
public class OpinionController {

	@Autowired
	OpinionServiceImpl opinionServiceImpl;
	@Autowired
	PublicService publicService;

	@RequestMapping("opinionShow")
	public String opinionShow(Model model) {
		return opinionServiceImpl.opinionShow(model);
	}

	@RequestMapping("showOpinion")
	@ResponseBody
	public String showOpinion(int limit, int page, String submit_time) {
		return opinionServiceImpl.showOpinion(limit, page, submit_time);

	}

	@RequestMapping("deleteOpinion")
	@ResponseBody
	public String deleteOpinion(String passenger_id) {
		return opinionServiceImpl.deleteOpinion(passenger_id);
	}
}
