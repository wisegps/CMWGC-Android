package com.wgc.cmwgc.Until;

public class Config {
	public static String  username=null;
	//用于上传经纬度
	public static String  service_obj_id=null;
	//用于上传经纬度
	public static String  service_apply_id=null;
    //记录主界面定位地址
	public static String location_address="test address";
	//添加设备入库时用到gps_time
	public static String gps_time="";
	//登录时用户的ID
	public static String user_id=null;
    //用户登录的 auth_code
	public static String auth_code=null;
	//用户登录时获取的数据
	public static String parent_id=null;
	//用户登录时获取的数据(角色)
	public static String role=null;
	//登录时获取的用户名字
	public static String user_name=null;
	//登录时填写的账号
	public static String account=null;
	//登录时填写的密码（加密后的）
	public static String hash=null;
	//登录时返回的数据（是否允许登录）
	public static String is_login=null;
	//登录时返回的数据（是否允许开车）
	public static String is_driver=null;
	/**
	 * 配置文件名称
	 */
	public static final String SP_NAME = "Vehicle_Net";
	public static final String IS_CREATE = "is_create";
	
	/*wistorm token*/
	public static String ACCESS_TOKEN = "";
	public static String USER_ID = "";
	
	public static final String USER_NAME = "wgcapk";
	public static final String USER_PASS = "123456";
	
	public static final String UPDATA_APK_URL = "http://o.bibibaba.cn/upgrade";
	
	
	public static String PackString = "com.wgc.cmwgc";
	public static String user_info = null;
	public static String con_serial = "";
}
