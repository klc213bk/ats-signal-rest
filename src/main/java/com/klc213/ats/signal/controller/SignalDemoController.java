package com.klc213.ats.signal.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.klc213.ats.common.AccountInfo;
import com.klc213.ats.signal.service.DemoService;

@RestController
@RequestMapping("/demo")
public class SignalDemoController {
	static final Logger LOGGER = LoggerFactory.getLogger(SignalDemoController.class);

	@Autowired
	private DemoService demoService;
	
	@Autowired
	private ObjectMapper mapper;

	@GetMapping(path="/ok")
	@ResponseBody
	public ResponseEntity<String> ok() {
		LOGGER.info(">>>>controller ok is called");

		LOGGER.info(">>>>controller ok finished ");
		
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	
	
	@PostMapping(path="/start", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> start() {
		LOGGER.info(">>>>controller start is called");

		ObjectNode objectNode = mapper.createObjectNode();
	
		try {
			demoService.start();
			
			objectNode.put("returnCode", "0000");
		} catch (Exception e) {
			String errMsg = ExceptionUtils.getMessage(e);
			String stackTrace = ExceptionUtils.getStackTrace(e);
			objectNode.put("returnCode", "-9999");
			objectNode.put("errMsg", errMsg);
			objectNode.put("returnCode", stackTrace);
			LOGGER.error(">>> errMsg={}, stacktrace={}",errMsg,stackTrace);
		}
		
		LOGGER.info(">>>>controller startDemo finished ");
		
		return new ResponseEntity<Object>(objectNode, HttpStatus.OK);
	}
	@PostMapping(path="/stop", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> stop() {
		LOGGER.info(">>>>controller stop is called");

		ObjectNode objectNode = mapper.createObjectNode();
	
		try {
			demoService.stopRun();
			
			objectNode.put("returnCode", "0000");
		} catch (Exception e) {
			String errMsg = ExceptionUtils.getMessage(e);
			String stackTrace = ExceptionUtils.getStackTrace(e);
			objectNode.put("returnCode", "-9999");
			objectNode.put("errMsg", errMsg);
			objectNode.put("returnCode", stackTrace);
			LOGGER.error(">>> errMsg={}, stacktrace={}",errMsg,stackTrace);
		}
		
		LOGGER.info(">>>>controller stopDemo finished ");
		
		return new ResponseEntity<Object>(objectNode, HttpStatus.OK);
	}
}
