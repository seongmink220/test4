package kr.co.ubcn.rm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import kr.co.ubcn.rm.rmchk.ReadFile;
import kr.co.ubcn.rm.rmchk.RmChkProc;

/*
@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
public class RmMgmt1Application {
	
	public static void main(String[] args) {
		SpringApplication.run(RmMgmt1Application.class, args);
		
		//RmChkProc chk = new RmChkProc();
		//chk.test();
		//rmChk.StateUpload();
	}
}*/
@SpringBootApplication
public class RmMgmt1Application implements CommandLineRunner{
	
	@Autowired
	private RmChkProc rmChk;
	
//	@Autowired
//	private ReadFile rf;
	
	public static void main(String[] args) {
		SpringApplication.run(RmMgmt1Application.class, args);
		
	}
	
	@Override
	public void run(String... args) throws Exception {
		//rmChk.RmTest();
		rmChk.RmProcess();
		
	}

}



