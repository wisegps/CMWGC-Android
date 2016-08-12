package com.wgc.cmwgc.Bean;

import java.io.Serializable;
import java.util.List;

public class Control_Model implements Serializable {
	String apply_id,user_name,numbers,destination,apply_reason,allot_username,status;
	String obj_name,obj_id,driver_name,driver_id,creat_time,start_time,end_time,out_time,back_time,notreason;
	String user_id,cost_status;
	
	public Control_Model(String apply_id, String user_name, String numbers,
			String destination, String apply_reason, String allot_username,
			String status, String obj_name, String obj_id, String driver_name,
			String driver_id, String creat_time, String start_time,
			String end_time, String out_time, String back_time,
			String notreason, String user_id, String cost_status) {
		super();
		this.apply_id = apply_id;
		this.user_name = user_name;
		this.numbers = numbers;
		this.destination = destination;
		this.apply_reason = apply_reason;
		this.allot_username = allot_username;
		this.status = status;
		this.obj_name = obj_name;
		this.obj_id = obj_id;
		this.driver_name = driver_name;
		this.driver_id = driver_id;
		this.creat_time = creat_time;
		this.start_time = start_time;
		this.end_time = end_time;
		this.out_time = out_time;
		this.back_time = back_time;
		this.notreason = notreason;
		this.user_id = user_id;
		this.cost_status = cost_status;
	}
	public String getDriver_id() {
		return driver_id;
	}
	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}
	public String getObj_id() {
		return obj_id;
	}
	public void setObj_id(String obj_id) {
		this.obj_id = obj_id;
	}
	public String getNotreason() {
		return notreason;
	}
	public void setNotreason(String notreason) {
		this.notreason = notreason;
	}
	
	public String getOut_time() {
		return out_time;
	}
	public void setOut_time(String out_time) {
		this.out_time = out_time;
	}
	public String getBack_time() {
		return back_time;
	}
	public void setBack_time(String back_time) {
		this.back_time = back_time;
	}
	public String getApply_id() {
		return apply_id;
	}
	public void setApply_id(String apply_id) {
		this.apply_id = apply_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getNumbers() {
		return numbers;
	}
	public void setNumbers(String numbers) {
		this.numbers = numbers;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getApply_reason() {
		return apply_reason;
	}
	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}
	public String getAllot_username() {
		return allot_username;
	}
	public void setAllot_username(String allot_username) {
		this.allot_username = allot_username;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getObj_name() {
		return obj_name;
	}
	public void setObj_name(String obj_name) {
		this.obj_name = obj_name;
	}
	public String getDriver_name() {
		return driver_name;
	}
	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}
	public String getCreat_time() {
		return creat_time;
	}
	public void setCreat_time(String creat_time) {
		this.creat_time = creat_time;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getCost_status() {
		return cost_status;
	}
	public void setCost_status(String cost_status) {
		this.cost_status = cost_status;
	}
	
}
