package kr.co.ubcn.rm.rmchk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 테스트용 파일 비교 시스템
 * 
 * @author UBCN
 *
 */
@Slf4j
@Service
public class ReadFile {
	
	public void fileRead() {
		
		try {
				//원본 파일읽기
				String orgSTR = fileRead2();
				
				String[] arrOrg= orgSTR.split(",");
				
				//for(int i=0; i<arrOrg.length;i++) {
				//	log.info(String.format("%d,%s",i,arrOrg[i]));
				//}
				
				System.out.println("여기나와요??/");
				
				//비교파일
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\CGV\\CLOSING_20211129.fin");
				File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\CGV\\CLOSING_20211129.dat");
				
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\LTC\\SALES_20211129");
				
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ\\63\\CLOSING_20211129.txt.fin");
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ\\ILSAN\\CLOSING_20211129.txt.fin");
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ\\JEJU\\CLOSING_20211129.txt.fin");
				
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\KTD\\"+files);
				
			
				//FileReader filereader = new FileReader(file);
				
				//BufferedReader bufReader = new BufferedReader(filereader);
				
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"euc-kr"));
				
				String line;
				int num=1;
				while((line = bufReader.readLine()) != null) {
					//log.info(String.format("%d,%s",num,line));
					
					for(int i=0; i<arrOrg.length;i++) {
						if(line.equals((arrOrg[i]))) {
							log.info(String.format("같음:%d,%s",i,line));
							break;
						}else {
							if(i==(arrOrg.length-1)) {
								log.info(String.format("다름:%d,%s",i,line));
							}//
						}//
					}//
					
					num++;
				}
				
				
				log.info(String.format("파일라인:Org- %d,차세대- %d",arrOrg.length,num-1));
				
				
				
				bufReader.close();
		
			
		}catch (FileNotFoundException e) {
            // TODO: handle exception
			System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
		
	}
	
	public String fileRead2() {
		StringBuffer sb = new StringBuffer();
		
		try {
			System.out.println("여기나와요??/");
			
			File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\CGV_ORA\\CLOSING_20211129.fin_ora");
			//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\LTC_ORA\\SALES_20211129.fin_ora");
			
			//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ_ORA\\63\\CLOSING_20211129.txt.fin");
			//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ_ORA\\ILSAN\\CLOSING_20211129.txt.fin");
			//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ_ORA\\JEJU\\CLOSING_20211129.txt.fin");
			
			
			
			//FileReader filereader = new FileReader(file);
			
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"euc-kr"));
			
			String line;
			int num=1;
			while((line = bufReader.readLine()) != null) {
				//log.info(String.format("%d,%s",num,line));
				
				sb.append(line);
				sb.append(",");
				
				num++;
			}
			
			bufReader.close();
		}catch (FileNotFoundException e) {
            // TODO: handle exception
			System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
		
		return sb.toString().substring(0,sb.toString().length()-1);
	}
	
	public void fileRead21() {
		
		try {
			String files = "";
			
			for(int j=1;j<59;j++) {
				files = String.format("UBCNIN%03d.20211129.ftp", j);
				
				log.info("파일이름:"+files);
			
				//원본 파일읽기
				String orgSTR = fileRead22(files);
				
				String[] arrOrg= orgSTR.split(",");
				
				//for(int i=0; i<arrOrg.length;i++) {
				//	log.info(String.format("%d,%s",i,arrOrg[i]));
				//}
				
				System.out.println("여기나와요??/");
				
				//비교파일
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\CGV\\CLOSING_20211129.fin");
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\LTC\\SALES_20211129");
				
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ\\63\\CLOSING_20211129.txt.fin");
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ\\ILSAN\\CLOSING_20211129.txt.fin");
				//File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\HAQ\\JEJU\\CLOSING_20211129.txt.fin");
				
				File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\KTD\\"+files);
				
			
				//FileReader filereader = new FileReader(file);
				
				//BufferedReader bufReader = new BufferedReader(filereader);
				
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"euc-kr"));
				
				String line;
				int num=1;
				while((line = bufReader.readLine()) != null) {
					//log.info(String.format("%d,%s",num,line));
					
					for(int i=0; i<arrOrg.length;i++) {
						if(line.equals((arrOrg[i]))) {
							log.info(String.format("같음:%d,%s",i,line));
							break;
						}else {
							if(i==(arrOrg.length-1)) {
								log.info(String.format("다름:%d,%s",i,line));
							}//
						}//
					}//
					
					num++;
				}
				
				
				log.info(String.format("파일라인:Org- %d,차세대- %d",arrOrg.length,num-1));
				
				
				
				bufReader.close();
			}//for
			
		}catch (FileNotFoundException e) {
            // TODO: handle exception
			System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
		
	}
	
	public String fileRead22(String files) {
		StringBuffer sb = new StringBuffer();
		
		try {
			System.out.println("여기나와요??/");
			
			File file = new File("D:\\업무\\차세대자판기결제시스템\\외부연동파일분석\\20211202_파일분석\\KTD_ORA\\"+files);
			
			//FileReader filereader = new FileReader(file);
			
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"euc-kr"));
			
			String line;
			int num=1;
			while((line = bufReader.readLine()) != null) {
				//log.info(String.format("%d,%s",num,line));
				
				sb.append(line);
				sb.append(",");
				
				num++;
			}
			
			bufReader.close();
		}catch (FileNotFoundException e) {
            // TODO: handle exception
			System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
		
		return sb.toString().substring(0,sb.toString().length()-1);
	}

}
