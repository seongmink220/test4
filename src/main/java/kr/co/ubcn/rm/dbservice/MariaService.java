package kr.co.ubcn.rm.dbservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ubcn.rm.mapper.mariaVanon.MariaVanonMapper;
import kr.co.ubcn.rm.mapper.mariaVmms.MariaVmmsMapper;
import kr.co.ubcn.rm.model.RmNoRegShopTimeVO;
import kr.co.ubcn.rm.model.TerAndCnt;
import kr.co.ubcn.rm.rmchk.RmChkProc;
import kr.co.ubcn.rm.util.DateUtils;
import kr.co.ubcn.rm.util.StringUtils;
import kr.co.ubcn.rm.util.StringXMLParse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("file:config/application.properties")
@Transactional
public class MariaService {
	
	@Autowired
	private MariaVanonMapper mariaVanonMapper;
	
	@Autowired
	private MariaVmmsMapper mariaVmmsMapper;
	
	private String apiURL = StringUtils.getConfigProp("kakao.api.url");
	
	private String rmToken = StringUtils.getConfigProp("RM.TOKEN");
	private String rmCompanySeq = StringUtils.getConfigProp("RM.COMPANYSEQ");
	private String rmMsgFrame = StringUtils.getConfigProp("RM.MSG.FRAME");
	private String rmMsg = StringUtils.getConfigProp("RM.MSG");
	
	private String rmTEL_MVP = StringUtils.getConfigProp("RM.TEL.MVP");
	private String rmTEL_TEAM = StringUtils.getConfigProp("RM.TEL.TEAM");
	private String rmTEL_TEAM2 = StringUtils.getConfigProp("RM.TEL.TEAM2");
	private String rmTEL_OTHER = StringUtils.getConfigProp("RM.TEL.OTHER");
	private String rmTEL_AS = StringUtils.getConfigProp("RM.TEL.AS");
	private String rmTEL_TERM = StringUtils.getConfigProp("RM.TEL.TERM");
	private String rmTEL_TERMQT = StringUtils.getConfigProp("RM.TEL.TERM.QT");	
	private String rmStateInfoTermTitle = StringUtils.getConfigProp("RM.STATEINFO.TERM.TITLE"); //상태정보 미수신시 메시지
	
	public void test() {
		
		System.out.println("[[LOG]]==[[]]");
	}

	
	/**
	 * 특정단말기 상태수신 확인
	 */
	public void HourTermStateCheck(String terminal,String endDate) {
		String strToday = rtnDate("d");
		int i = 0;
		
		String rmTerminalMsg = "";
		
		String[] rmTerminalMsgArr = StringUtils.getConfigProp("RM.STATEINFO.TERM.MSG").split(",");
		
		for(i=0; i<rmTerminalMsgArr.length; i++) {
			log.debug("메시지:"+rmTerminalMsgArr[i]);
			
			if(rmTerminalMsgArr[i].substring(0, 10).equals(terminal)) {
				rmTerminalMsg = rmTerminalMsgArr[i].substring(11);
				break;
			}//
		}//
		
		log.debug("메시지:"+rmTerminalMsg);
		
		String rsTerminalID = "";
		Integer rsCNT = null;
		
		String stMS = "0000";
		String endMS = "5959";
		
		String whHOUR = rtnDate("h");
		String stTIME = whHOUR+stMS;
		String endTIME = whHOUR+endMS;
		String rmStateInfoMsg = "";
		String stIND_DT = strToday+stTIME;
		String endIND_DT = endDate;
		StringBuilder sb = new StringBuilder();
		
		TerAndCnt terAndCnt;
		
		log.debug("체크  {}:{}:{}",terminal,stIND_DT,endIND_DT);
		
		try {
			
			terAndCnt = mariaVmmsMapper.HourTermStateCheck(terminal, stIND_DT, endIND_DT);
			
//			for(int j=0; j<terAndCnt.size(); j++) {
			if(terAndCnt != null) {
				rsTerminalID = terAndCnt.getTERMINAL_ID().trim();
				rsCNT = terAndCnt.getCNT();
				
				log.debug("체크 터미널ID {}:카운트{}",rsTerminalID,rsCNT);
			}
//			}
		   	   
			if(!terminal.equals(rsTerminalID)) {
				sb.append(String.format(rmTerminalMsg, terminal, rmStateInfoTermTitle, whHOUR));				   
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		rmStateInfoMsg=sb.toString();		
		if(!"".equals(rmStateInfoMsg)) {			
			String rmTel = rmTEL_TERM;			
			//카카오 메시지 보내기
			SendKakao("KSM" + rmStateInfoMsg,rmTel);

			log.info("상태정보 수신 없슴: [{}]시  [{}]",whHOUR, rmStateInfoMsg);
		}else {
			log.info("상태정보 수신 결과: [{}]시 상태정보 0건 수신단말기[{}] 없슴",whHOUR,terminal);	
		}
		
	}
	
	
	
	
	/**
	 * 시간대의 망취소: RM.TEL.TEAM, RM.TEL.OTHER
	 * 매시 30분,55분에 단위로 거래내역없슴 망취소 건수 모니터링, N회 이상이면 RM 발송 RM.TEL.TEAM, RM.TEL.OTHER
	 */
	public void MangCancelCNT() {
		String strToday = rtnDate("d");
		
		String rmMANGPropCnt = StringUtils.getConfigProp("RM.MANG");	
		String rmMangMsg = StringUtils.getConfigProp("RM.MANG.MSG");
		
		int rsMangCNT = 0;
		
		String stMS = "0000";
		String endMS = "5959";
		
		String whUNIQUENO = rtnDate("U");
		
		String stUNIQUENO = whUNIQUENO+stMS+"000000";
		String endUNIQUENO = whUNIQUENO+endMS+"999999";
		
		String whHOUR = rtnDate("h");
		String stTIME = whHOUR+stMS;
		String endTIME = whHOUR+endMS;
		
		try {
			rsMangCNT = mariaVanonMapper.MangCancelCNT(stUNIQUENO, endUNIQUENO, stTIME, endTIME, strToday);

			log.debug("쿼리결과:"+rsMangCNT);
			
		 }catch(Exception e) {
			e.printStackTrace();
//			log.error(StringUtils.getPrintStackTrace(ex));
		}

		
		if(rsMangCNT>=Integer.parseInt(rmMANGPropCnt) ) {
            String rmTel = 	rmTEL_TEAM+","+rmTEL_OTHER;			
			//카카오 메시지 보내기
			rmMangMsg = String.format(rmMangMsg,whHOUR,rsMangCNT);
			SendKakao("KSM" + rmMangMsg,rmTel);	
			
		}else {
			log.info("망취소 결과: [{}]시 망취소:[{}]건",whHOUR, rsMangCNT);	
		}
		
	}
	
	
	
	/**
	 * 시간대의 미응답 프로세스 : rmTEL_TEAM
	 * 매시 59분 승인정보 없는 서버 : RM.TEL.TEAM
	 */
	public void AppProcessCheck() {
		String strToday = rtnDate("d");
		
		String rmProcMsg = StringUtils.getConfigProp("RM.PROCESS.MSG");
		String rsPROCNAME=null;
		ArrayList<String> rsPROCNAME_Arr = null;
		
		String stMS = "0000";
		String endMS = "5959";
		
		String whUNIQUENO = rtnDate("U");
		
		String stUNIQUENO = whUNIQUENO+stMS+"000000";
		String endUNIQUENO = whUNIQUENO+endMS+"999999";
		
		String whHOUR = rtnDate("h");
		String stTIME = whHOUR+stMS;
		String endTIME = whHOUR+endMS;
		
		String stIND_DT = strToday+stTIME;
		String endIND_DT = strToday+endTIME;
		
		try {
			rsPROCNAME_Arr = mariaVanonMapper.AppProcessCheck(stUNIQUENO, endUNIQUENO, stIND_DT, endIND_DT, strToday);
			
//			for(int i=0; i<rsPROCNAME_Arr.size(); i++) {
//				System.out.println("rsPROCNAME_ArrrsPROCNAME_ArrrsPROCNAME_Arr" + rsPROCNAME_Arr.get(i));
//			}
			
			log.debug("쿼리결과:"+rsPROCNAME_Arr);
		}catch(Exception e) {
			e.printStackTrace();
//			log.error(StringUtils.getPrintStackTrace(ex));
		}
		
		
		if(rsPROCNAME_Arr.size()!=0) {
		    String rmTel = rmTEL_TEAM;
			//카카오 메시지 보내기
			rmProcMsg = String.format(rmProcMsg,whHOUR,rsPROCNAME_Arr);
			SendKakao("KSM" + rmProcMsg, rmTel);
			
		}else {
			log.info("프로세스체크 결과: [{}]시 무응답 프로세스:없슴",whHOUR);	
		}
		
		
	}
	
	
	
	/**
	 * 시간대별 미등록 가맹점  N개 이상 : RM.TEL.TEAM , RM.TEL.OTHER
	 * 매시 58분 승인응답이 미등록 가맹점(8331)이 승인이 N개 이상인 가맹점 
	 */
	public void NoShopApp() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String strToday = rtnDate("d");
		
		String rmNoShopPropCnt = StringUtils.getConfigProp("RM.NOREGSHOP");	
		String rmNoShopMsg = StringUtils.getConfigProp("RM.NOREGSHOP.MSG");
		
		String rsNoShopAppNM="";
		int rsNoShopAppCNT = 0;
		String rsNoShop_Arr="";
		
		
		String stMS = "0000";
		String endMS = "5959";
		
		String whUNIQUENO = rtnDate("U");
		
		String stUNIQUENO = whUNIQUENO+stMS+"000000";
		String endUNIQUENO = whUNIQUENO+endMS+"999999";
		
		String whHOUR = rtnDate("h");
		String stTIME = whHOUR+stMS;
		String endTIME = whHOUR+endMS;
		
		String stIND_DT = strToday+stTIME;
		String endIND_DT = strToday+endTIME;
		
		ArrayList<RmNoRegShopTimeVO> rmNoRegShopTimeArr;
		
		try {
			
			rmNoRegShopTimeArr = mariaVanonMapper.NoShopApp(stUNIQUENO, endUNIQUENO, stIND_DT, endIND_DT, rmNoShopPropCnt, strToday);
			
			for(int i=0; i<rmNoRegShopTimeArr.size(); i++) {
				rsNoShopAppNM = rmNoRegShopTimeArr.get(i).getMERCHANTNAME().trim();
				rsNoShopAppCNT = rmNoRegShopTimeArr.get(i).getCount();	   
				rsNoShop_Arr = rsNoShop_Arr+rsNoShopAppNM+":"+String.valueOf(rsNoShopAppCNT)+"건,";
			}
			
			log.debug("쿼리결과:"+rsNoShop_Arr);
			
		}catch(Exception e){
            e.printStackTrace();
		}

		
		if(!"".equals(rsNoShop_Arr)) {
			String rmTel = 	rmTEL_TEAM+","+rmTEL_OTHER;
			//카카오 메시지 보내기
			rmNoShopMsg = String.format(rmNoShopMsg,whHOUR,rmNoShopPropCnt,rsNoShop_Arr);
			SendKakao("KSM" + rmNoShopMsg,rmTel);		

		}else {
			log.info("미등록 가맹점 결과: [{}]시 [{}]건이상 미등록가맹점 없슴",whHOUR, rmNoShopPropCnt);	
		}

	}
	
	
	/**
	 * 시간대별 상태정보 N회 이상 수신 단말기 :  rmTEL_TEAM+","+rmTEL_AS;
	 * 매시59분 상태정보 수신이 N개 이상인 단말기 -  RM.TEL.TEAM,RM.TEL.AS 
	 */
	public void StateUpload() {
		
		String strToday = rtnDate("d");
		
		String rmPropCnt = StringUtils.getConfigProp("RM.STATEINFO");	
		String rmStateInfoMsg = StringUtils.getConfigProp("RM.STATEIINFO.MSG");
		
		String rsTermonalID="";
		int rsCNT = 0;
		String rsStateInfo_Arr="";
		
		String stMS = "0000";
		String endMS = "5959";
		
		String whHOUR = rtnDate("h");
		String stTIME = whHOUR+stMS;
		String endTIME = whHOUR+endMS;
		
		String stIND_DT = strToday+stTIME;
		String endIND_DT = strToday+endTIME;
		
		
		List<TerAndCnt> stateUploadVO;
		
		try {
			
			stateUploadVO = mariaVmmsMapper.StateUpload(stIND_DT, endIND_DT, rmPropCnt, strToday);
			
			for(int i=0; i<stateUploadVO.size(); i++) {
				rsTermonalID = stateUploadVO.get(i).getTERMINAL_ID();
				rsCNT = stateUploadVO.get(i).getCNT();
				rsStateInfo_Arr = rsStateInfo_Arr+rsTermonalID+":"+String.valueOf(rsCNT)+"건,";
			}
			
			log.debug("쿼리결과:"+rsStateInfo_Arr);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		if(!"".equals(rsStateInfo_Arr)) {			
			//String rmTel = rmTEL_TEAM+","+rmTEL_AS;
			String rmTel = rmTEL_AS;
			
			//카카오 메시지 보내기
			rmStateInfoMsg = String.format(rmStateInfoMsg,whHOUR,rsStateInfo_Arr);
			SendKakao("KSM" + rmStateInfoMsg,rmTel);	
			
		}else {
			log.info("상태정보 수신 결과: [{}]시 [{}]건이상 단말기 없슴",whHOUR, rmPropCnt);	
		}
		
	}
	
	
	
	//QT 단말기 상태 정보 수신 확인 
	//매시 특정 시간에 해당단말기의 상태 정보 수신 확인
	public void StateQtInfo() {

		String strToday = rtnDate("d");
		
		String rmTermTimeMin = StringUtils.getConfigProp("RM.STATE.QT.TERM.TIME");
		String rmTerminalID = StringUtils.getConfigProp("RM.STATE.QT.TERM");
		String rmStateInfoMsg = StringUtils.getConfigProp("RM.STATE.QT.TERM.MSG");
		String rmStateInfoMsg2 = StringUtils.getConfigProp("RM.STATE.QT.TERM.MSG2");
		
		String[] rmTerminalID_Arr = rmTerminalID.split(",");
		String[] rmStateInfoMsg_Arr = rmStateInfoMsg.split(",");
				
		String rsCreaTime="";
		
		String stMS = "0000";
		String endMS = rmTermTimeMin+"59";
		
		String whHOUR = rtnDate("h");
		String stTIME = whHOUR+stMS;
		String endTIME = whHOUR+endMS;
		
		String stIND_DT = strToday+stTIME;
		String endIND_DT = strToday+endTIME;
		StringBuffer sb = new StringBuffer();
		
		try {
			
			for(int i=0; i<rmTerminalID_Arr.length;i++) {
				
				rsCreaTime =  mariaVmmsMapper.StateQtInfo(rmTerminalID_Arr[i], stIND_DT, endIND_DT);
								
//				rsCreaTime = StateUploadVO.get(i).getTERMINAL_ID().trim();
				
				   if("".equals(rsCreaTime)) {
					   
					   if(rmTerminalID_Arr[i].equals(rmStateInfoMsg_Arr[i].substring(0, 10))) {
						   
						   
						   sb.append(rmStateInfoMsg_Arr[i]);
					   
						   if(i< (rmTerminalID_Arr.length-1)) {
							   sb.append(",");
						   }//
					   }//
				   }//
				   rsCreaTime="";
			   }//
			   
			   log.debug("쿼리결과 미수신 TID:"+sb.toString());
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		String sendQTMsg = sb.toString();
		
		if(!"".equals(sendQTMsg)) {			
			//String rmTel = rmTEL_TEAM+","+rmTEL_AS;
			String rmTel = rmTEL_TERMQT;
			
			sendQTMsg = sendQTMsg + rmStateInfoMsg2; 
			
			//카카오 메시지 보내기
			rmStateInfoMsg = sendQTMsg.replace("[hour]", whHOUR);
			
			log.info(rmStateInfoMsg);
			
			SendKakao("KSM" + rmStateInfoMsg,rmTel);
		}else {
			log.info("QT상태정보 수신 결과: [{}]시  단말기 없슴",whHOUR);	
		}
	}
	
	
	
	/**
	 * 현재 시간에서의 측정 예상 시간리턴
	 * @return
	 */
	public HashMap<String,String> rtnTermStateNext() {
		HashMap<String,String> map = new HashMap<String,String>();
		
		String strToday = DateUtils.dateAdd("",-1).substring(0,8);		
		String nowTimeHour="";
		nowTimeHour = DateUtils.timeAdd("", 0).substring(0, 10);
		
		String stMS = "0000";
		String endMS = "5959";
		String rmTerminal = StringUtils.getConfigProp("RM.STATEINFO.TERM");	
		String[] rmTerminalArr = rmTerminal.split(",");
		String rmSTATEINFO_NextMinHour = StringUtils.getConfigProp("RM.STATEINFO.TERM.TIME.MIN"); //3
		
		String stIND_DT = strToday+"00"+stMS; //20240313000000
		
		String rsCreateTime = "";
		String lastDT = "";  //마지막 수신 시간
		String nextPreDictDT = "";  //다음 예상 수신시간
		
		log.info("상태정보체크  수신시간{}",stIND_DT);
		
		try {
			for(int i=0; i<rmTerminalArr.length; i++) {
				
				String createTime = mariaVmmsMapper.rtnTermStateNext(rmTerminalArr[i], stIND_DT);	//가장 최근 상태 정보 수신
				
				if(createTime != null) {
					rsCreateTime = createTime.trim();  //마지막 수신시간
				}else {
					continue;
				}


				lastDT = DateUtils.rtnDateString(nowTimeHour+rsCreateTime.substring(10, 14));	//20240314092602
				nextPreDictDT = DateUtils.timeAdd2(lastDT,0,Integer.parseInt(rmSTATEINFO_NextMinHour));  //마지막 층정시간 +3분
				
				map.put(rmTerminalArr[i],nextPreDictDT);
				   
				log.info("상태정보체크  수신시간{}/{}",rmTerminalArr[i], lastDT);
				   
				log.info("상태정보체크  예상시간{}/{}",rmTerminalArr[i], nextPreDictDT);	
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return map;
		 
	}
	
	
	/**
	 * 일일원장 테이블 생성확인
	 */
	public void DayTBLCheck() {

		String strToday = rtnDate("d");
		
		String whHOUR = rtnDate("h");
		String nextDay = DateUtils.dateAdd("", 1).substring(0, 8);	//다음날		
		String rmMsg = StringUtils.getConfigProp("RM.TBLCHK.MSG");
		
		Integer rsTlfCNT=null;
		Integer rsICtlfCNT=null;
		
//		log.debug("쿼리문1:"+tlfTbSql);
//		log.debug("쿼리문2:"+icTlfTbSql);
		
		try {
			
			rsTlfCNT = mariaVanonMapper.DayTBLCheck1(nextDay);
			log.debug("쿼리결과1:"+rsTlfCNT);
			
			rsICtlfCNT = mariaVanonMapper.DayTBLCheck2(nextDay);
			log.debug("쿼리결과2:"+rsICtlfCNT); 
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		if(rsTlfCNT!=null && rsICtlfCNT!=null) {
			log.info(nextDay+"일일원장 생성완료!");
		}else {
			//카톡 루틴
			String rmTel = rmTEL_TEAM2;
			
			//카카오 메시지 보내기
			rmMsg = String.format(rmMsg,whHOUR,nextDay);			
			log.info(nextDay+" 일일원장 생성안됨!:"+rmMsg);
			
			SendKakao("KSM" + rmMsg,rmTel);
		}	
	}
	
	
	
	/**
	 * 해당 전화번호에 카카오 메시지 보내기
	 * @param sndMsg
	 * @param whHOUR
	 * @param resCNT
	 */
	private void SendKakao(String sndMsg,String rmTel) {
		
		String kakaoMsg = "";
		kakaoMsg = String.format(rmMsgFrame,sndMsg,DateUtils.nowDate("h","t"));
		
		String send_Msg = String.format(rmMsg, kakaoMsg);
		
		try {
		  String rtnSend=null;
		  
		  if(rmTel!=null) {		  
			  String[] arrTel = rmTel.split(","); 
			  String sendMsg = "";
			  for(int i=0;i<arrTel.length;i++) {
			      if(!"".equals(arrTel[i]) && arrTel[i].length()>=13) {  
			    	  sendMsg = send_Msg.replace("[TEL]", arrTel[i].subSequence(0, 13));
			    	  log.info(sendMsg);
				  
			    	  rtnSend = getConnectionKakao(apiURL, rmToken,rmCompanySeq,sendMsg);			  
			    	  log.info("요청결과:"+rtnSend);
			      }else {
			    	  log.error("송신불가:"+arrTel[i]);
			      }
			  }//
		  }else {
			  log.error("송신불가: 전화번호 없슴");
		  }//
		  
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @param apiUrl
	 * @param arrayObj
	 * @return
	 * @throws Exception
	 */
	private String getConnectionKakao(String apiUrl, String authToken,String companySeq,String arrayObj) throws Exception{
		
		URL url 			  = new URL(apiUrl); 	// 요청을 보낸 URL
		String sendData 	  = arrayObj;
		HttpURLConnection con = null;
		StringBuffer buf 	  = new StringBuffer();
		String returnStr 	  = "";
		
		try {
			con = (HttpURLConnection)url.openConnection();
			
			con.setConnectTimeout(10000);		//서버통신 timeout 설정. 페이코 권장 30초
			con.setReadTimeout(10000);			//스트림읽기 timeout 설정. 페이코 권장 30초
			
			con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			con.setRequestProperty("token", authToken);
			con.setRequestProperty("company", companySeq);
			
		    con.setDoOutput(true);
		    con.setRequestMethod("POST");
		    con.connect();
		    
		    // 송신할 데이터 전송.
		    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
		    
		    dos.write(sendData.getBytes("UTF-8"));
		    dos.flush();
		    dos.close();
		    
		    int resCode = con.getResponseCode();
		    
		    if (resCode == HttpURLConnection.HTTP_OK) {
		    
		    	BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			   	
				int c;
			    
			    while ((c = br.read()) != -1) {
			    	buf.append((char)c);
			    }
			    
			    returnStr = buf.toString();
			    br.close();
			    
			    log.info("응답:"+ returnStr);
			    
		    } else {
		    	returnStr = "{ \"code\" : 9999, \"message\" : \"Send Error\" }";
		    	log.error("응답:"+ String.valueOf(resCode));
		    }
		    
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    con.disconnect();
		}		
		return returnStr;
	}
	
	
	/**
	 * code에 따른 날짜, 시간 리턴
	 * @param code
	 * @return
	 */
	public String rtnDate(String code) {
		String nowDate = DateUtils.rtnDateMille();
		String rtnDate=null;
		//20210303134748029
		switch(code) {
		case "d":
			rtnDate = nowDate.substring(0, 8);
			break;
		case "t":
			rtnDate = nowDate.substring(8,14);
			break;
		case "h":
			rtnDate = nowDate.substring(8,10);
			break;
		case "U":  //UNIQUENO 용
			rtnDate = nowDate.substring(2,10);
			break;	
		default:
			rtnDate = nowDate.substring(0, 8);			
		}
		return rtnDate;
	}
	
	
}
