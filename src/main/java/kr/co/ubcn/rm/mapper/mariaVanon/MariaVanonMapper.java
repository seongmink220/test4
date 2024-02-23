package kr.co.ubcn.rm.mapper.mariaVanon;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import kr.co.ubcn.rm.model.RmNoRegShopTimeVO;

@Mapper
public interface MariaVanonMapper {
	
	public int MangCancelCNT(String stUNIQUENO, String endUNIQUENO, String stTIME, String endTIME, String strToday);
	public ArrayList<String> AppProcessCheck(String stUNIQUENO, String endUNIQUENO, String stIND_DT, String endIND_DT, String strToday);
	public ArrayList<RmNoRegShopTimeVO> NoShopApp(String stUNIQUENO, String endUNIQUENO, String stIND_DT, String endIND_DT, String rmNoShopPropCnt, String strToday);
	public int DayTBLCheck1(String nextDay);
	public int DayTBLCheck2(String nextDay);
	
}
