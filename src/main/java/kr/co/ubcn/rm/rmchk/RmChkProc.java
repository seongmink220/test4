package kr.co.ubcn.rm.rmchk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import kr.co.ubcn.rm.dbservice.MariaService;
import kr.co.ubcn.rm.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PropertySource("file:config/application.properties")
@Service
public class RmChkProc {
	
	@Autowired
	private MariaService mariaService;
			
	private String gRmMangTime1 = StringUtils.getConfigProp("RM.MANG.TIME1");
	private String gRmMangTime2 = StringUtils.getConfigProp("RM.MANG.TIME2");
	private String gRmProcTime = StringUtils.getConfigProp("RM.PROCESS.TIME");
	private String gRmNoRegShopTime = StringUtils.getConfigProp("RM.NOREGSHOP.TIME");
	private String gRmNStateInfoTime = StringUtils.getConfigProp("RM.STATEINFO.TIME");	
	private String gRMTblChkTime = StringUtils.getConfigProp("RM.TBLCHK.TIME");
	
	private String gRmNStateInfoTermTime = StringUtils.getConfigProp("RM.STATEINFO.TERM.TIME");
	
	private String gRmStateInfoTermQTTime = StringUtils.getConfigProp("RM.STATE.QT.TERM.TIME");
	
	//RM 측정 제외시간
	private String gRM_STATEINFO_TERM_OFF_TIME = StringUtils.getConfigProp("RM.STATEINFO.TERM.OFF.TIME");
	
	
	private String exeSec00 = "00";
	private String exeSec10 = "10";
	
	public void RmTest() {
		
		String nowTime = "";
		
	}
	
	/**
	 * 시간별 상태정보 수신 체크
	 * @param map
	 * @param nowTime
	 */
	private void HourTerminalStateRcv(HashMap<String,String> map,String nowTime ) {
		String nextPridictTime="";
		String nextPridictMin="";
		String rsTerminalID="";
		
		if(!map.isEmpty()) {			
			log.debug("상태정보데이타 산출-------------------");		
			Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
			while(entries.hasNext()) {
				Map.Entry<String, String> entry = entries.next();
				
				log.debug(entry.getKey() + ":" +entry.getValue());
				rsTerminalID = entry.getKey();
				nextPridictTime = entry.getValue();
				nextPridictMin = entry.getValue().substring(8, 14);
				
				log.info( "상태정보 다음예상시간:" +rsTerminalID+":"+nextPridictMin);
				
				if(nowTime.equals(nextPridictMin)) {
					mariaService.HourTermStateCheck(rsTerminalID,nextPridictTime);
				}else {
					log.debug("현재는 시간아님"+entry.getKey() + ":" +entry.getValue());
				}
			}//while
		}
//			else {
//			log.debug("상태정보데이타 산출시간아님");
//		}
	}
	
	
	
	/**
	 * RM 프로세스 실행
	 */
	public void RmProcess() {
		//RmChk rmChk = new RmChk();
		
		String nowTime = "";
		String nowHour = "";
		String rmTime="";
				
		String rmMangTime1_00 = gRmMangTime1.trim()+exeSec00;
		String rmMangTime1_10 = gRmMangTime1.trim()+exeSec10;
		
		String rmMangTime2_00 = gRmMangTime2.trim()+exeSec00;
		String rmMangTime2_10 = gRmMangTime2.trim()+exeSec10;
		
		String rmProcTime_00 = gRmProcTime.trim()+exeSec00;
		String rmProcTime_10 = gRmProcTime.trim()+exeSec10;
				
		String rmNoRegShopTime_00 = gRmNoRegShopTime.trim()+exeSec00;
		String rmNoRegShopTime_10 = gRmNoRegShopTime.trim()+exeSec10;
		
		String rmNStateInfoTime_00 = gRmNStateInfoTime.trim()+exeSec00;
		String rmNStateInfoTime_10 = gRmNStateInfoTime.trim()+exeSec10;
		
		String rmStateInfoTermQTTime_00 = gRmStateInfoTermQTTime.trim()+exeSec00;
		
		
		String[] arr_gRMTblChkTime = gRMTblChkTime.split(",");
		
		//String rmStateTermInfoTime_10 = gRmNStateInfoTermTime.trim()+exeSec10;
		
		HashMap<String,String> map = new HashMap<String,String>();
		String stateChkTime= StringUtils.getConfigProp("RM.STATEINFO.TERM.TIME");
	    String[] arr_gRM_STATEINFO_TERM_OFF_TIME = gRM_STATEINFO_TERM_OFF_TIME.split(",");
		
		while(true) {		
			try {
				
				nowTime = mariaService.rtnDate("t");
				nowHour = nowTime.substring(0, 2);
				rmTime = nowTime.substring(2, 6); //분초
				
				System.out.println("시간:"+nowTime+"/"+rmTime);
								
				
				if(rmTime.equals(rmMangTime2_00)) {
					mariaService.MangCancelCNT();//망취소
				}
				if(rmTime.equals(rmProcTime_00)) {
					mariaService.AppProcessCheck(); //미응답 프로세스
				}
				if(rmTime.equals(rmNoRegShopTime_00)) {
					mariaService.NoShopApp(); //미등록 가맹점
				}				
				if(rmTime.equals(rmNStateInfoTime_00)) {
					mariaService.StateUpload(); //상태정보 수신
				}
				if(rmTime.equals(rmStateInfoTermQTTime_00)) {
					mariaService.StateQtInfo(); // QT 상태정보 수신 확인
				}

				//기기별 상태정보 수집 시간 산출-------------------------------------
				if(rmTime.equals(stateChkTime)) {
					if(!nowHour.equals(arr_gRM_STATEINFO_TERM_OFF_TIME[0]) && !nowHour.equals(arr_gRM_STATEINFO_TERM_OFF_TIME[1])) { 
					    map = mariaService.rtnTermStateNext();  //각 기기별 상태 체크 시간
					}else {
						 log.info("[{}]시는 체크 안함",nowHour);
					}//
					HourTerminalStateRcv(map,nowTime); //상태정보 미전송단말기 - 라인 위로 변경 ksmin
				}//				

				
				for(int i=0;i<arr_gRMTblChkTime.length;i++) {
					if(nowTime.equals(arr_gRMTblChkTime[i]+exeSec10)) {
						mariaService.DayTBLCheck();
					}else {
						//System.out.println("해당없슴:"+nowTime);							
					}//
				}//					
				
				Thread.sleep(1000L,0);
				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}//
	}
		

}
