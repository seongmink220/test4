package kr.co.ubcn.rm.mapper.mariaVmms;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ubcn.rm.model.HourTermStateCheckVO;
import kr.co.ubcn.rm.model.StateUploadVO;

@Mapper
public interface MariaVmmsMapper {
	
	public ArrayList<StateUploadVO> StateUpload(String stIND_DT, String endIND_DT, String rmPropCnt, String strToday);
	public ArrayList<HourTermStateCheckVO> HourTermStateCheck(String terminal, String stIND_DT, String endIND_DT);
	public String StateQtInfo(String rmTerminalID, String stIND_DT, String endIND_DT);
	public String rtnTermStateNext(String rmTerminalArr,String stIND_DT);
	
}
